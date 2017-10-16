package com.zhudai.network.api;

import java.util.Map;

import com.zhudai.network.ResponseResult;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CallLogService {


    /**
     * 上传通话记录
     *
     * @return
     */
    @FormUrlEncoded
    @POST("callrecords.html")
    Observable<ResponseResult> postCallLog(@FieldMap Map<String, String> calllogmap);

    /**
     * 保存顾问通话记录
     *
     * @param kid
     * @param type
     * @param number
     * @param name
     * @param date
     * @param duration
     * @return
     */
    @FormUrlEncoded
    @POST("callrecords.html")
    Observable<ResponseResult> postCallLog(@Field("kid") int kid, @Field("type") int type, @Field("number") String number,
                                           @Field("name") String name, @Field("date") int date,
                                           @Field("duration") int duration);


    @Multipart
    @POST("saverecording.html")
    Observable<ResponseResult> uploadCallRecord(@Part("kid") RequestBody kid, @Part("number") RequestBody number, @Part("date") RequestBody date,
                                                @Part MultipartBody.Part file);

}


