package com.lio.markit.jobs;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

import com.lio.markit.bolt.MarketDataExporterBolt;
import com.lio.markit.db.MarketDao;
import com.lio.markit.spout.MarketDataImporterSpout;
import com.lio.markit.topology.MarkitStockTopology;
import com.lio.markit.util.ContextInitializer;

public class TopologyRunnerJob {
	
	private static final Logger logger = LoggerFactory
			.getLogger(TopologyRunnerJob.class);

	public void runTopology(){
		
		logger.info("Starting TopologyRunner Job "+new Date());
		// This will initialize all the beans, and the scheduler.
		@SuppressWarnings("resource")
		final
		ApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
		final MarketDao dao =(MarketDao) ContextInitializer.getBean("marketDao");
		dao.resetJobCompletion(); 
		logger.info("Job Completion Resetting");
		
		
		final MarketDataImporterSpout marketDataImporterSpout = ctx.getBean(MarketDataImporterSpout.class);
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("markit-data-import-spout",marketDataImporterSpout);
		
		builder.setBolt("markit-data-export-bolt", new MarketDataExporterBolt()).shuffleGrouping("markit-data-import-spout");
		
		Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, builder.createTopology());
        
        
        
        
        
//	        new Thread(new Runnable(){
//	        	
//	        	
//	        	
//				@Override
//				public void run() {
//					while(true){
//						
//						//Sleep here because sometimes, the cluster may not have been started and the code tries to kill it. 
//						try {
//							Thread.sleep(3000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						// If the Process has been completed by the spout then the cluster needs to be closed. 
//						if(dao.isWSJobCompleted() == true)
//						{
//							cluster.killTopology("test");
//							cluster.shutdown();
//							logger.info("Job has been completed. Shutting down cluster");
//							break;
//						}
//						
//						}
//						
//				}
//	        	
//	        }).start();;
	}
}
