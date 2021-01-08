package com.naah.redis.lock.redisson.simple;

/**
 * Created by fangzhipeng on 2017/4/5.
 * 获取锁后需要处理的逻辑
 */
public interface AquiredLockWorker<T> {
     T invokeAfterLockAquire() throws Exception;
}