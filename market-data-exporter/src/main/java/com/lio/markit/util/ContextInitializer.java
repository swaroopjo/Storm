package com.lio.markit.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextInitializer {

	
	public static ApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
	
	public static Object getBean(String beanName){
		return ctx.getBean(beanName);
	}
	
}
