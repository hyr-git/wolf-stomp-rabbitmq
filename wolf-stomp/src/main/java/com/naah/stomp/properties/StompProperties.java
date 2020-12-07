package com.naah.stomp.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by lijian on 2018/11/28
 */
@ConfigurationProperties(prefix = "stomp", ignoreUnknownFields = false)
@Data
public class StompProperties {
    private String server;
    private Integer port;
    private String username;
    private String password;
}
