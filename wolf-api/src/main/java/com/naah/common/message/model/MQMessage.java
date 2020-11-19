package com.naah.common.message.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MQMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String type;//消息类型
	private String title;
	
	@NotNull(message = "消息内容不能为空")
	private String context;
	
	@NotNull(message = "系统标识不能为空")
    @ApiModelProperty(value = "系统标识nba、carsir、oylp、center")
	private String systemTag;//系统标识nba、carsir、oylp、center
	
	@NotNull(message = "移动终端设备不能为空")
    @ApiModelProperty(value = "移动终端设备:all、ios、android")
	private String mobileTerminal;//移动终端设备:all、IOS、Android
	
	@NotNull(message = "消息接收人不能为空")
	private String toUserId;
	private String status;
	private String fromUserId;
	private String extraKey;//匹配极光数据增加扩展字段
	private String extrasparam;//匹配极光数据增加扩展字段
	private Date createdDate;
	private Date readDate;
	
	public MQMessage(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExtraKey() {
		return extraKey;
	}

	public void setExtraKey(String extraKey) {
		this.extraKey = extraKey;
	}

	public String getExtrasparam() {
		return extrasparam;
	}

	public void setExtrasparam(String extrasparam) {
		this.extrasparam = extrasparam;
	}
}
