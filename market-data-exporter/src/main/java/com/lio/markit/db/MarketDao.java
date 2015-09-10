package com.lio.markit.db;


import org.json.simple.JSONObject;


public interface MarketDao{

	/**
	 * 
	 */

	
	public void openConnection();
	public void closeConnection();
	
	@SuppressWarnings("unchecked")
	public void prepareDatabase();
	public void saveJson(JSONObject json);
	
	public boolean isWSJobCompleted();
	
	public void reportJobCompletion();
	
	
	public void consolidateFeedData(String symbol);
	public void saveDocument(JSONObject json,String collectionName);
	public void resetJobCompletion() ;
}
