package com.naah.common.message.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConditionalOnProperty(prefix = "jiguang.app", name = {"carsirKey", "carsirSecret"})
public class JPushProperties {

	@Value("${jiguang.app.carsirKey}")
	private String carsirKey;
	
	@Value("${jiguang.app.carsirSecret}")
	private String carsirSecret;
	
	@Value("${jiguang.app.centerKey}")
	private String centerKey;
	
	@Value("${jiguang.app.centerSecret}")
	private String centerSecret;
}
