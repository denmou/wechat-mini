package com.biocurd.mysticalcard.wechat;

import com.biocurd.mysticalcard.wechat.bean.*;
import com.biocurd.mysticalcard.wechat.constant.Url;
import com.biocurd.mysticalcard.wechat.exception.WeChatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author denmou
 */
public class WeChat {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChat.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final WeChatConfig config;
    private String accessToken;
    private final ScheduledExecutorService scheduled;

    /**
     * 实例化
     * @param config 小程序配置
     */
    public WeChat(WeChatConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
        this.mapper = new ObjectMapper();
        this.config = config;
        this.scheduled = new ScheduledThreadPoolExecutor(1);
        getAccessToken();
    }

    /**
     * 广播小程序订阅消息给订阅者
     *
     * @param page        消息跳转小程序页面
     * @param data        消息模版参数
     * @param subscribers 订阅者openId列表
     * @return 推送成功订阅者openId列表
     */
    public List<String> broadcast(String page, SubscribeMessageData data, List<String> subscribers) {
        List<String> consumer = new ArrayList<>();
        SubscribeMessageItem item = new SubscribeMessageItem();
        item.setTemplateId(config.getTemplateId());
        if (config.getState() != null) {
            item.setMiniProgramState(config.getState());
        }
        if (page != null) {
            item.setPage(page);
        }
        if (data != null) {
            item.setData(data);
        }
        for (String subscriber : subscribers) {
            item.setToUser(subscriber);
            boolean result = sendNotice(item);
            if (!result) {
                LOGGER.warn("[" + item.getToUser() + "]推送失败, 重试一次");
                result = sendNotice(item);
                if (!result) {
                    LOGGER.warn("[" + item.getToUser() + "]推送重试失败, 跳过");
                }
            }
            if (result) {
                consumer.add(subscriber);
            }
        }
        return consumer;
    }

    /**
     * 推送订阅消息到微信服务器
     *
     * @param item 订阅消息
     * @return 推送是否成功
     */
    private boolean sendNotice(SubscribeMessageItem item) {
        String url = String.format(Url.SUBSCRIBE_TOKEN_FORMAT, accessToken);
        ResponseEntity<SubscribeMessage> res = restTemplate.postForEntity(url, item, SubscribeMessage.class);
        if (res.getStatusCode().is2xxSuccessful()) {
            SubscribeMessage subscribeMessage = res.getBody();
            if (subscribeMessage != null) {
                if (subscribeMessage.isSuccess()) {
                    LOGGER.warn("[" + item.getToUser() + "]推送成功，状态码[" + subscribeMessage.getErrCode()
                            + "]，返回消息[" + subscribeMessage.getErrMsg() + "]");
                    return true;
                } else {
                    LOGGER.warn("[" + item.getToUser() + "]推送失败: " + subscribeMessage.getErrMsg());
                }
            } else {
                LOGGER.warn("[" + item.getToUser() + "]推送失败: 返回消息体为空");
            }
        } else {
            LOGGER.warn("[" + item.getToUser() + "]推送请求失败: " + res.getStatusCodeValue());
        }
        return false;
    }

    /**
     * 更新令牌
     */
    private void getAccessToken() {
        long delay = 1000L;
        String url = String.format(Url.TOKEN_APP_SECRET_FORMAT, config.getAppId(), config.getAppSecret());
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        if (res.getStatusCode().is2xxSuccessful()) {
            String body = res.getBody();
            AccessToken access = null;
            try {
                if (body != null) {
                    access = mapper.readValue(body, AccessToken.class);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (access != null) {
                if (access.isSuccess()) {
                    long time = access.getExpiresIn();
                    if (time / 2 > 600) {
                        time -= 300;
                    } else {
                        time /= 2;
                    }
                    if (time > 1) {
                        delay = time * 1000L;
                        accessToken = access.getAccessToken();
                        LOGGER.info("获取AccessToken成功，有效期[" + access.getExpiresIn() + "]s, [" + delay + "]ms后重新获取");
                    }
                } else {
                    LOGGER.warn("获取AccessToken[GET: " + url + "]请求失败: " + access.getErrMsg());
                }
            } else {
                LOGGER.warn("获取AccessToken[GET: " + url + "]请求失败: 返回消息体为空");
            }
        } else {
            LOGGER.warn("获取AccessToken[GET: " + url + "]请求失败: " + res.getStatusCodeValue());
        }
        scheduled.schedule(this::getAccessToken, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取用户openId
     *
     * @param code 小程序前段[wx.login]登陆码
     * @return openId
     * @throws WeChatException 处理异常
     */
    private String getOpenId(String code) throws WeChatException {
        String url = String.format(Url.SESSION_APP_SECRET_CODE_FORMAT, config.getAppId(), config.getAppSecret(), code);
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        if (res.getStatusCode().is2xxSuccessful()) {
            String body = res.getBody();
            Code2Session code2Session = null;
            try {
                if (body != null) {
                    code2Session = mapper.readValue(body, Code2Session.class);
                }
            } catch (JsonProcessingException e) {
                throw new WeChatException("[" + url + "]GET请求失败: 消息体解析失败", e);
            }
            if (code2Session != null) {
                if (code2Session.isSuccess()) {
                    String openId = code2Session.getOpenId();
                    LOGGER.info("根据code[" + code + "]获取到openId[" + openId + "]");
                    return openId;
                } else {
                    throw new WeChatException("[" + url + "]GET请求失败: " + code2Session.getErrMsg());
                }
            } else {
                throw new WeChatException("[" + url + "]GET请求失败: 返回消息体为空");
            }
        } else {
            throw new WeChatException("[" + url + "]GET请求失败: " + res.getStatusCodeValue());
        }
    }
}
