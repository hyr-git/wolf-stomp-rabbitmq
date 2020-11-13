package com.naah.common.message.component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.naah.common.jiguang.JPushUtil;
import com.naah.common.message.constant.MessageBaseConstant;
import com.naah.common.message.constant.MessageTitleConstant;
import com.naah.common.message.enums.JPushAppEnums;
import com.naah.common.message.enums.MessageHandlerTypeEnums;
import com.naah.common.message.enums.MessagePageEmum;
import com.naah.common.message.enums.MessageSystemEnums;
import com.naah.common.message.model.MQMessage;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;


public class JPushManageTest {

    //在极光注册上传应用的 appKey 和 masterSecret
	//carsir
    public static  String carsirAppKey = "1e623dc9152d6b4f7d465aa7";//必填，例如466f7032ac604e02fb7bda89
    public static  String carsirMasterSecret = "091d24d2a8955c1b897e8dd6";//必填，每个应用都对应一个masterSecret

    //center
	public static   String centerAppKey = "e3fcde833a0a155830f8995c";//必填，例如466f7032ac604e02fb7bda89
	public static   String centerMasterSecret = "e319f99bd771c1e5d264d1e2";//必填，每个应用都对应一个masterSecret

   /***
    * 推送给设备标识参数的用户
    * @param aliasList 别名或别名组
    * @param msgTitle 消息标题
    * @param msgContent 消息内容
    * @param extranKey 扩展字段
    * @param extranParam 扩展字段
    * @return 0推送失败，1推送成功
    */
    public static Integer sendToAliasList(List<String> aliasList,String msgTitle,String msgContent,String extranKey,String extranParam){
        JPushClient jpushClient = new JPushClient(carsirMasterSecret, carsirAppKey, null, ClientConfig.getInstance());
        return JPushUtil.sendToAliasList(jpushClient,aliasList,msgContent,extranKey,extranParam,msgTitle);
    }
    
    public static void main(String[] args) {
    	JPushManageTest fd = new JPushManageTest();
    	fd.testMain();
	}
    
    public void testMain() {
    	JPushClient jpushClient = getJPushClientByTag(JPushAppEnums.CARSIR_APP.getCode());
    	for (int i = 1; i <= 3; i++) {
    		//tewst(jpushClient,i);
    		//sendCenterWorkOrderToAndroid(jpushClient);
    		sendCarsirOrderStatusChangeToAndroid(jpushClient);
		}
    }
    
    
    public static JPushClient getJPushClientByTag(String appTag) throws Exception {
    	 JPushClient jpushClient = null;
    	if(StringUtils.equals(appTag, JPushAppEnums.CARSIR_APP.getCode())) {
            jpushClient = new JPushClient(carsirMasterSecret, carsirAppKey, null, ClientConfig.getInstance());
    	}else if(StringUtils.equals( appTag, JPushAppEnums.CENTER_APP.getCode())) {
    		jpushClient = new JPushClient(centerMasterSecret, centerAppKey, null, ClientConfig.getInstance());
    	}else {
    		throw new Exception("极光未知的系统标识");
    	}
    	return jpushClient;
    }
    
    /***
     * 发送给carsir安卓端的订单状态变更通知
     * @param jpushClient
     */
    private static void sendCarsirOrderStatusChangeToAndroid(JPushClient jpushClient) {
    	List<String> tagsList = new ArrayList<>();
    	tagsList.add("16033333333");
    	
    	//TODO 推送消息给APP端
 		MQMessage message = new MQMessage();
 		message.setFromUserId("16033333333");
 		message.setToUserId("16033333333");
 		//TODO 消息标题需要审核确定
 		message.setTitle("16033333333-----轻松配");
 		//TODO 具体内容格式待定
 		message.setContext("16033333333-----轻松配");//MessageTitleConstant.MSG_QSP_CHECK_PRICE
 		message.setType(MessageHandlerTypeEnums.TODO.name());//消息类型 TODO
 		message.setSystemTag(MessageSystemEnums.APP.name());//终端类型
 		message.setExtraKey(MessageBaseConstant.EXTRA_KEY);//业务扩展字段
 		JSONObject extraJson = new JSONObject();
 		
 		
 		JSONObject businessJson = new JSONObject();
 		// TODO
 		String orderId="aa81b20f0f354beebd09b4ea3bce5a46";
 		String orderStatus="WILL_CHECK";
 		String orderType="QSS";
 		businessJson.put("orderId", orderId);
 		businessJson.put("orderStatus", orderStatus);
 		businessJson.put("orderType", orderType);
 		
 		extraJson.put(MessageBaseConstant.BUSINESS_TYPE_KEY, MessagePageEmum.CARSIR_QSP_ORDER_DETAIL.getCode());
 		extraJson.put(MessageBaseConstant.BUSINESS_MODEL, businessJson);
 		message.setExtrasparam(extraJson.toJSONString());//业务扩展字段
    	JPushUtil.sendToAliasList(jpushClient,tagsList,message.getTitle(),message.getContext(),message.getExtraKey(),message.getExtrasparam());
    }
    
    
    /***
     * 发送给中心端检测工单
     * @param jpushClient
     */
    private static void sendCenterWorkOrderToAndroid(JPushClient jpushClient) {
    	List<String> tagsList = new ArrayList<>();
    	tagsList.add("16033333333");
    	
    	//TODO 推送消息给APP端
 		MQMessage message = new MQMessage();
 		message.setFromUserId("16033333333");
 		message.setToUserId("16033333333");
 		//TODO 消息标题需要审核确定
 		message.setTitle(MessageTitleConstant.WEB_TITLE_WORK_ORDER_CHECK_REJECT +"重新填写车辆信息");
 		//TODO 具体内容格式待定
 		message.setContext(MessageTitleConstant.WEB_TITLE_WORK_ORDER_CHECK_REJECT +"重新填写车辆信息");
 		message.setType(MessageHandlerTypeEnums.TODO.name());//消息类型 TODO
 		message.setSystemTag(MessageSystemEnums.APP.name());//终端类型
 		message.setExtraKey(MessageBaseConstant.EXTRA_KEY);//业务扩展字段
 		JSONObject extraJson = new JSONObject();
 		
 		
 		JSONObject businessJson = new JSONObject();
 		// TODO
 		String orderId="626bce8c9d5640809dc9e61f1038d68e";
 		String workOrderId="bfeda7ec9bf74084a2cedf018210187b";
 		String workStatus="CHECK_WAIT";
 		String workOrderType="CHECK_WORK_ORDER";
 		businessJson.put("orderId", orderId);
 		businessJson.put("workOrderId",workOrderId);
 		businessJson.put("workStatus", workStatus);
 		businessJson.put("workOrderType", workOrderType);
 		businessJson.put("workOrderTag", "QSP");
 		businessJson.put("carModel","carModel012");
 		businessJson.put("carColor", "RED");
 		businessJson.put("carNature", "PUBLIC");
 		businessJson.put("carNo", "CAR1203499");
 		businessJson.put("numberPlate", "A-011");
 		businessJson.put("workOrderStatus", "CHECK_WAIT");
 		businessJson.put("workOrderLastUpdateTime", new Date());
 		businessJson.put("workOrderCreateTime", new Date());
 		businessJson.put("workOrderOperationDesc", "备注说明");
 		
 		extraJson.put(MessageBaseConstant.BUSINESS_TYPE_KEY, MessageBaseConstant.CENTER_WORK_ORDER_CHECK_DETAIL);
 		extraJson.put(MessageBaseConstant.BUSINESS_MODEL, businessJson);
 		message.setExtrasparam(extraJson.toJSONString());//业务扩展字段
    	JPushUtil.sendToAliasList(jpushClient,tagsList,message.getTitle(),message.getContext(),message.getExtraKey(),message.getExtrasparam());

    }
    
    /***
     * 推送给ios订单状态编化的
     * @param jpushClient
     * @param i
     */
    private static void sendToIOS(JPushClient jpushClient,int i) {

    	List<String> tagsList = new ArrayList<>();
    	tagsList.add("16033333333");
    	JSONObject json = new JSONObject();
    	json.put("businessType", "1");
    	JSONObject orderJson = new JSONObject();
    	orderJson.put("orderId", "0167658052374d0897abe7ec2fdf48d2");
    	orderJson.put("orderStatus", "FINISH_FINANCE");
    	json.put("orderModel", orderJson);
    	
    	JSONObject json1 = new JSONObject();
    	json1.put("userId", UUID.randomUUID());
    	json1.put("workType", "checkOrder");
    	json1.put("url", "dev.carsir.xin");
    	JPushUtil.sendToAliasList(jpushClient,tagsList,"[订单状态变更]",json1.toJSONString(),"orderBusinessKey",json.toJSONString());
    	
    }
}