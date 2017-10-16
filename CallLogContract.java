package com.zhudai.call;

import android.app.Dialog;

import com.zhudai.call.entity.CallLogInfo;

import java.util.List;

/**
 * Created by wuchunmei on 2017/9/16.
 */

public interface CallLogContract {
    //通话记录列表相关
    interface CallLogView {
        Dialog createDialog();

        void notifyCallLogListAdapter(List<CallLogInfo> list);

        void fillRecord(String size);

        void fillAverageCall(String average);

        void fillYearAndMonth(int year,int month);

        void showEmptyView(List<CallLogInfo> list);

    }

    interface CallLogPresenterCallBack {
        void loadCallLogs(int year, int month);
    }

    //统计当天通话记录详情相关
    interface DayCountView {
        void fillDate(List<CallLogInfo> list);

        void fillRecordDate( List<CallLogInfo> list);

        void setTotalDuration(int duration);

        void setComingTotalDuration(int duration);

        void setCallOutTotalDuration(int duration);

        void setComingTotalTimes(int size);

        void setCallOutTotalTimes(int size);
    }

    interface DayCountPresenter {
        void queryCallForDay(int year, int month, int day);
        void queryCallRecordForDay(int year, int month, int day);
    }


    interface DetailView {
        void initView();

        void notifyDataSetChanged(List<CallLogInfo> list);
    }

    interface DetailPresenterCallBack {
        void loadLocalCallData(int year, int month, int day);
    }


}
