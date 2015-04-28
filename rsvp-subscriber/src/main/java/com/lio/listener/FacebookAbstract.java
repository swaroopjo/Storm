package com.lio.listener;

import java.util.concurrent.ArrayBlockingQueue;

import backtype.storm.topology.base.BaseRichSpout;

public abstract class FacebookAbstract extends BaseRichSpout{

	ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(500);
	public FacebookAbstract(){
		
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					queue.add("Helloworld"+Math.random());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}});
		
		thread.start();
		
	}
}
