package com.naah.stomp.model;

import java.io.Serializable;


public class MQMessageDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;
	private String context;
	private String fromUserId;
	private String toUserId;
	
	public MQMessageDTO(){
		
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

}
