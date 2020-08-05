package com.biocurd.mysticalcard.wechat.constant;

/**
 * @author denmou
 * @date 2020/8/5 11:13
 */
public class Url {
    private static final String API = "https://api.weixin.qq.com";
    public static final String TOKEN_APP_SECRET_FORMAT = API + "/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    public static final String SESSION_APP_SECRET_CODE_FORMAT = API + "/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    public static final String SUBSCRIBE_TOKEN_FORMAT = API + "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=%s";
}
