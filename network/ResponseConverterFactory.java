//package com.zhudai.network;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.gson.Gson;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//
//import okhttp3.ResponseBody;
//import retrofit2.Converter;
//import retrofit2.Retrofit;
//
///**
// * 从GsonConverterFactory修改
// * https://github.com/square/retrofit/blob/master/retrofit-converters/gson/src/main/java/retrofit2/converter/gson/GsonConverterFactory.java
// *
// */
//public class ResponseConverterFactory extends Converter.Factory {
//
//    /**
//     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
//     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
//     */
//    public static ResponseConverterFactory create() {
//        return create(new Gson());
//    }
//
//    /**
//     * Create an instance using {@code gson} for conversion. Encoding to JSON and
//     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
//     */
//    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
//    public static ResponseConverterFactory create(Gson gson) {
//        if (gson == null) throw new NullPointerException("gson == null");
//        return new ResponseConverterFactory(gson);
//    }
//
//    private final Gson gson;
//
//    private ResponseConverterFactory(Gson gson) {
//        this.gson = gson;
//    }
//
//    @Override
//    public Converter<ResponseBody, ResponseResult> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//        Log.v("Converter", type.toString() + "***" + type.getClass().getName());
//
//        if (!(type instanceof ParameterizedType)) return null;
//        ParameterizedType parameterizedType = (ParameterizedType) type;
//        if (parameterizedType.getRawType() != ResponseResult.class) return null;
//        Type actualType = parameterizedType.getActualTypeArguments()[0];
//        Type[] types = parameterizedType.getActualTypeArguments();
//        for (int i = 0; i < types.length; i++) {
//            Type tt = types[i];
//            Log.v("Converter", tt.toString() + "***" + tt.getClass().getName());
//        }
//
//        if (actualType instanceof ParameterizedType) {
//            ParameterizedType secondParameterizedType = (ParameterizedType) actualType;
//            boolean flag = false;
//            Type rawType = secondParameterizedType.getRawType();
//            if (rawType instanceof Class) {
//                Class clazz = (Class) rawType;
//                flag = List.class.isAssignableFrom(clazz);
//            }
//            if (secondParameterizedType.getRawType() == List.class || flag) {
//                return new ResponseResultListConverter(secondParameterizedType.getActualTypeArguments()[0]);
//            }
//        }
//        return new ResponseResultConverter(actualType);
//    }
//
//
//    static class ResponseResultConverter<T> implements Converter<ResponseBody, ResponseResult> {
//        Type type;
//
//        public ResponseResultConverter(Type type) {
//            this.type = type;
//        }
//
//        @Override
//        public ResponseResult convert(ResponseBody value) throws IOException {
//            try {
//                String data = value.string();
//                if (TextUtils.isEmpty(data)) {
//                    return ResponseResult.responseDataNoEnough;
//                }
//                Log.v("ResponseBody", data);
//                JSONObject json = new JSONObject(data);
//
//                int code = json.optInt("code", 0);
//                boolean success = code == ResponseResult.RESPONCE_CODE_SUCCESS;
//                ResponseResult clientObject = new ResponseResult(success, code);
//                if (json.has("msg")) {
//                    clientObject.msg = json.getString("msg");
//                }
//                if (!success) {
//                    return clientObject;
//                }
//                if (!json.has("data")) {
//                    return ResponseResult.responseDataParseError;
//                }
//                JSONObject jsonData = json.getJSONObject("data");
//                if (type != Object.class) {
//                    Class clazz = (Class) type;
//                    Constructor constructor = clazz.getDeclaredConstructor(JSONObject.class);
//                    constructor.setAccessible(true);
//                    Object obj = constructor.newInstance(jsonData);
//                    clientObject.data = obj;
//                } else {
//                    clientObject.data = jsonData;
//                }
//                return clientObject;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            return ResponseResult.responseDataParseError;
//        }
//
//    }
//
//    static class ResponseResultListConverter<T> implements Converter<ResponseBody, ResponseResult> {
//        Type type;
//
//        public ResponseResultListConverter(Type type) {
//            this.type = type;
//        }
//
//        @Override
//        public ResponseResult convert(ResponseBody value) throws IOException {
//            String data = value.string();
//            if (TextUtils.isEmpty(data)) {
//                return ResponseResult.responseDataNoEnough;
//            }
//            try {
//                JSONObject json = new JSONObject(data);
//
//                int code = json.optInt("code", 0);
//                boolean success = code == ResponseResult.RESPONCE_CODE_SUCCESS;
//                ResponseResult clientObject = new ResponseResult(success, code);
//                if (json.has("msg")) {
//                    clientObject.msg = (json.getString("msg"));
//                }
//                if (!success) {
//                    return clientObject;
//                }
//                if (!json.has("data")) {
//                    return ResponseResult.responseDataParseError;
//                }
//                JSONObject jsonData = json.getJSONObject("data");
//                JSONArray array = null;
//                if (jsonData.has("list")) {
//                    array = jsonData.optJSONArray("list");
//                } else if (jsonData.has("data")) {
//                    array = jsonData.optJSONArray("data");
//                } else {
//                    array = json.getJSONArray("data");
//                }
//
//                List<Entity> entityList = new ArrayList<Entity>();
//                if (array != null && array.length() > 0) {
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject o = array.getJSONObject(i);
//                        Entity bo;
//                        if (type != Object.class) {
//                            //TODO 单独item的错误不应该影响整个list
//                            Class clazz = (Class) type;
//                            Constructor constructor = clazz.getDeclaredConstructor(JSONObject.class);
//                            constructor.setAccessible(true);
//                            Object obj = constructor.newInstance(o);
//                            bo = (Entity) obj;
//                            entityList.add(bo);
//                        }
//                    }
//                }
//                clientObject.data = entityList;
//                return clientObject;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            return ResponseResult.responseDataParseError;
//        }
//
//    }
//}
