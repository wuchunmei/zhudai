package com.zhudai.network.exception;

/**
 * Created by xm on 2016/8/18.
 */
public class ServerException extends Exception{

    private int code;

    /**
     * 服务器异常
     * @param msg
     */
    public ServerException(String msg){
        super(msg);
    }

    public ServerException(int code, String msg){
        super(msg);
        this.code = code;
    }
}
