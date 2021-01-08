package com.naah.redis.common;

import java.util.concurrent.CountDownLatch;

// 老板进入会议室等待5个人全部到达会议室才会开会。所以这里有两个线程老板等待开会线程、员工到达会议室：
public class CountDownLatchTest {
    private static CountDownLatch countDownLatch = new CountDownLatch(5);
 
    // Boss线程，等待员工到达开会
    static class BossThread extends Thread {
        @Override
        public void run() {
            System.out.println("Boss在会议室等待，总共有" + countDownLatch.getCount() + "个人开会...");
            try {
                countDownLatch.await(); // Boss等待
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
 
            System.out.println("所有人都已经到齐了，开会吧...");
        }
    }
 
    // 员工到达会议室
    static class EmpleoyeeThread extends Thread {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "，到达会议室...."); // 员工到达会议室 count - 1
            countDownLatch.countDown();
        }
    }
 
    public static void main(String[] args) {
        // Boss线程启动
        new BossThread().start();
 
        // 员工到达会议室
        for (int i = 0; i < countDownLatch.getCount(); i++) {
            new EmpleoyeeThread().start();
        }
    }
}