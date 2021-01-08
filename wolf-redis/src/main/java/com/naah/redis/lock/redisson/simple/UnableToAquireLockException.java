package com.naah.redis.lock.redisson.simple;
/**
 * Created by fangzhipeng on 2017/4/5.
 * 异常类
 */
public class UnableToAquireLockException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnableToAquireLockException() {
    }

    public UnableToAquireLockException(String message) {
        super(message);
    }

    public UnableToAquireLockException(String message, Throwable cause) {
        super(message, cause);
    }
}