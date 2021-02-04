package com.naah.redis.lock.redisson.simple.component.impl;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.naah.redis.lock.redisson.simple.component.AquiredLockWorker;
import com.naah.redis.lock.redisson.simple.component.DistributedLocker;
import com.naah.redis.lock.redisson.simple.exception.UnableToAquireLockException;

/**
 * @author chendesheng
 * @create 2019/10/12 10:49
 */
public class RedissonDistributedLocker implements DistributedLocker {

	private RedissonClient redissonClient;

	private final static String LOCKER_PREFIX = "lock:";

	@Override
	public RLock lock(String lockKey) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock();
		return lock;
	}

	@Override
	public RLock lock(String lockKey, int leaseTime) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(leaseTime, TimeUnit.SECONDS);
		return lock;
	}

	@Override
	public RLock lock(String lockKey, TimeUnit unit, int timeout) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(timeout, unit);
		return lock;
	}

	@Override
	public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
		RLock lock = redissonClient.getLock(lockKey);
		try {
			return lock.tryLock(waitTime, leaseTime, unit);
		} catch (InterruptedException e) {
			return false;
		}
	}

	@Override
	public void unlock(String lockKey) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.unlock();
	}

	@Override
	public void unlock(RLock lock) {
		lock.unlock();
	}

	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	@Override
	public <T> T lock(String resourceName, AquiredLockWorker<T> worker)
			throws InterruptedException, UnableToAquireLockException, Exception {

		return lock(resourceName, worker, 100);
	}

	@Override
	public <T> T lock(String resourceName, AquiredLockWorker<T> worker, int lockTime)
			throws UnableToAquireLockException, Exception {
		RLock lock = redissonClient.getLock(LOCKER_PREFIX + resourceName);
		// Wait for 100 seconds seconds and automatically unlock it after lockTime
		// seconds
		boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS);
		if (success) {
			try {
				return worker.invokeAfterLockAquire();
			} finally {
				lock.unlock();
			}
		}
		throw new UnableToAquireLockException();
	}
}