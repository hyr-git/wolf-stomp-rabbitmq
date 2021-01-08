package com.naah.redis.redpackage.controller;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naah.redis.common.vo.R;
import com.naah.redis.redpackage.service.RedPackageService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RedPackageController {

	@Resource
	private RedPackageService redPackageService;
	

	@GetMapping("/getRedPackage")
	public R getRedPackage() {
		redPackageService.getRedPackage();;
		return R.ok();
	}
	
	
	@GetMapping("/grabRedPackage")
	public R grabRedPackage() {
		String result = redPackageService.grabRedPackage(1l, 1l);
		return R.ok(result);
	}
	
	@GetMapping("/grabRedPackageRedison")
	public R grabRedPackageRedison() {
		String result = redPackageService.grabRedPackageRedison(1l, 1l);
		return R.ok(result);
	}
	
	@GetMapping("/grabRedPackageBatch")
	public String grabRedPackageBatch() {
		String userId = UUID.randomUUID().toString();

		int num = 100;
		ExecutorService service = Executors.newFixedThreadPool(num);

		CountDownLatch latch = new CountDownLatch(num);

		for (int i = 1; i <= num; i++) {
			final int index = i;
			Runnable command = new Runnable() {
				@Override
				public void run() {
					//log.info("子线程" + Thread.currentThread().getName() + "开始执行");
					String result = redPackageService.grabRedPackage(1l, 1l);
					//log.info("子线程"+Thread.currentThread().getName()+"执行完成");
					// 当前线程调用此方法,则计数器减一
					latch.countDown();
					log.info("用户" + index + "----->" + result);
				}
			};
			service.execute(command);
		}
		// 阻塞当前线程直到计数器的值为0
		try {
			log.info("主线程"+Thread.currentThread().getName()+"等待子线程执行完成...");
			latch.await();
			log.info("主线程"+Thread.currentThread().getName()+"开始执行...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}
}
