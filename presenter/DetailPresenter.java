package com.zhudai.call.presenter;


import android.content.Context;

import com.zhudai.call.CallLogContract;
import com.zhudai.call.CallLogUtils;
import com.zhudai.call.entity.CallLogInfo;

import java.util.List;


/**
 * Created by wuchunmei on 2017/9/16.
 */

public class DetailPresenter implements CallLogContract.DetailPresenterCallBack {

    private CallLogContract.DetailView mDetailView;
    private Context mContext;

    public DetailPresenter(Context context, CallLogContract.DetailView detailView) {
        this.mDetailView = detailView;
        this.mContext = context;
    }

    @Override
    public void loadLocalCallData(int year, int month, int day) {
        List<CallLogInfo> list = CallLogUtils.queryDayforCall(year, month, day);
        if (list != null && list.size() > 0){
            mDetailView.notifyDataSetChanged(list);
        }
    }

}
