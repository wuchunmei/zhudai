package com.zhudai.network;

import android.content.Context;

import com.zhudai.network.api.CallLogService;

import java.io.File;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 通话记录相关接口调用
 * Created by xm
 */
public class CallLogConnector {

    private static CallLogService creatService(Context context) {
        return RetrofitWrapper.INSTANCE.getInstance(context.getApplicationContext()).create(CallLogService.class);
    }

    /**
     * 上传通话记录
     *
     * @param context
     * @param calllogmap
     * @return
     */
    public static Observable<ResponseResult> postCallLog(Context context, Map<String, String> calllogmap) {
        CallLogService callLogService = creatService(context);
        return callLogService.postCallLog(calllogmap);
    }

    /**
     * 保存顾问通话记录
     *
     * @param context
     * @param kid
     * @param type
     * @param number
     * @param name
     * @param date
     * @param duration
     * @return
     */
    public static Observable<ResponseResult> postCallLog(Context context, int kid, int type, String number, String name, long date, int duration) {
        //时间戳转成秒，服务端只支持这个
        date = date / 1000;
        CallLogService callLogService = creatService(context);
        return callLogService.postCallLog(kid, type, number, name, (int)date, duration);
    }

    /**
     * 上传录音文件
     *
     * @param context
     * @param number
     * @param uid
     * @param path
     * @return
     */
    public static Observable<ResponseResult> uploadCallRecord(Context context, int uid, String number, long date, String path) {
        //时间戳转成秒，服务端只支持这个
        date = date / 1000;
        RequestBody n = RequestBody.create(MediaType.parse("text/plain"), number);
        RequestBody d = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(date));
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(uid));

        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part callRecord = MultipartBody.Part.createFormData("recording", file.getName(), requestFile);


        CallLogService callLogService = creatService(context);
        return callLogService.uploadCallRecord(id, n, d, callRecord);
    }

}
