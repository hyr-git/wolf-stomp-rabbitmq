package com.naah.common.dynamicproxy.jdkproxy;

/***
 * 被代理对象
 * @author mlj
 *
 */
public class Student implements People{

	@Override
	public void eat() {
       System.out.println("Student--------eat 开始吃东西");		
	}
	
}
