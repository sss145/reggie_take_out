package com.mkn.common;

/**
 * @author ：mkn
 * @date ：Created in 2022/8/8 9:59
 * @description：自定义业务异常
 */
public class CustomException extends RuntimeException {
    public CustomException(String msg) {
        super(msg);
    }
}
