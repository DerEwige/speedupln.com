package fr.acinq.AmbossHealth;


import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.acinq.eclair.EclairImpl;
import fr.acinq.eclair.Kit;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import scodec.bits.ByteVector;

public class AmbosHealth_AmbossHealth implements Runnable  {
	final static Logger logger = LoggerFactory.getLogger(AmbosHealth_AmbossHealth.class);

	private EclairImpl eclair;

	AmbosHealth_AmbossHealth(Kit kit)
	{
		this.eclair = new EclairImpl(kit);
	}
	
	
	@Override
	public void run() {
	
		
		while(true)
		{	
			try {
				
				String now = Instant.now().toString();
				String nowBase64 = Base64.getEncoder().encodeToString(now.getBytes());
				

				String Signature = eclair.signMessage(ByteVector.fromBase64(nowBase64, ByteVector.fromValidBase64$default$2()).get()).signature().toHex();
						
				String JSON = " {\"query\": \"mutation HealthCheck($signature: String!, $timestamp: String!) { healthCheck(signature: $signature, timestamp: $timestamp) }\", \"variables\": {\"signature\": \""+Signature+"\", \"timestamp\": \""+now+"\"}}";

				logger.info(JSON);

				Request.Builder Builder = new Request.Builder()
							  .url("https://api.amboss.space/graphql")
							  .addHeader("Content-Type", "application/json");
					
				MediaType mediaType = MediaType.parse("application/json");
				RequestBody body = RequestBody.create(JSON, mediaType);
				Request request = Builder.post(body).build();
					
				OkHttpClient client = new OkHttpClient.Builder()
						    .connectTimeout(20, TimeUnit.SECONDS)
						    .writeTimeout(20, TimeUnit.SECONDS)
						    .readTimeout(60, TimeUnit.SECONDS)
						    .build();	
					
				String Result;
	
				Response response = client.newCall(request).execute();
				Result = response.toString();	

					
				logger.info(Result);
				

				
			} catch (Throwable e) {
				logger.error("Exception: "+this.getClass()+" 1 ",e);
			}
			
			try {
				TimeUnit.MINUTES.sleep(5);
			} catch (Throwable e) {
				logger.error("Exception: "+this.getClass()+" 2 ",e);
			}

		}
	}
}
