package com.lio.markit.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class MongoDBAspect {
	
	private static final Logger logger = LoggerFactory
			.getLogger(MongoDBAspect.class);
	@Around("@annotation(DbAccess)")
	public void performTransaction(ProceedingJoinPoint joinPoint){
		int maxRetry = 10;
		logger.info("Executing advice");
		try {
			joinPoint.proceed();
		} catch (Throwable e) {
			logger.error("mongoDB connection Failed for the first time."+ e.getMessage());
			boolean success = false;
			while(maxRetry !=1 && !success){
				logger.info(" Retrying: "+(maxRetry-1));
				maxRetry = maxRetry-1;
				try {
					joinPoint.proceed();
					success = true;
				} catch (Throwable e1) {
					logger.error("mongoDB connection Failed again"+ e1.getMessage());
				}
			}
			
		}
	}

}
