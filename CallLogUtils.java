package com.zhudai.call;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.zhudai.call.entity.CallLogInfo;
import com.zhudai.call.entity.CallNumberBo;
import com.zhudai.login.UserDataManager;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import timber.log.Timber;

/**
 * Created by wuchunmei on 9/14/17.
 * 读取手机通话记录工具类
 */
public class CallLogUtils {

    private static String TAG = CallLogUtils.class.getSimpleName();

    public static List<CallLogInfo> getCallLog(Context context, long minDate, int limit) {
        List<CallLogInfo> list = new ArrayList<>();
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CallLog.Calls.CONTENT_URI;
            String[] projection =
                    {
                            CallLog.Calls.NUMBER,
                            CallLog.Calls.DATE,
                            CallLog.Calls.TYPE,
                            CallLog.Calls.CACHED_NAME,
                            CallLog.Calls.DURATION,
                            CallLog.Calls.GEOCODED_LOCATION
                    };
            String limitStr = limit > 0 ? (" LIMIT " + limit) : "";
            String selection = null;
            String[] selectionArgs = null;
            if (minDate > 0) {
                selection = CallLog.Calls.DATE + ">?";
                selectionArgs = new String[]{String.valueOf(minDate)};
            }
            Cursor cursor = cr.query(uri, projection, selection, selectionArgs, CallLog.Calls.DATE + " DESC" + limitStr);
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                        int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));//通话时长
                        long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));//拨打时间
                        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));//电话号码
                        String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));//姓名
                        String location = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));//归属地

                        //高版本系统里会返回大于3的值，当作未接处理
                        if (type > 3) type = 3;

                        CallLogInfo callLogInfo = new CallLogInfo();
                        callLogInfo.setDate(date);
                        callLogInfo.setType(type);
                        callLogInfo.setName(name);
                        callLogInfo.setNumber(number);
                        callLogInfo.setDuration(duration);
                        callLogInfo.setLocation(location);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);
                        int year = calendar.get(calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        callLogInfo.setYear(year);
                        callLogInfo.setMonth(month + 1);
                        callLogInfo.setDay(day);

                        list.add(callLogInfo);
                        Timber.v(callLogInfo.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 同步数据库最大日期以后的通话记录
     *
     * @param context
     */
    public static void syncCalllog(Context context) {
        long maxData = DataSupport.max(CallLogInfo.class, "date", long.class);
        if (maxData == 0) maxData = UserDataManager.INSTANCE.getAppInstalledTime(context);
        List<CallLogInfo> list = getCallLog(context, maxData, 0);
        if (list != null && list.size() > 0) {
            DataSupport.saveAll(list);
        }
    }

    public static String getType(int type) {
        switch (type) {  //呼入1/呼出2/未接3
            case CallLog.Calls.INCOMING_TYPE:
                return "呼入";
            case CallLog.Calls.OUTGOING_TYPE:
                return "呼出";
            case CallLog.Calls.MISSED_TYPE:
                return "未接";
            default:
                break;

        }
        return null;
    }

    public static String getDate(String date) {
        Date callDate = new Date(Long.parseLong(date));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(callDate);
    }

    /**
     * 获取当天的具体时间，时：分
     *
     * @param date
     * @return
     */
    public static String getHour(long date) {
        Calendar calendar = Calendar.getInstance();
        Date d = new Date(date);
        calendar.setTime(d);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//小时
        int minute = calendar.get(Calendar.MINUTE);//分
        return (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute);
    }

    public static String getDuration(String duration) {
        int callDuration = Integer.parseInt(duration);
        if (callDuration <= 0) return "00:00:00";
        int h = callDuration / 3600;
        int m = callDuration % 3600 / 60;
        int s = callDuration % 60;
        String hour = h + "";
        String min = m + "";
        String sec = s + "";
        if (h < 10) {
            hour = "0" + h;
        }
        if (m < 10) {
            min = "0" + m;
        }
        if (s < 10) {
            sec = "0" + s;
        }
        return hour + ":" + min + ":" + sec;
    }

    /**
     * 返回x分x秒的形式
     *
     * @param duration
     * @return
     */
    public static String getTime(String duration) {
        int callDuration = Integer.parseInt(duration);
        if (callDuration <= 0) return "00:00:00";
        int h = callDuration / 3600;
        int m = callDuration % 3600 / 60;
        int s = callDuration % 60;
        String min = m + "";
        String sec = s + "";
        if (m < 10) {
            min = "0" + m;
        }
        if (s < 10) {
            sec = "0" + s;
        }
        if (h > 0) {
            m += h * 60;
            min = m + "";
        }
        return min + "分" + sec + "秒";
    }

    /**
     * 查询数据库当天的数据
     *
     * @param year
     * @param month
     * @return
     */
    public static List<CallLogInfo> queryDayforCall(int year, int month, int day) {
        String _month = String.valueOf(month);
        String _year = String.valueOf(year);
        String _day = String.valueOf(day);
        List<CallLogInfo> callList = DataSupport.where("month = ? and year = ? and day = ?", _month, _year, _day).order("date desc").find(CallLogInfo.class);
        return callList;
    }

    /**
     * 查询当天的通话录音
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static List<CallLogInfo> queryCallRecordForCurrentDay(int year, int month, int day) {
        String _month = String.valueOf(month);
        String _year = String.valueOf(year);
        String _day = String.valueOf(day);
        List<CallLogInfo> callList = DataSupport.where("month = ? and year = ? and day = ? and callrecod NOT NULL", _month, _year, _day).order("date desc").find(CallLogInfo.class);
        return callList;
    }

    /**
     * 查询该电话号码下的所有通话录音
     *
     * @param number
     * @return
     */
    public static List<CallLogInfo> queryCallRecordForNumber(String number) {
        List<CallLogInfo> callList = DataSupport.where("number = ? and callrecod NOT NULL", number).order("date desc").find(CallLogInfo.class);
        return callList;
    }

    /**
     * 去重（同一个一模一样的号码,留一个就可以了）
     *
     * @param list
     * @return
     */
    private static List<CallNumberBo> filterList(List<CallNumberBo> list) {
        Map<String, CallNumberBo> map = new TreeMap<String, CallNumberBo>();
        List<CallNumberBo> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CallNumberBo bo = list.get(i);
            String number = bo.getNumber();
            map.put(number, bo);
        }
        if (!map.isEmpty()) {
            for (Map.Entry<String, CallNumberBo> entry : map.entrySet()) {
                newList.add(entry.getValue());
            }
        }
        return newList;
    }

    /**
     * 当通话记录姓名查询不到时去通讯录匹配,这里的匹配原则是通话记录中的电话号码必须跟通讯录备注名的电话号码一模一样,才能找到
     * 如果像这样子的情况：通讯录中保存的电话号码是加上区号保存，比如：179511528753468这样子把区号页保存，而通话记录中如果是
     * 1528753468这样子不加区号的拨打,那么备注名是找不到的，必须一模一样才可以匹配成功，目前是按这样子的原则去匹配。
     */
    public static String matchName(String number, List<CallNumberBo> numberList) {
        String name = null;
        //遍历通讯录里面的所有电话号码，如果通话记录中的电话号码在通讯录中，则获取其对应的备注姓名
        List<CallNumberBo> list = filterList(numberList);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getNumber().equals(number)) {
                    name = list.get(i).getName();
                    return name;
                }
            }
        }
        return name;
    }

}
