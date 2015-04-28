package com.lio.listener;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class FacebookListenerSpout extends BaseRichSpout implements MessageListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(FacebookListenerSpout.class);
	
	private SpoutOutputCollector collector;
	
	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
	
	public FacebookListenerSpout(){
		logger.info("Listner Initialized. Listening on rsvpNJQueue");
	}
	
	
		/**
		 * Called when the message is recieved.
		 * */
		public synchronized void onMessage(Message message) {
			
			String messageBody= new String(message.getBody());
			logger.info("Listener received message----->"+messageBody);
			
				try {
						queue.put(new String(message.getBody()));
						//Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			this.collector = collector;
			
		}

		@Override
		public synchronized void  nextTuple() {
			
					String message;
					try {
						message = queue.take();
					collector.emit(new Values(message));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			
			
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("json"));
			
		}

		
}
