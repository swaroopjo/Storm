package com.lio.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

import com.lio.listener.FacebookBolt;
import com.lio.listener.FacebookListenerSpout;

public class FacebookTopology {

	
	private FacebookBolt fbBolt; 
	private FacebookListenerSpout fbSpout;
	
	
	public void buildTopology(){
		TopologyBuilder builder = new TopologyBuilder();
		System.out.println("Building Topology");
        builder.setSpout( "spout", fbSpout );
        builder.setBolt( "prime", fbBolt )
                .shuffleGrouping("spout");


        Config conf = new Config();
        
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, builder.createTopology());
        Utils.sleep(10000);
        //cluster.killTopology("test");
        //cluster.shutdown();
	}


	public FacebookBolt getFbBolt() {
		return fbBolt;
	}


	public void setFbBolt(FacebookBolt fbBolt) {
		this.fbBolt = fbBolt;
	}


	public FacebookListenerSpout getFbSpout() {
		return fbSpout;
	}


	public void setFbSpout(FacebookListenerSpout fbSpout) {
		this.fbSpout = fbSpout;
	}

}
