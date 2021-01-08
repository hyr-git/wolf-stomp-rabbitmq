package com.naah.redis.common.constants;

/**
 * 错误码标识接口
 */
public interface CodeMessage {

    /**
     * 获取code码
     * @return code码
     */
    Integer getCode();

    /**
     * 获取错误消息
     * @return 错误消息
     */
    String getMessage();

    /**
     * 错误描述
     * @return 错误描述
     */
    String getDescription();

    /**
     * 格式化message内容
     *
     * @param codeMessage 错误码
     * @param paramValue 格式化内容
     * @return 填充了占位符的异常码
     */
    static CodeMessage formatMessage(CodeMessage codeMessage, Object... paramValue) {
        return new CodeMessage() {

            @Override
            public Integer getCode() {
                return codeMessage.getCode();
            }

            @Override
            public String getMessage() {
                return String.format(codeMessage.getMessage(), paramValue);
            }

            @Override
            public String getDescription() {
                return codeMessage.getDescription();
            }

        };
    }

}
