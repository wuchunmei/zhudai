package com.zhudai.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.zhudai.common.util.NetworkUtils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nyzhang on 2016/5/5.
 */
public class CommonInterceptor implements Interceptor {

    private String proxyHost;
    private String retryProxyHost;
    private Map<String, String> mHeadersMap;

    public CommonInterceptor(String proxyHost, String retryProxyHost, Map<String, String> headers) {
        this.proxyHost = proxyHost;
        this.retryProxyHost = retryProxyHost;
        if (headers != null) {
            mHeadersMap = headers;
        } else {
            mHeadersMap = new HashMap<>();
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        //设置代理
        if (proxyHost != null) {
            HttpUrl httpUrl = originalRequest.url();
            originalRequest = originalRequest.newBuilder().addHeader("X-Online-Host", httpUrl.host()).url(httpUrl.newBuilder().scheme("https").host(proxyHost).build()).build();
        }
        Request.Builder newBuilder = originalRequest.newBuilder();
        //添加头文件，若有重复则替换
        for (Map.Entry<String, String> entry : mHeadersMap.entrySet()) {
            newBuilder.header(entry.getKey(), entry.getValue());
        }

        Request request = newBuilder.build();
        Response response = chain.proceed(request);
        if (proxyHost != null && proxyHost.equals(NetworkUtils.WAPPROXY_CHINATELCOM) && (response.body() == null || response.body().string() == null)) {
            //这里的retryProxyHost是用于在网络处于WAPPROXY_CHINATELCOM情况下，使用和不使用proxyhost两种状态的重试
            if (retryProxyHost != null) {
                HttpUrl httpUrl = request.url();
                request = request.newBuilder().header("X-Online-Host", httpUrl.host()).url(httpUrl.newBuilder().scheme("https").host(retryProxyHost).build()).build();
                response = chain.proceed(request);
                if (response.body() == null || response.body().string() == null) {
                    NetworkUtils.setCTWapHolder(false);//存在线程安全问题，留坑在此  //  holder是AtomicReference，好像并无线程安全问题？
                }
            }
            response = chain.proceed(request);//由于调用了responseBody.string(),所以要再请求一次(此方法不可删除)
        }
        return response;
    }
}
