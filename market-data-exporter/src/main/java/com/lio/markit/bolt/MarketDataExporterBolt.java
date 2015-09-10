package com.lio.markit.bolt;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.lio.markit.db.MarketDao;
import com.lio.markit.util.ContextInitializer;
import com.mongodb.MongoClient;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class MarketDataExporterBolt  extends BaseRichBolt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OutputCollector collector;
	
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(MarketDataExporterBolt.class);
	

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;

	}

	public void execute(Tuple input) {
		
		//String symbol = (String) input.getValue(0);
		String response = (String) input.getValue(1);
		MarketDao dao = (MarketDao)ContextInitializer.getBean("marketDao");
		
		
			JSONObject json;
			try {
				json = (JSONObject)new JSONParser().parse(response);
				if(json.get("Status") != null && json.get("Status").equals("SUCCESS")){
					logger.info(json.toString());
				//	Thread.sleep(1000);
					dao.saveDocument(json, "MARKITFEED");
				}
			}catch (Exception e) {
				logger.error("Error occured while storing the json to Mongo collection");
			}
			
			collector.ack(input);
		} 
		

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
