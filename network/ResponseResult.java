package com.zhudai.network;

/**
 * 服务端响应数据
 *
 * @author xm
 */
public final class ResponseResult<T> {

    // code 说明：
    // 200 返回正常
    // 500 执行失败

    /**
     * 接口请求成功
     */
    public static final int RESPONCE_CODE_SUCCESS = 200;
    /**
     * 失败
     */
    public static final int RESPONCE_CODE_FAILED = 500;

    public static final int RESPONCE_ERROR_CONNECTERROR = 10000;
    public static final int RESPONCE_ERROR_TIMEOUT = 10001;
    public static final int RESPONCE_ERROR_DATAPARSEERROR = 10002;
    public static final int RESPONCE_ERROR_DATANOENOUGH = 10003;
    public static final int RESPONCE_ERROR_SERVERERROR = 10004;
    public static final int RESPONCE_ERROR_NOCONNECTION = 10005;
    public static final int RESPONCE_ERROR_UPLOADFILEFAILED = 10006;
    public static final int RESPONCE_ERROR_PARAMERROR = 10007;

    public static final ResponseResult responseSuccess = new ResponseResult();

    public static final ResponseResult responseFailed = new ResponseResult(RESPONCE_CODE_FAILED);

    public static ResponseResult responseConnectError = new ResponseResult(
            ResponseResult.RESPONCE_ERROR_CONNECTERROR);
    public static ResponseResult responseDataNoEnough = new ResponseResult(
            ResponseResult.RESPONCE_ERROR_DATANOENOUGH);
    public static ResponseResult responseDataParseError = new ResponseResult(
            ResponseResult.RESPONCE_ERROR_DATAPARSEERROR);
    public static ResponseResult responseNoConnection = new ResponseResult(
            ResponseResult.RESPONCE_ERROR_NOCONNECTION);
    public static ResponseResult responseParamError = new ResponseResult(
            ResponseResult.RESPONCE_ERROR_PARAMERROR);

    public final int code;
    public String msg;
    public String dataStr;
    public T data;

    public boolean success(){
        return code == RESPONCE_CODE_SUCCESS;
    }

    public ResponseResult(int code) {
        this.code = code;
    }

    public ResponseResult() {
        this.code = RESPONCE_CODE_SUCCESS;
    }

    @Override
    public String toString() {
        return ";code = " + code +
                ";msg = " + msg +
                ";data = " + dataStr;
    }
}
