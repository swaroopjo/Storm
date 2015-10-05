package com.lio.markit.db;

import java.io.Serializable;
import java.net.UnknownHostException;

import org.bson.types.BasicBSONList;
import org.joda.time.LocalDateTime;
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
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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
	public void consolidateFeedData(final String symbol){
		openConnection();
		 LocalDateTime ldt = new LocalDateTime(); 
		 
		 String cvTable = "CONSVIEW"+"-"+ldt.getMonthOfYear()+"-"+ldt.getDayOfMonth()+"-"+ldt.getYear();
			
		DB db = mClient.getDB("stockmarketfeed");
		DBCollection collection = db.getCollection("MARKITFEED");
		BasicDBObject query = new BasicDBObject("Symbol",symbol);
		
		DBCursor cursor = collection.find(query); 
		
		while(cursor.hasNext()){
			DBObject feedObject = cursor.next();
			
			DBCollection cvCollection = db.getCollection(cvTable);
			
			DBCursor cvCursor = cvCollection.find(new BasicDBObject("Symbol",symbol));
			boolean recordExists = false; 
			
			 JSONObject timeValPair = new JSONObject(); 
			 JSONArray arrayOfKeyValues = new JSONArray(); 
			 JSONObject jsonObject = new JSONObject(); 
			 boolean hasRecord = false; 
			 
			if(cvCursor.size() > 0){
				hasRecord = true;
				while(cvCursor.hasNext()){
					
					DBObject cvObject = cvCursor.next();
					
					
					BasicDBList objArray = (BasicDBList)cvObject.get(symbol);
					for(Object indObject:objArray){
						DBObject x = (DBObject)indObject;
						arrayOfKeyValues.add(x);
						if(feedObject.get("Timestamp").equals(x.get("Timestamp"))){
							
							recordExists = true;
						}
					}
					
				}
				if(!recordExists){
					timeValPair.put(feedObject.get("Timestamp"), feedObject);
					timeValPair.put("Timestamp", feedObject.get("Timestamp"));
					
					arrayOfKeyValues.add(timeValPair);
					
					cvCollection.remove(new BasicDBObject("Symbol", symbol));
					jsonObject.put(symbol, arrayOfKeyValues);
					jsonObject.put("Symbol",symbol);
					mongoTemplate.insert(jsonObject,cvTable);
					return;
				}
				
			}
			
			if(hasRecord == false){
				timeValPair.put(feedObject.get("Timestamp"), feedObject);
				timeValPair.put("Timestamp", feedObject.get("Timestamp"));
				
				arrayOfKeyValues.add(timeValPair);
				
				jsonObject.put(symbol, arrayOfKeyValues);
				jsonObject.put("Symbol",symbol);
				
				mongoTemplate.insert(jsonObject,cvTable);
			}
			
		
			
			
			
		}
		
		
		 
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
