package com.lio.markit.db;

import java.io.Serializable;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.lio.markit.aop.DbAccess;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MarketDaoImpl implements MarketDao{

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(MarketDaoImpl.class);
	
	private MongoTemplate mongoTemplate = null;
	private MongoClient mClient = null;
	private MongoDbFactory factory = null;
	
	public void openConnection(){
		
		try {
			mClient = new MongoClient(); 
			 factory = new SimpleMongoDbFactory(mClient,"stockmarketfeed");
			this.mongoTemplate = new MongoTemplate(factory);
			
		} catch (UnknownHostException e) {
			logger.error("Mongo Connection could not be established. "+e.getMessage());
		}
		
	}
	public void closeConnection(){
		mClient.close();
	}
	
	@DbAccess
	public void prepareDatabase(){
		
		openConnection();
		
		if(!mongoTemplate.collectionExists("MARKITFEED")){
			mongoTemplate.createCollection("MARKITFEED");
		}
		if(!mongoTemplate.collectionExists("JOBCOMPLETION")){
			mongoTemplate.createCollection("JOBCOMPLETION");
			JSONObject jsonObject = new JSONObject(); 
			jsonObject.put("process_complete", false);
			mongoTemplate.insert(jsonObject,"JOBCOMPLETION");
		}
		if(!mongoTemplate.collectionExists("CONSOLIDATEDVIEW")){
			mongoTemplate.createCollection("CONSOLIDATEDVIEW");
		}
		closeConnection();
	}
	@DbAccess
	public void saveJson(JSONObject json){
		openConnection();
		if(!mongoTemplate.collectionExists("MARKITFEED")){
			mongoTemplate.createCollection("MARKITFEED");
		}
		mongoTemplate.save(json);
		closeConnection();
	}
	
	@DbAccess
	public boolean isWSJobCompleted(){
		openConnection();
		DBObject object = mongoTemplate.getCollection("JOBCOMPLETION").findOne();
		boolean isCompleted = false;
		if(object.get("process_complete").toString().equalsIgnoreCase("true")){
			isCompleted = true;
		}
//		if((Boolean)mongoTemplate.getCollection("JOBCOMPLETION").findOne().get("process_complete") == true){
//			isCompleted = true;
//		}
		closeConnection();
		return isCompleted;
	}
	
	@DbAccess
	public void reportJobCompletion(){
		openConnection();
		DBObject object = mongoTemplate.getCollection("JOBCOMPLETION").findOne();
		object.put("process_complete", true);
		mongoTemplate.getCollection("JOBCOMPLETION").update(mongoTemplate.getCollection("JOBCOMPLETION").findOne(),object );
		closeConnection();
	}
	
	
	@DbAccess
	public void consolidateFeedData(String symbol){
		openConnection();
		 Query searchStockQuery = new Query(Criteria.where("Symbol").is(symbol));
		
		 	JSONObject jsonObject = new JSONObject(); 
			final JSONObject timeValPair = new JSONObject(); 
			final JSONArray arrayOfKeyValues = new JSONArray(); 
			
		 mongoTemplate.executeQuery(searchStockQuery, "MARKITFEED", new DocumentCallbackHandler(){
			 
			 
			@Override
			public void processDocument(DBObject dbObject)
					throws MongoException, DataAccessException {
				if(!mongoTemplate.collectionExists("CONSOLIDATEDVIEW")){
					mongoTemplate.createCollection("CONSOLIDATEDVIEW");
					
				}
				timeValPair.put(dbObject.get("Timestamp"), dbObject);
				arrayOfKeyValues.add(timeValPair);
				
				
			}
			 
		 });
		 jsonObject.put(symbol, arrayOfKeyValues);
		 mongoTemplate.insert(jsonObject, "CONSOLIDATEDVIEW");
		closeConnection();
		
	}
	@DbAccess
	public void saveDocument(JSONObject json,String collectionName){
		openConnection();
		mongoTemplate.save(json, "MARKITFEED");
		closeConnection();
	}
	@DbAccess
	public void resetJobCompletion() {
		openConnection();
		DBObject object = mongoTemplate.getCollection("JOBCOMPLETION").findOne();
		object.put("process_complete", false);
		mongoTemplate.getCollection("JOBCOMPLETION").update(mongoTemplate.getCollection("JOBCOMPLETION").findOne(),object );
		closeConnection();
		
	}
}
