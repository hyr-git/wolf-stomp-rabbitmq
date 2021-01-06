package com.naah.common.dynamicproxy.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/***
 * 增强的类,对目标对的一个方法进行增强
 * @author mlj
 *
 */
public class StudentAdvice implements InvocationHandler{
	
	private People people;

	public StudentAdvice(People people){
		this.people = people;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("前置方法-----------------------------");
		Object object = method.invoke(people, args);
		System.out.println("后置方法-----------------------------");
		return object;

	}

	
}
