package com.naah.common.jdk18.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JDKERRor{
	
	
	public static void main(String[] args) {
		testPackageNumNPE();
	}
	
	public static void testPackageNumNPE() {
		Integer a = 1; 
		Integer b = 1;
		
		System.out.println(a==b);
		
		Integer c = 1227;
		Integer d = 1227;

		System.out.println(c==d);
		
		System.out.println(c.equals(d));
		
		
		List<String> list = Arrays.asList("a","b","c","d");
		//list.add("F");
		
		List<String> listQ = new ArrayList<String>();
		listQ.addAll(list);
		listQ.add("F");
		
		for (String string : listQ) {
			listQ.remove(string);
		}
 	}
}