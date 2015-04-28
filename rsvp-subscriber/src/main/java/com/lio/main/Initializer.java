package com.lio.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lio.topology.FacebookTopology;

public class Initializer {

	public static void main(String[] args){
		FacebookTopology topology = new ClassPathXmlApplicationContext("rabbit-listener-context.xml").getBean(FacebookTopology.class);
		topology.buildTopology();
	}
	
}
