package com.naah.common.message.constant;

/***
 * 消息常量
 * @author mlj
 *
 */
public class MessageTitleConstant {

	//web端推送给中心端的消息
	public static String WEB_TITLE_WORK_ORDER_CHECK_REJECT ="[车辆检测工单驳回]原因:";
	public static String WEB_TITLE_WORK_ORDER_CHECK_REFUSE ="[车辆检测工单拒绝]原因:";
	public static String WEB_TITLE_WORK_ORDER_CHECK_PASS ="[车辆检测工单通过]原因:";
	
    //轻松配
	//评估师完成检测工单并提交收购价后，跳转订单详情页面
	public static String MSG_QSP_CHECK_PRICE ="您的车辆【%s】已完成检测评估，请及时确认价格";
	//车辆入位工单完成后
	public static String MSG_QSP_FINSH_PARKING ="您的车辆【%s】已经入库，即刻起上架销售";
	//放款审核工单完成后
	public static String MSG_QSP_FINSH_LOAN ="您的车辆【%s】已完成配资";

	//轻松售-直营店
	//评估师完成检测工单并提交收购价后，跳转订单详情页面
	public static String MSG_QSS_CHECK_PRICE ="您的车辆【%s】已完成检测评估，请及时确认价格";
	//车辆入位工单完成后
	public static String MSG_QSS_FINSH_PARKING ="您的车辆【%s】已经入库，即刻起上架销售";
	//放款审核工单完成后
	public static String MSG_QSS_FINSH_LOAN ="您的车辆【%s】已完成入库";
	
	//轻松售-加盟店
	//评估师完成检测工单并提交收购价后，跳转订单详情页面
	public static String MSG_XD_CHECK_PRICE ="您的车辆【%s】已完成检测评估，请及时确认价格";
	//车辆入位工单完成后
	public static String MSG_XD_FINSH_PARKING ="您的车辆【%s】已经入库，即刻起上架销售";
	//放款审核工单完成后
	public static String MSG_XD_FINSH_LOAN ="您的车辆【%s】已完成入库";
	
	
	//轻松配、轻松售-直营店产生车辆状态变更文案提示后期存在变化
	public static String MSG_QSP_CAR_STATUS = "您的车辆【%s】已售出，即刻为您结算收益";
	public static String MSG_QSS_CAR_STATUS = "您的车辆【%s】已售出，即刻为您结算收益";
	public static String MSG_XD_CAR_STATUS = "您的车辆【%s】已售出，即刻为您结算收益";
	
	//产生30天应付订单后，跳转支付二维码页面
	public static String MSG_QSP_30_PAY_STATUS = "您的车辆【%s】有一笔30天应付订单，请及时缴纳应付费用，避免产生滞纳金，影响其他车辆放款";
	
	//产生60天应付订单后，跳转支付二维码页面
	public static String MSG_QSP_60_PAY_STATUS = "您的车辆【%s】有一笔60天应付订单，请及时缴纳应付费用，避免产生滞纳金，影响其他车辆放款";
	public static String MSG_QSS_60_PAY_STATUS = "您的车辆【%s】有一笔60天应付订单，请及时缴纳应付费用，避免产生滞纳金，影响其他车辆放款";
	
	//产生90天应付订单后，跳转支付二维码页面
	public static String MSG_QSP_90_PAY_STATUS = "您的车辆【%s】入库已超过90天，请及时缴纳应付费用并取回车辆，避免产生滞纳金，影响其他车辆放款";
	public static String MSG_QSS_90_PAY_STATUS = "您的车辆【%s】入库已超过90天，请及时缴纳应付费用并取回车辆，避免产生滞纳金，影响其他车辆放款";
	public static String MSG_XD_90_PAY_STATUS = "您的车辆【%s】入库已超过90天，已无法继续继续展示销售";
	
}
