package com.lio.markit.util;

import org.joda.time.LocalDateTime;

public class JodaTimeTest {

	
	public static void main(String[] args){
		LocalDateTime ldt = new LocalDateTime(); 
		System.out.println(ldt.getMonthOfYear()+"-"+ldt.getDayOfMonth()+"-"+ldt.getYear());
		
	}
}
