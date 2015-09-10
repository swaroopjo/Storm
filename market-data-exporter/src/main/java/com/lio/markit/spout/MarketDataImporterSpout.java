package com.lio.markit.spout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.thrift7.TException;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.lio.markit.db.MarketDao;
import com.lio.markit.util.ContextInitializer;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import backtype.storm.LocalCluster;
import backtype.storm.generated.KillOptions;
import backtype.storm.generated.Nimbus.Client;
import backtype.storm.generated.NotAliveException;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.Utils;

/**
 * MarketDataImporterSpout.java
 * */
public class MarketDataImporterSpout extends BaseRichSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(MarketDataImporterSpout.class);
	
	private SpoutOutputCollector collector;
	ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(500);

	private String server;
	private Map configuration; 
	
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		this.configuration = conf;

	}
	/**
	 * Called by the Storm as long as user dont kill the process. 
	 * */
	public void nextTuple() {

		MarketDao dao = (MarketDao) (ContextInitializer.getBean("marketDao"));
		
		LocalDateTime ldt = new LocalDateTime(); 
		if(ldt.getHourOfDay() >17 && ldt.getHourOfDay() < 8 &&
				ldt.getDayOfWeek() == 6 && ldt.getDayOfWeek() == 7){
			try {
				logger.info("Stock Market Timing has closed. ");
				Thread.sleep(350000);
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		InputStream stream = MarketDataImporterSpout.class.getResourceAsStream("/symbols.txt");
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader breader = new BufferedReader(reader);
		
		try{
			while(breader.ready()){
				String symbol = breader.readLine().trim();
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet("http://" + server
						+ "/Api/v2/Quote/json?symbol="
						+ symbol );
				HttpResponse response;
				
				response = client.execute(get);
				StatusLine status = response.getStatusLine();
				if (status.getStatusCode() == 200) {
					InputStream responseStream = response.getEntity().getContent();
					BufferedReader responseReader = new BufferedReader(
							new InputStreamReader(responseStream));
					String in;
					// Read line by line
					while ((in = responseReader.readLine()) != null) {
						try {
							collector.emit(new Values(symbol,in));
						} catch (Exception e) {
							logger.error("Could not read from Lines server. Connection would have been lost. "+e.getMessage());
						}
					}
				}
				
			}
			dao.reportJobCompletion();
			try{
				logger.info("Process has been completed. Wait for 5 minutes to start the process again.");
				Thread.sleep(350000);
			}
			catch(InterruptedException ie){
				logger.error("Cluster might have been shutdown which made the thread being interrupted");
			}
		}
		catch(Exception e){
			logger.error("Application would have been stopped"+e.getMessage());
			// Notify User if necessary using Spring AOP
			
		}

		}
	



	public void declareOutputFields(OutputFieldsDeclarer declarer) {

		declarer.declare(new Fields("symbol","json"));
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}


}
