package com.example.bookclub.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class AspectLog {
	@Pointcut("execution(* com.example.bookclub.controller.*Controller.*(..))")
	public void controllerPointCut() {}

	@Pointcut("execution(* com.example.bookclub.controller.api.*Controller.*(..))")
	public void controllerApiPointCut() {}

	@Pointcut("execution(* com.example.bookclub.application.*Service.*(..))")
	public void servicePointCut() {}

	@Before("controllerPointCut() || controllerApiPointCut() || servicePointCut()")
	public void logBeforeControllerAndService(JoinPoint.StaticPart jpsp) {
		log.info("{}.{}",
				jpsp.getSignature().getDeclaringType().getName(), jpsp.getSignature().getName());
	}

	@Around("servicePointCut()")
	public Object logAroundServiceImpl(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch sw = new StopWatch();
		sw.start();
		Object result = joinPoint.proceed();
		sw.stop();
		log.info("MethodName = {}, Service Time : {} ", joinPoint.getSignature().getName(), sw.getTotalTimeSeconds());

		return result;
	}
}
