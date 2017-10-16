package com.zhudai.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicReference;

public class VersionUtils {

    private static final String TAG = "VersionUtils";
    private static AtomicReference<ClientVersionInfo> clientVersionInfoReference = new AtomicReference<ClientVersionInfo>();


    public static ClientVersionInfo getApplicationVersionInfo(Context context) {
        ClientVersionInfo versionInfo = clientVersionInfoReference.get();
        if (versionInfo != null) {
            return versionInfo;
        }

        synchronized (clientVersionInfoReference) {
            versionInfo = clientVersionInfoReference.get();
            if (versionInfo != null) {
                return versionInfo;
            }
            versionInfo = initApplicationVersionInfo(context);
            clientVersionInfoReference.set(versionInfo);
            return versionInfo;
        }
    }

    private static ClientVersionInfo initApplicationVersionInfo(Context context) {
        ClientVersionInfo versionInfo = new ClientVersionInfo();
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            versionInfo = new ClientVersionInfo();
            versionInfo.packageName = info.packageName;
            versionInfo.versionCode = info.versionCode;
            versionInfo.versionName = info.versionName;
        } catch (NameNotFoundException e) {
        }

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        if (width < height) {
            versionInfo.displayWidth = width;
            versionInfo.displayHeight = height;
        } else {
            versionInfo.displayWidth = height;
            versionInfo.displayHeight = width;
        }
        return versionInfo;
    }

    /**
     * 版本信息
     *
     * @author zhanggx
     */
    public static class ClientVersionInfo {
        private String packageName;
        private int versionCode;
        private String versionName;
        private int displayWidth;
        private int displayHeight;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getDisplayWidth() {
            return displayWidth;
        }

        public void setDisplayWidth(int displayWidth) {
            this.displayWidth = displayWidth;
        }

        public int getDisplayHeight() {
            return displayHeight;
        }

        public void setDisplayHeight(int displayHeight) {
            this.displayHeight = displayHeight;
        }
    }

}
