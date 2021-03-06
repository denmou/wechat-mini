package com.biocurd.mysticalcard.wechat.bean;

/**
 * @author denmou
 * @date 2020/8/5 10:54
 */
public class WeChatConfig {
    private String appId;
    private String appSecret;
    /**
     * 订阅消息模版Id
     */
    private String templateId;
    /**
     * 订阅消息跳转小程序类型
     */
    private String state;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
