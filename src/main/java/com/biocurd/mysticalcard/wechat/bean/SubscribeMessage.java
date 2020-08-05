package com.biocurd.mysticalcard.wechat.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author denmou
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscribeMessage {
    @JsonProperty("errcode")
    private int errCode;
    @JsonProperty("errmsg")
    private String errMsg;

    /**
     * 根据错误码判断本次请求是否完成
     * [0:正常，43101:用户取消, 40003:openId错误]视为完成，无需重试
     * @return 处理结果
     */
    public boolean isSuccess() {
        return errCode == 0 || errCode == 43101 || errCode == 40003;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
