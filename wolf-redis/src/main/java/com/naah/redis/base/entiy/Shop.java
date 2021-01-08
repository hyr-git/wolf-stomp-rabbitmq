package com.naah.redis.base.entiy;

import java.io.Serializable;

import lombok.Data;

@Data
public class Shop implements Serializable{

	private static final long serialVersionUID = -471381944358552777L;
	private String id;
	private String shopName;
	private String shopNo;
	private String shopAddress;
	
}
