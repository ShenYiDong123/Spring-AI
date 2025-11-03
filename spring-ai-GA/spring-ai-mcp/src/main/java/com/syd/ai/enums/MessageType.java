package com.syd.ai.enums;


/**
 * 消息类型枚举，定义系统中各类消息的类型标识
 */
public enum MessageType {

    /**
     * 用户发送的消息（显式，可展示给用户）
     */
    USER("user", "用户消息"),

    /**
     * AI助手的回复消息（显式，可展示给用户）
     */
    AI("assistant", "AI回复消息"),

    /**
     * 系统级消息（隐式，通常用于初始化上下文、配置参数等，不直接展示给用户）
     */
    SYSTEM("system", "系统配置消息"),

    /**
     * 工具调用相关消息（如调用API、数据库查询等的请求/响应，可用于日志或流程追踪）
     */
    TOOL_INVOKE("tool", "工具调用消息"),

    /**
     * 历史对话消息（用于缓存或回溯历史记录，通常是用户与AI的过往交互）
     */
    HISTORY("history", "历史对话消息"),

    /**
     * 通知类消息（如系统提示、操作结果通知等，可轻度展示给用户）
     */
    NOTIFICATION("notification", "系统通知消息"),

    /**
     * 错误消息（如调用失败、参数错误等，用于内部错误处理或提示用户）
     */
    ERROR("error", "错误提示消息");


    /**
     * 类型标识（用于序列化、传输或存储）
     */
    private final String type;

    /**
     * 类型描述（用于日志、文档或前端展示说明）
     */
    private final String desc;


    /**
     * 构造函数：初始化消息类型和描述
     * @param type 类型标识
     * @param desc 类型描述
     */
    MessageType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }


    /**
     * 获取类型标识（如"user"、"assistant"）
     * @return 类型标识字符串
     */
    public String getType() {
        return type;
    }

    /**
     * 获取类型描述（如"用户消息"、"AI回复消息"）
     * @return 类型描述字符串
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 重写toString，返回类型标识（方便日志打印和快速识别）
     * @return 类型标识
     */
    @Override
    public String toString() {
        return type;
    }
}