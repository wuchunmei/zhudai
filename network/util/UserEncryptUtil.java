package com.zhudai.common.util;

import java.util.concurrent.atomic.AtomicReference;

public class UserEncryptUtil {

//    public static User getUserFromEncryptedData(Context context, byte[] data) throws JSONException,
//            ClassNotFoundException, IllegalAccessException, InstantiationException {
//        String key = getUserKey(context);
//
//        if (TextUtils.isEmpty(key)) {
//            key = getKeyStringOld(context);
//        }
//        String str = (String) CompressEncrypt
//                .decryptObject(key, data);
//        if (str != null) {
//            User user = (User) JSONUtil.parseString(str, User.class);
//            return user;
//        }
//        return null;
//    }
//
//    public static byte[] encryptUser(Context context, User user) throws JSONException {
//        String json = JSONUtil.toJsonString(user);
//
//        if (json == null) {
//            return null;
//        }
//        String key = getUserKey(context);
//
//        if (TextUtils.isEmpty(key)) {
//            key = getKeyStringOld(context);
//        }
//        byte[] datas = CompressEncrypt.encryptObject(key, json);
//        return datas;
//    }
//
//    @Deprecated
//    public static User getUserFromEncryptedDataOld(Context context, byte[] data)
//            throws JSONException, ClassNotFoundException, IllegalAccessException,
//            InstantiationException {
//        String str = (String) CompressEncrypt
//                .decryptObject(getKeyStringOld(context), data);
//        User user = (User) JSONUtil.parseString(str, User.class);
//        return user;
//    }
//
//    private static AtomicReference<String> userKeyReference = new AtomicReference<String>();
//
//    public static String getUserKey(Context context) {
//        String keyData = userKeyReference.get();
//        if (keyData != null) {
//            return keyData;
//        }
//        synchronized (userKeyReference) {
//            keyData = userKeyReference.get();
//            if (keyData != null) {
//                return keyData;
//            }
//            keyData = EncryptUtil.getUserkeyText();
//            if (keyData != null) {
//                userKeyReference.set(keyData);
//            }
//            return keyData;
//        }
//    }

    private static AtomicReference<String> userCheckString = new AtomicReference<String>();

    /**
     * 登录用的新md5字符串
     * @return
     */
    public static String getUserLoginHttpsCheckString() {
        return "J$*3I3!@6FSP*#+L";
    }

    public static String getUserCheckString() {
        String checkString = userCheckString.get();
        if (checkString != null) {
            return checkString;
        }
        synchronized (userCheckString) {
            checkString = userCheckString.get();
            if (checkString != null) {
                return checkString;
            }

            StringBuilder sb = new StringBuilder("afjlk");
            int index = 2;
            sb.append(index);
            index++;
            sb.append(index);
            index++;
            sb.append(index);
            index = 0;
            sb.append(index);
            sb.append("rsdjk");
            index += 9;
            sb.append(index);
            index -= 7;
            sb.append(index);
            String p = "p";
            sb.append(p);
            sb.append(p);
            checkString = sb.toString();
            userCheckString.set(checkString);
            return checkString;
        }
    }

    /**
     * Https的请求头加密串
     * 
     * @return
     */
    public static String getHttpsUserCheckString() {
        StringBuilder sb = new StringBuilder("dfalz");
        int index = 6;
        sb.append(index);
        index++;
        sb.append(index);
        index++;
        sb.append(index);
        index = 0;
        sb.append(index);
        sb.append("asrqk");
        index += 9;
        sb.append(index);
        index -= 7;
        sb.append(index);
        String p = "q";
        sb.append(p);
        sb.append(p);
        return sb.toString();
    }
}
