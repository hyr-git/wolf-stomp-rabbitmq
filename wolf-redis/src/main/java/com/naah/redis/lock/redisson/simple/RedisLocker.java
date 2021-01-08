package com.naah.redis.lock.redisson.simple;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * Created by fangzhipeng on 2017/4/5.
 */
@Component
public class RedisLocker  implements DistributedLocker{

    private final static String LOCKER_PREFIX = "lock:";
    
    
    private RedissonClient redissonClient;
    
    @Autowired
    RedissonConnector redissonConnector;
    
    @Override
    public <T> T lock(String resourceName, AquiredLockWorker<T> worker) throws InterruptedException, UnableToAquireLockException, Exception {

        return lock(resourceName, worker, 100);
    }

    @Override
    public <T> T lock(String resourceName, AquiredLockWorker<T> worker, int lockTime) throws UnableToAquireLockException, Exception {
        RedissonClient redisson= redissonConnector.getClient();
        RLock lock = redisson.getLock(LOCKER_PREFIX + resourceName);
      // Wait for 100 seconds seconds and automatically unlock it after lockTime seconds
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
    
    @Override
    public RLock lock(String lockKey) {
        RedissonClient redisson= redissonConnector.getClient();
     RLock lock = redisson.getLock(lockKey);
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
    public RLock lock(String lockKey, TimeUnit unit ,int timeout) {
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
}