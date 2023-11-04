package fr.acinq.ChannelInterceptor;

import akka.actor.typed.Behavior;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import fr.acinq.eclair.router.Router;
import fr.acinq.eclair.router.Router.GetNode;
import fr.acinq.eclair.router.Router.PublicNode;
import fr.acinq.eclair.router.Router.UnknownNode;
import fr.acinq.eclair.wire.protocol.Error;
import fr.acinq.eclair.AcceptOpenChannel;
import fr.acinq.eclair.InterceptOpenChannelCommand;
import fr.acinq.eclair.InterceptOpenChannelReceived;
import fr.acinq.eclair.RejectOpenChannel;

/**
 * Intercept OpenChannel and OpenDualFundedChannel messages received by the node. Respond to the peer
 * that received the request with AcceptOpenChannel to continue the open channel process,
 * optionally with modified default parameters, or fail the request by responding to the initiator
 * with RejectOpenChannel and an Error message.
 *
 * This example plugin decides how much funds (if any) the non-initiator should put into a dual-funded channel. It also
 * demonstrates how to reject requests from nodes with less than a minimum amount of total capacity or too few public
 * channels.
 *
 * Note: only one open channel request can be processed at a time.
 */
public class OpenChannelInterceptor {
  final static Logger logger = LoggerFactory.getLogger(OpenChannelInterceptor.class);
  

  
  private static class WrappedGetNodeResponse implements InterceptOpenChannelCommand {
    private final InterceptOpenChannelReceived interceptOpenChannelReceived;
    private final Router.GetNodeResponse response;

    public WrappedGetNodeResponse(InterceptOpenChannelReceived interceptOpenChannelReceived, Router.GetNodeResponse response) {
      this.interceptOpenChannelReceived = interceptOpenChannelReceived;
      this.response = response;
    }

    public InterceptOpenChannelReceived getInterceptOpenChannelReceived() {
      return interceptOpenChannelReceived;
    }

    public Router.GetNodeResponse getResponse() {
      return response;
    }
  }

  public static Behavior<InterceptOpenChannelCommand> apply(String configPath, ActorRef<Object> router) {
    return Behaviors.setup(context -> new OpenChannelInterceptor(configPath, router, context).start());
  }


  private final String ConfigPath;
  private final ActorRef<Object> router;
  private final ActorContext<InterceptOpenChannelCommand> context;

  private OpenChannelInterceptor(String ConfigPath, ActorRef<Object> router, ActorContext<InterceptOpenChannelCommand> context) {
	logger.info("Interceptor created");
    this.ConfigPath = ConfigPath;
    this.router = router;
    this.context = context;
  }

  private Behavior<InterceptOpenChannelCommand> start() {
    return Behaviors.receiveMessage(interceptOpenChannelCommand -> {
      if (interceptOpenChannelCommand instanceof InterceptOpenChannelReceived) {
        InterceptOpenChannelReceived o = (InterceptOpenChannelReceived) interceptOpenChannelCommand;
        ActorRef<Router.GetNodeResponse> adapter = context.messageAdapter(Router.GetNodeResponse.class, nodeResponse -> new WrappedGetNodeResponse(o, nodeResponse));
        router.tell(new GetNode(adapter, o.openChannelNonInitiator().remoteNodeId()));
        return start();
      } else if (interceptOpenChannelCommand instanceof WrappedGetNodeResponse) {
        WrappedGetNodeResponse wrappedGetNodeResponse = (WrappedGetNodeResponse) interceptOpenChannelCommand;
        InterceptOpenChannelReceived o = wrappedGetNodeResponse.getInterceptOpenChannelReceived();
        Router.GetNodeResponse response = wrappedGetNodeResponse.getResponse();
        
        logger.info("Node info: "+response);
        logger.debug("Channel request: "+o);
        
        try {
        	Config PluginConf = ConfigFactory.parseFile(new File(ConfigPath)).resolve();
            boolean accept = PluginConf.getBoolean("open-channel-interceptor.default.accept");
            long min_channel_size = PluginConf.getLong("open-channel-interceptor.default.min_channel_size");
            long max_channel_size = PluginConf.getLong("open-channel-interceptor.default.max_channel_size");
            int	min_active_channels = PluginConf.getInt("open-channel-interceptor.default.min_active_channels");
            
    		@SuppressWarnings("unchecked")
    		List<Config> Overrides = (List<Config>) PluginConf.getConfigList("open-channel-interceptor.override");
    		

            
            if (response instanceof PublicNode) {
              PublicNode publicNode = (PublicNode) response;

              logger.info("Got Request from: "+publicNode.announcement().nodeId());

              
    	  	  for(Config config : Overrides)
    	  	  {
    	  		  //we check if the Nodeid is on a override list, if so we overwrite the defaults
    	  		  List<String> NodeIds = config.getStringList("NodeIds");
    	  		  if(NodeIds.contains(publicNode.announcement().nodeId().toString()))
    	  		  {
    	  	         accept = config.getBoolean("accept");
    	  	         min_channel_size = config.getLong("min_channel_size");
    	  	         max_channel_size = config.getLong("max_channel_size");
    	  	         min_active_channels = config.getInt("min_active_channels");
    	  		  }
    	  	  }
              
              
              String error = "";
              if(!accept) {
            	  error = "rejected, not accepting new channels";
              } else
              {
                  if (publicNode.activeChannels() < min_active_channels) {
                	  error =  "rejected, less than " + min_active_channels + " active channels";
                  } 
                  if (o.remoteFundingAmount().toLong() < min_channel_size) {
                	  error = "rejected, min channel size " + min_channel_size;
                  }
                  if (o.remoteFundingAmount().toLong() > max_channel_size) {
                	  error = "rejected, max channel size " + max_channel_size;
                  } 
              }
              
              if(error.equals("")) {
            	logger.info("channel accepted");
            	acceptOpenChannel(o); 
              }else {
            	logger.info("channel rejected: "+error);
            	rejectOpenChannel(o, error);  
              }
              
            } else if (response instanceof UnknownNode) {
            	String error = "rejected, no public channels";
            	logger.info("channel rejected: "+error);
                rejectOpenChannel(o, error);
            } 
        } catch (Throwable e)
        {
        	//if anything goes wrong during checking of the request, we deny the request with a generic error and log the exception
        	logger.error("Exception: "+this.getClass()+" 1 ",e);
        	rejectOpenChannel(o, "rejected, internal error");
        }
        
        
        return start();
      } else {
        return Behaviors.same();
      }
    });
  }

  private void acceptOpenChannel(InterceptOpenChannelReceived o) {
    o.replyTo().tell(new AcceptOpenChannel(o.temporaryChannelId(), o.defaultParams()));
  }

  private void rejectOpenChannel(InterceptOpenChannelReceived o, String error) {
	  o.replyTo().tell(new RejectOpenChannel(o.temporaryChannelId(), Error.apply(o.temporaryChannelId(), error)));
  }
}
