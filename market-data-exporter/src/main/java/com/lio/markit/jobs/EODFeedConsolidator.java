package com.lio.markit.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.lio.markit.db.MarketDao;
import com.lio.markit.util.ContextInitializer;

public class EODFeedConsolidator {

	private static final Logger logger = LoggerFactory
			.getLogger(EODFeedConsolidator.class);
	
	private MongoTemplate template;

	public MongoTemplate getTemplate() {
		return template;
	}

	public void setTemplate(MongoTemplate template) {
		this.template = template;
	}
	

	public void consolidateFeedData(){
		logger.info("Scheduler has been started...");
		
		if(!template.collectionExists("MARKITFEED")){
			logger.info("Collection does not exists. Could be that the previous job had flused it");
			return; 
		}
		
		InputStream stream = EODFeedConsolidator.class.getResourceAsStream("/symbols.txt");
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader breader = new BufferedReader(reader);
		String symbol = "";
		
		try {
			MarketDao dao = (MarketDao) (ContextInitializer.getBean("marketDao"));
			while(breader.ready()){
				 symbol = breader.readLine().trim();
				 dao.consolidateFeedData(symbol);
			}
		} catch (IOException e) {
			logger.error("Error while Revising the Objects to Mongo. "+e.getMessage());
		}
		
	}
	
}
