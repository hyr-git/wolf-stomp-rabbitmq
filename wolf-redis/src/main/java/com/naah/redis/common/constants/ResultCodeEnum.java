package com.naah.redis.common.constants;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    // 未来会存在大量的响应代码，是必然的！
    SUCCESS(true, 200, "成功"),
    REQUEST_INVALID(false, 400, "无效请求"),
    TOKEN_INVALID(false, 401, "登录凭证失效"),
    NOT_FOUND(false, 404, "资源不存在"),
    ACCESS_FORBIDDEN(false, 403, "资源无权访问"),
    INTENER_ERROR(false, 500, "系统内部错误"),
    UNKNOWN_REASON(false, 30001, "未知错误"),
    BAD_SQL_GRAMMAR(false, 30002, "sql异常"),
    JSON_PARSE_ERRPR(false, 30003, "JSON 解析错误"),
    PARAM_ERROR(false, 30004, "参数错误"),
    CUSTOM_ERRORT(false, 50001, "" ),
    FILE_ERROR(false, 30005, "上传文件格式有误");

    private Boolean success;
    private Integer code;
    private String message;

    private ResultCodeEnum(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
