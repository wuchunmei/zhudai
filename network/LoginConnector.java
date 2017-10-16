package com.zhudai.network;

import android.content.Context;

import com.zhudai.network.api.LoginService;
import com.zhudai.common.bo.User;
import com.zhudai.network.transformer.SchedulersCompat;
import io.reactivex.Observable;

/**
 * 登录相关接口调用
 * Created by xm on 2016/8/8.
 */
public class LoginConnector {

    private static LoginService create(Context context){
        return RetrofitWrapper.INSTANCE.getInstance(context.getApplicationContext()).create(LoginService.class);
    }

    /**
     * 获取手机验证码
     * @param context
     * @param name
     * @return
     */
    public static Observable<ResponseResult> getCode(Context context, String name) {
        return create(context.getApplicationContext()).getCode(name);
    }

    public static Observable<ResponseResult<User>> login(Context context, String mobile, String psw, String code) {
        return create(context.getApplicationContext()).login(mobile, psw, code);
    }

    public static Observable<ResponseResult> verifyIDCard(Context context, int uid, String idcardnum) {
        return create(context.getApplicationContext()).verifyIDCard(uid, idcardnum);
    }

    /**
     * 身份认证成功后调用
     * @param context
     * @param uid
     * @return
     */
    public static Observable<ResponseResult> setVerifySuccess(Context context, int uid) {
        return create(context.getApplicationContext()).setVerifySuccess(uid);
    }

    /**
     * 意见反馈
     * @param context
     * @param uid
     * @param content
     * @return
     */
    public static Observable<ResponseResult> feedback(Context context, int uid, String content) {
        return create(context.getApplicationContext()).feedback(uid, content);
    }

}
