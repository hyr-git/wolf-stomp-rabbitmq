package com.naah.common.dynamicproxy.jdkproxy;

import java.lang.reflect.Proxy;

public class Jdkproxy {

	public static void main(String[] args) {
		//peopleProxy是People类的一个代理对象, Student是一个被代理对象
		People peopleProxy = (People) Proxy.newProxyInstance(Jdkproxy.class.getClassLoader(), 
				new Class[]{People.class}, new StudentAdvice(new Student()));
		peopleProxy.eat();
		
		Integer i1 =new Integer(128);
		Integer i2 =128 ;//=new Integer(100);
		Integer i3 =128 ;//=new Integer(100);
		System.out.println(i1.hashCode()+"-----"+i1.intValue());
		System.out.println(i2.hashCode()+"-----"+i1.intValue());
		System.out.println(i3.hashCode()+"-----"+i3.intValue());


		System.out.println(i3-i2);
		
		System.out.println(i3==i2);

		System.out.println(i1.intValue()==i2.intValue());
		
		String sg = "001srt";
		String sgg = new 	String("001srt");
		System.out.println(sg.hashCode());
		System.out.println(sgg.hashCode());
        System.out.println(sgg==sg);	
        System.out.println(sgg.equals(sgg));	
}
}
