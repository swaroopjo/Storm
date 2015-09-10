package com.lio.markit.topology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lio.markit.jobs.TopologyRunnerJob;
import com.lio.markit.util.ContextInitializer;


/**
 * Topology Initializer. 
 * 
 * */
public class MarkitStockTopology {

	private static final Logger logger = LoggerFactory
			.getLogger(MarkitStockTopology.class);
	
	
	
	public static void main(String[] args){
		
		ContextInitializer initializer = new ContextInitializer(); 
		TopologyRunnerJob runner = new TopologyRunnerJob();
		runner.runTopology();
	}
}
