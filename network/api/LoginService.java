package com.zhudai.network.api;

import com.zhudai.network.ResponseResult;
import com.zhudai.common.bo.User;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginService {

    @FormUrlEncoded
    @POST("login_do.html")
    Observable<ResponseResult<User>> login(@Field("mobile") String mobile, @Field("password") String psw, @Field("verification") String code);

    /**
     * 请求发送手机验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("get_verification.html")
    Observable<ResponseResult> getCode(@Field("mobile") String name);

    @FormUrlEncoded
    @POST("idcard_certification.html")
    Observable<ResponseResult> verifyIDCard(@Field("kid") int uid, @Field("id_card_number") String idcardnum);

    @FormUrlEncoded
    @POST("check_firstlogin.html")
    Observable<ResponseResult> setVerifySuccess(@Field("kid") int kid);

    @FormUrlEncoded
    @POST("feedback.html")
    Observable<ResponseResult> feedback(@Field("kid") int uid, @Field("content") String content);

}
