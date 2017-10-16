package com.zhudai.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 判断网络util
 * 根据环境设置host
 */
public class NetworkUtils {
    public static final String WAPPROXY_CHINATELCOM = "10.0.0.200";
    public static final String WAPPROXY_CHINEMOBILE = "10.0.0.172";

    private final static AtomicReference<Boolean> ctwapHolder = new AtomicReference<Boolean>(
            false);

    public static void setCTWapHolder(boolean isSet) {
        ctwapHolder.set(isSet);
    }

    public static boolean getCTWapHolder() {
        return ctwapHolder.get();
    }

    public static String getWapProxyServer(WapProviderConfigEnum wapType) {

        if (wapType == WapProviderConfigEnum.CMWAP) {
            return WAPPROXY_CHINEMOBILE;
        }
        if (wapType == WapProviderConfigEnum.UNIWAP) {
            return WAPPROXY_CHINEMOBILE;
        }
        if (wapType == WapProviderConfigEnum.CTWAP) {
            return WAPPROXY_CHINATELCOM;
        }
        return null;
    }

    public static String getConnectProxy(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return null;
        }
        NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
        if (netWrokInfo != null && netWrokInfo.isAvailable()
                && netWrokInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            String wapType = netWrokInfo.getExtraInfo();
            if (wapType != null) {
                WapProviderConfigEnum wt = WapProviderConfigEnum
                        .from(wapType);
                if (wt == WapProviderConfigEnum.CTWAP) {
                    Boolean ctwap = ctwapHolder.get();
                    if (ctwap != null && ctwap.booleanValue()) {
                        return null;
                    }
                    return WAPPROXY_CHINATELCOM;
                }
                String proxy = NetworkUtils.getWapProxyServer(wt);
                if (proxy != null) {
                    return proxy;
                }
            }
        }
        return null;
    }

    public enum WapProviderConfigEnum {
        NONE("NONE"),
        CMWAP("CMWAP"),
        UNIWAP("UNIWAP"),
        CTWAP("CTWAP");
        private final String mode;

        private WapProviderConfigEnum(String mode) {
            this.mode = mode;
        }

        public static WapProviderConfigEnum from(String mode) {
            if (TextUtils.isEmpty(mode) || mode.equalsIgnoreCase(NONE.mode)) {
                return NONE;
            }
            if (mode.equalsIgnoreCase(CMWAP.mode)) {
                return CMWAP;
            }
            if (mode.equalsIgnoreCase(UNIWAP.mode)) {
                return UNIWAP;
            }
            if (mode.equalsIgnoreCase(CTWAP.mode)) {
                return CTWAP;
            }
            return NONE;
        }

        public String to() {
            return mode;
        }
    }
}
