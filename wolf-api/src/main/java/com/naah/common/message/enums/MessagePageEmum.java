package com.naah.common.message.enums;

public enum MessagePageEmum {

	//中心端
	CENTER_WORK_CHECK_DETAIL("CENTER_WORK_CHECK_DETAIL","中心端-检测工单详情"),
	CENTER_ORDER_DETAIL("CENTER_ORDER_DETAIL","中心端-订单状态变更详情"),
	
	//web端
	WEB_WORK_CHECK_DETAIL("WEB_WORK_CHECK_DETAIL","中心端-检测工单详情"),
	
	//carsir端
	CARSIR_QSP_ORDER_DETAIL("CARSIR_QSP_ORDER_DETAIL","carsir-QSP订单状态变更详情"),
	//carsir端
	CARSIR_QSS_ORDER_DETAIL("CARSIR_QSS_ORDER_DETAIL","carsir-QSS订单状态变更详情"),
	//carsir端
	CARSIR_XD_ORDER_DETAIL("CARSIR_XD_ORDER_DETAIL","carsir-XD订单状态变更详情"),
	
	//carsir端
	CARSIR_QSP_CAR_DETAIL("CARSIR_QSP_CAR_DETAIL","carsir-QSP车辆状态变更详情"),
	//carsir端
	CARSIR_QSS_CAR_DETAIL("CARSIR_QSS_CAR_DETAIL","carsir-QSS车辆状态变更详情"),
	//carsir端
	CARSIR_XD_CAR_DETAIL("CARSIR_XD_CAR_DETAIL","carsir-XD车辆状态变更详情");
	
	
	
    private String code;
    
    private String desc;
    
    private MessagePageEmum(String code,String desc) {
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
