package fr.acinq.ChannelInterceptor;


import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.Behaviors;
import fr.acinq.eclair.InterceptOpenChannelCommand;
import fr.acinq.eclair.InterceptOpenChannelPlugin;
import fr.acinq.eclair.Kit;
import fr.acinq.eclair.NodeParams;
import fr.acinq.eclair.Plugin;
import fr.acinq.eclair.PluginParams;
import fr.acinq.eclair.Setup;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Intercept OpenChannel messages received by the node and respond by continuing the process
 * of accepting the request, potentially with different local parameters, or failing the request.
 */
public class ChannelFundingPlugin implements Plugin {
	final static Logger logger = LoggerFactory.getLogger(ChannelFundingPlugin.class);

    private OpenChannelInterceptorKit pluginKit;
    private File configFile;

    @Override
    public PluginParams params() {
        return new InterceptOpenChannelPlugin() {
            @Override
            public String name() {
                return "ChannelFundingPlugin";
            }

            @Override
            public ActorRef<InterceptOpenChannelCommand> openChannelInterceptor() {
                return pluginKit.openChannelInterceptor();
            }
        };
    }

    @Override
    public void onSetup(Setup setup) {
    	File datadir = new File(setup.datadir().toPath().toAbsolutePath().toString());
		File resourcesDir = new File(datadir,"/plugin-resources/ChannelFunding/");
		configFile = new File(resourcesDir,"ChannelFunding.conf");
		logger.info("Using config file: "+configFile.getAbsolutePath());
		
		//we load the config once at startup to check if at least hte default accept rule is set
		try {
        	Config PluginConf = ConfigFactory.parseFile(configFile).resolve();
        	
        	@SuppressWarnings("unused")
            boolean accept = PluginConf.getBoolean("open-channel-interceptor.default.accept");
        	@SuppressWarnings("unused")
			long min_channel_size = PluginConf.getLong("open-channel-interceptor.default.min_channel_size");
        	@SuppressWarnings("unused")
        	long max_channel_size = PluginConf.getLong("open-channel-interceptor.default.max_channel_size");
        	@SuppressWarnings("unused")
        	int	min_active_channels = PluginConf.getInt("open-channel-interceptor.default.min_active_channels");
		} catch (Throwable e)
		{
			logger.error("Can not read default accept rules from config file");
			logger.error(""+e);
		}
			
		logger.info("ChannelFundingPlugin finished startup");
    }

    @Override
    public void onKit(Kit kit) {

        ActorSystem actorSystem = kit.system();
        Behavior<InterceptOpenChannelCommand> MyBehaviour = Behaviors.supervise(OpenChannelInterceptor.apply(configFile.getAbsolutePath(), Adapter.toTyped(kit.router())))
                .onFailure(SupervisorStrategy.restart());
        
        akka.actor.typed.ActorRef<InterceptOpenChannelCommand> openChannelInterceptor = Adapter.spawnAnonymous(actorSystem, MyBehaviour);
 
        
        pluginKit = new OpenChannelInterceptorKit(kit.nodeParams(), kit.system(), openChannelInterceptor);
        
        logger.info("ChannelFundingPlugin subscribed to events");
    }

}

class OpenChannelInterceptorKit {
    private final NodeParams nodeParams;
    private final ActorSystem system;
    private final ActorRef<InterceptOpenChannelCommand> openChannelInterceptor;

    public OpenChannelInterceptorKit(NodeParams nodeParams, ActorSystem system, ActorRef<InterceptOpenChannelCommand> openChannelInterceptor) {
        this.nodeParams = nodeParams;
        this.system = system;
        this.openChannelInterceptor = openChannelInterceptor;
    }

    public NodeParams nodeParams() {
        return nodeParams;
    }

    public ActorSystem system() {
        return system;
    }

    public ActorRef<InterceptOpenChannelCommand> openChannelInterceptor() {
        return openChannelInterceptor;
    }
}
