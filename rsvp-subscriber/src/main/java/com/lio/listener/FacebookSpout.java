package com.lio.listener;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class FacebookSpout extends FacebookAbstract{

	private SpoutOutputCollector collector;
	
	private FacebookListenerSpout listener = (FacebookListenerSpout) new ClassPathXmlApplicationContext("rabbit-listener-context.xml").getBean("rsvp-fb-listener");
	
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		
	}

	@Override
	public void nextTuple() {
		
//		if(listener.getQueue().size() > 0){
//			String message = queue.poll();
//			collector.emit(new Values(message));
//		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		declarer.declare(new Fields("json"));
	}
	

}
