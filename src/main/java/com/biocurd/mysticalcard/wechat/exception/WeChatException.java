package com.biocurd.mysticalcard.wechat.exception;

/**
 * @author denmou
 */
public class WeChatException extends Exception {
    public WeChatException(String message) {
        super(message);
    }

    public WeChatException(Throwable e) {
        super(e);
    }

    public WeChatException(String message, Throwable e) {
        super(message, e);
    }
}
