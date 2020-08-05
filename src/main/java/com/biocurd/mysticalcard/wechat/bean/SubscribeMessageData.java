package com.biocurd.mysticalcard.wechat.bean;

import com.biocurd.mysticalcard.wechat.constant.DataValue;

import java.util.HashMap;

/**
 * @author denmou
 */
public class SubscribeMessageData extends HashMap<String, HashMap<String, String>> {
    public void addValue(String k, String v) {
        computeIfAbsent(k, n -> new HashMap<>(16)).put(DataValue.VALUE, v);
    }
}
