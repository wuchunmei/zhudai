package com.zhudai.call.presenter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.zhudai.call.CallLogContract;
import com.zhudai.call.CallLogUtils;
import com.zhudai.call.entity.CallLogInfo;
import com.zhudai.call.entity.CallNumberBo;
import com.zhudai.login.UserDataManager;
import com.zhudai.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wuchunmei on 9/18/17.
 */
public class LocalCallPresenter implements CallLogContract.CallLogPresenterCallBack {
    private Context mContext;
    private CallLogContract.CallLogView mCallLogView;

    public LocalCallPresenter(Context mContext, CallLogContract.CallLogView callLogView) {
        this.mContext = mContext;
        this.mCallLogView = callLogView;
    }


    @Override
    public void loadCallLogs(int year, int month) {
        queryContactPhoneNumber(year, month);
    }

    /**
     * 从通话记录中查询出数据，然后添加到本地数据库，方便后续取数据
     */
    private void queryCallLogsFromLocal(final int year, final int month) {
        CallLogUtils.syncCalllog(mContext);

        //当需要根据日期筛选数据时进行刷选
        if (year != 0 && month != 0) {
            queryCallLogFromData(year, month);
        }
    }

    /**
     * 根据选择的年月进行查询
     *
     * @param year
     * @param month
     */

    private void queryCallLogFromData(int year, int month) {
        String _month = String.valueOf(month);
        String _year = String.valueOf(year);
        List<CallLogInfo> callList = DataSupport.where("month = ? and year = ?", _month, _year).order("date desc").find(CallLogInfo.class);
        handleCallInfo(callList);
    }

    /**
     * 处理结果
     *
     * @param list
     */
    private void handleCallInfo(List<CallLogInfo> list) {
        int sum = 0;
        int average = 0;
        int year = 0;
        int month = 0;
        List<CallLogInfo> lastCallList = new ArrayList<>();
        List<CallLogInfo> monthCallList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            //获取自安装开始的数据
            lastCallList.clear();
            lastCallList.addAll(list);

            int size = lastCallList.size();
            for (int i = 0; i < lastCallList.size(); i++) {
                int duration = list.get(i).getDuration();
                year = list.get(i).getYear();
                month = list.get(i).getMonth();
                sum += duration;
            }
            //这个是为了计算当月有通话记录的天数
            monthCallList.clear();
            monthCallList.addAll(filterList(lastCallList));
            if (monthCallList != null && monthCallList.size() > 0) {
                average = sum / monthCallList.size();
            }
            if (average != 0) {
                mCallLogView.fillAverageCall(CallLogUtils.getTime(String.valueOf(average)));
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append("       ");
                sb.append("0");
                mCallLogView.fillAverageCall(sb.toString());
            }

            mCallLogView.fillRecord(String.valueOf(size));
            mCallLogView.fillYearAndMonth(year, month);

        } else {
            mCallLogView.fillRecord(String.valueOf(0));
            mCallLogView.fillAverageCall(String.valueOf(0));
            mCallLogView.showEmptyView(list);
        }
        //获取到从最新安装时刻起的通话列表
        mCallLogView.notifyCallLogListAdapter(monthCallList);
    }

    /**
     * 过滤数据,以天为单位(因为查询出来的数据是每条通话记录为单位，但是一天有好多条记录，而通话记录列表要求显示的是以天为item项)
     */
    private List<CallLogInfo> filterList(List<CallLogInfo> list) {
        Map<Integer, CallLogInfo> map = new TreeMap<Integer, CallLogInfo>();
        List<CallLogInfo> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CallLogInfo bo = list.get(i);
            int day = bo.getDay();
            map.put(day, bo);
        }
        if (!map.isEmpty()) {
            Map<Integer, CallLogInfo> result = Utils.sortMapByKey(map);
            for (Map.Entry<Integer, CallLogInfo> entry : result.entrySet()) {
                newList.add(entry.getValue());
            }
        }
        return newList;
    }

    /**
     * 查询通讯录,并保存到本地数据库
     */
    public void queryContactPhoneNumber(final int year, final int month) {
        DataSupport.deleteAll(CallNumberBo.class);
        Observable.create(new ObservableOnSubscribe<List<CallNumberBo>>() {
            Cursor cursor = null;
            List<CallNumberBo> list = new ArrayList<>();

            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CallNumberBo>> e) throws Exception {
                try {
                    String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                    cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            cols, null, null, null);
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String number = cursor.getString(numberFieldColumnIndex).replaceAll(" ", "");
                        int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                        String name = cursor.getString(nameFieldColumnIndex).replaceAll(" ", "");
                        CallNumberBo numberBo = new CallNumberBo(number, name);
                        list.add(numberBo);
                    }
                    if (list != null && list.size() > 0) {
                        DataSupport.saveAll(list);
                        e.onNext(list);
                        e.onComplete();
                    }

                } catch (Exception ex) {
                    e.onError(ex);
                } finally {
                    cursor.close();
                }
            }
        })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CallNumberBo>>() {
                    @Override
                    public void accept(List<CallNumberBo> callNumberBos) throws Exception {
                        if (callNumberBos != null && callNumberBos.size() > 0) {
                            queryCallLogsFromLocal(year, month);
                        }

                    }
                });

    }
}



