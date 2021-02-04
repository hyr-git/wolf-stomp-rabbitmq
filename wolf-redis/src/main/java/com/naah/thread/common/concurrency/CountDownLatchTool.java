package com.naah.thread.common.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchTool {

	
	public static void main(String[] args) throws InterruptedException {
		int num = 3;
		ExecutorService execute = Executors.newFixedThreadPool(num);
		final CountDownLatch countDown = new CountDownLatch(num); 
		
		try {
			for (int i = 0; i < num; i++) {
				Runnable task = new Runnable() {
					@Override
					public void run() {
						System.out.println(Thread.currentThread().getName()+"子线程开始启动运行--------");
						try {
	                        Thread.sleep((long) (Math.random() * 10000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println(Thread.currentThread().getName()+"子线运行结束。。。。。。。");
						countDown.countDown();
					}
				};
				execute.execute(task);
			}
		} finally {
			//execute.shutdown();
		}
		
	   //使用main方法进行模拟主线程操作
	   System.out.println("主线程"+Thread.currentThread().getName()+"等待子线程执行完成...");
	   countDown.await();//阻塞当前线程，直到计数器的值为0
       System.out.println("主线程"+Thread.currentThread().getName()+"开始执行...");

	}
}
