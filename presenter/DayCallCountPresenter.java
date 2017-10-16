package com.zhudai.call.presenter;

import android.content.Context;

import com.zhudai.call.CallLogContract;
import com.zhudai.call.CallLogUtils;
import com.zhudai.call.entity.CallLogInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wuchunmei on 9/20/17.
 */
public class DayCallCountPresenter implements CallLogContract.DayCountPresenter {

    private CallLogContract.DayCountView mDayCountView;

    public DayCallCountPresenter(Context context, CallLogContract.DayCountView dayCountView) {
        this.mDayCountView = dayCountView;
    }

    @Override
    public void queryCallForDay(int year, int month, int day) {
        List<CallLogInfo> list = CallLogUtils.queryDayforCall(year, month, day);
        if (list != null && list.size() > 0) {
            mDayCountView.fillDate(list);
            int duration = 0;
            for (int i = 0; i < list.size(); i++) {
                duration += (list.get(i).getDuration());
            }
            mDayCountView.setTotalDuration(duration);
        }
        List<CallLogInfo> callListForType = filterListForType(list);
        //根据call类型来查询数据
        for (int i = 0; i < callListForType.size(); i++) {
            int type = callListForType.get(i).getType();
            //未接算进去来电里
            if (type == CallLogInfo.CALLTYPE_MISSEDCALL) {
                type = CallLogInfo.CALLTYPE_INCOMING;
            }
            handlerResultForType(year, month, day, type);
        }
    }

    @Override
    public void queryCallRecordForDay(int year, int month, int day) {
        List<CallLogInfo> list = CallLogUtils.queryCallRecordForCurrentDay(year, month, day);
        if (list != null) {
            mDayCountView.fillRecordDate(list);
        }
    }

    /**
     * 处理根据type查询回来的数据结果
     *
     * @param year
     * @param month
     * @param day
     * @param type
     */
    private void handlerResultForType(int year, int month, int day, int type) {
        switch (type) {
            case CallLogInfo.CALLTYPE_INCOMING:
                //未接算进去来电里
                int size = 0;
                List<CallLogInfo> missedList = queryCallTypeForData(year, month, day, CallLogInfo.CALLTYPE_MISSEDCALL);
                if (missedList != null) {
                    size = missedList.size();
                }
                List<CallLogInfo> comingList = queryCallTypeForData(year, month, day, CallLogInfo.CALLTYPE_INCOMING);
                if (comingList != null) {
                    int comingDuration = 0;
                    for (int j = 0; j < comingList.size(); j++) {
                        int _duration = comingList.get(j).getDuration();
                        comingDuration += _duration;
                    }
                    mDayCountView.setComingTotalDuration(comingDuration);
                    mDayCountView.setComingTotalTimes(size + comingList.size());
                }
                break;
            case CallLogInfo.CALLTYPE_OUTGOING:
                List<CallLogInfo> outCallList = queryCallTypeForData(year, month, day, CallLogInfo.CALLTYPE_OUTGOING);
                if (outCallList != null) {
                    int outDuration = 0;
                    for (int j = 0; j < outCallList.size(); j++) {
                        int _duration = outCallList.get(j).getDuration();
                        outDuration += _duration;
                    }
                    mDayCountView.setCallOutTotalDuration(outDuration);
                    mDayCountView.setCallOutTotalTimes(outCallList.size());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 过滤type,重复的type只保留一个
     *
     * @param list
     * @return
     */
    private List<CallLogInfo> filterListForType(List<CallLogInfo> list) {
        Map<Integer, CallLogInfo> map = new TreeMap<Integer, CallLogInfo>();
        List<CallLogInfo> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CallLogInfo bo = list.get(i);
            int type = bo.getType();
            map.put(type, bo);
        }
        for (Map.Entry<Integer, CallLogInfo> entry : map.entrySet()) {
            newList.add(entry.getValue());
        }
        return newList;
    }

    private List<CallLogInfo> queryCallTypeForData(int year, int month, int day, int type) {
        String _month = String.valueOf(month);
        String _year = String.valueOf(year);
        String _day = String.valueOf(day);
        String _type = String.valueOf(type);
        List<CallLogInfo> callList = DataSupport.where("month = ? and year = ? and day = ? and type = ?", _month, _year, _day, _type).order("date desc").find(CallLogInfo.class);
        return callList;
    }

}
