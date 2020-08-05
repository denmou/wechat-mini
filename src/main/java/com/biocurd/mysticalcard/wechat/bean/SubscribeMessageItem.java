package com.biocurd.mysticalcard.wechat.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author denmou
 */
public class SubscribeMessageItem {
    @JsonProperty("touser")
    private String toUser;
    @JsonProperty("template_id")
    private String templateId;
    @JsonProperty("page")
    private String page = "pages/home/home?server=";
    @JsonProperty("miniprogram_state")
    private String miniProgramState = "formal";
    @JsonProperty("lang")
    private String lang = "zh_CN";
    @JsonProperty("data")
    private SubscribeMessageData data;

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getMiniProgramState() {
        return miniProgramState;
    }

    public void setMiniProgramState(String miniProgramState) {
        this.miniProgramState = miniProgramState;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public SubscribeMessageData getData() {
        return data;
    }

    public void setData(SubscribeMessageData data) {
        this.data = data;
    }
}
