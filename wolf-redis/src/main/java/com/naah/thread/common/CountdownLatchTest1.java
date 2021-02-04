package com.naah.thread.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主线程等待子线程执行完成再执行
 * main方法是主线程,
 * ExecutorService创建了3个大小的线程池为子线程
 * 当3个子线程中的方法都执行完毕之后主线程mian才可以进行执行。
 * 
 * 主线程通过await()方法进行阻塞.
 * 子线程每执行一次代用一次countdown()方法。
 * 直到计数器为0时,主线程才继续进行执行。
 */
public class CountdownLatchTest1 {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        final CountDownLatch latch = new CountDownLatch(3);
        try {
        	for (int i = 0; i < 3; i++) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("子线程" + Thread.currentThread().getName() + "开始执行");
                            Thread.sleep((long) (Math.random() * 10000));
                            System.out.println("子线程"+Thread.currentThread().getName()+"执行完成");
                            latch.countDown();//当前线程调用此方法，则计数减一
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                service.execute(runnable);
            }
		}catch(Exception e) {
			e.printStackTrace();
		} 
        /*finally {
			service.shutdown();
		}*/
        

        try {
            System.out.println("主线程"+Thread.currentThread().getName()+"等待子线程执行完成...");
            latch.await();//阻塞当前线程，直到计数器的值为0
            System.out.println("主线程"+Thread.currentThread().getName()+"开始执行...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}