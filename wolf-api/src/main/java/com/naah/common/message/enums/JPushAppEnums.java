package com.naah.common.message.enums;

/***
 * 极光app系统标识
 * @author mlj
 */

public enum JPushAppEnums {

	CARSIR_APP("carsir","carsir端"),
	CENTER_APP("center","中心端");
	
	private String code;
	
	private String desc;
	
	private JPushAppEnums(String code,String desc) {
		this.setCode(code);
		this.setDesc(desc); 
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
