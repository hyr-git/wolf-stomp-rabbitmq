package com.naah.common.jdk18.vo;
public class UserBean{
    	
    	private int userId;    	
    	private String workId;
    	private String userName;
    	
    	public UserBean (int userId,String workId,String userName){
    		this.userId = userId;
    		this.workId = workId;
    		this.userName = userName;
    	}

		public int getUserId() {
			return this.userId;
		}
		
		public String getWorkId() {
			return this.workId;
		}

		public String getUserName() {
			return this.userName;
		}
}    	
    		