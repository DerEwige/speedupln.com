package fr.acinq.AmbossHealth;



import fr.acinq.eclair.Kit;
import fr.acinq.eclair.Plugin;
import fr.acinq.eclair.PluginParams;

import fr.acinq.eclair.Setup;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class AmbosHealth_Plugin implements Plugin  {
	final static Logger logger = LoggerFactory.getLogger(AmbosHealth_Plugin.class);

	public static void main(String[] args)
	{


	}

	@Override
	public void onKit(Kit kit) {
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
		AmbosHealth_AmbossHealth AmbossHealth = new AmbosHealth_AmbossHealth(kit);
		executor = Executors.newFixedThreadPool(1);
		executor.execute(AmbossHealth);

	}

	@Override
	public void onSetup(Setup setup) {
		logger.info("AmbossHealthPing finished startup");
	}

	@Override
	public PluginParams params() {
		PluginParams myParams = new PluginParams() {
			
			@Override
			public String name() {

				return "AmbossHealthPing";
			}
		};
		return myParams;
	}
	

}
