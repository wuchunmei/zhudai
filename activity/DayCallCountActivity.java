package com.zhudai.call.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.zhudai.R;
import com.zhudai.call.CallLogContract;
import com.zhudai.call.CallLogUtils;
import com.zhudai.call.entity.CallLogInfo;
import com.zhudai.call.presenter.DayCallCountPresenter;
import com.zhudai.main.BaseActivity;
import com.zhudai.util.ToastUtil;
import com.zhudai.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wuchunmei on 9/18/17.
 */
public class DayCallCountActivity extends BaseActivity implements CallLogContract.DayCountView {

    @BindView(R.id.goto_detail)
    TextView mGotoTv;
    @BindView(R.id.call_log_title)
    TextView mTitleTv;//头部通话记录
    @BindView(R.id.detail_time_sum)
    TextView mCallTimesTv;//通话总数
    @BindView(R.id.detail_duration)
    TextView mDuration;//时长总计
    @BindView(R.id.detail_call_duration)
    TextView mCallOutTv;//去电时长
    @BindView(R.id.detail_incoming_duration)
    TextView mCommingTv;//来电时长
    @BindView(R.id.detail_call)
    TextView mCalloutTimesTv;//今日去电
    @BindView(R.id.detail_incoming)
    TextView mComingCallTimesTv;//今日来电
    @BindView(R.id.call_record)
    TextView mRecordTv;//通话录音
    @BindView(R.id.record_detail)
    TextView mRecordDetailTv;

    //这是从通话记录点击item项传过来的
    private CallLogInfo mCallLogInfo;
    private CallLogContract.DayCountPresenter mDayCountPresenter;
    private Long mDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log_details_activity_layout);
        ButterKnife.bind(this);
        mDayCountPresenter = new DayCallCountPresenter(DayCallCountActivity.this, this);
        mCallLogInfo = (CallLogInfo) this.getIntent().getSerializableExtra("LocalCallBo");
        initView();
        loadDate();
        initActionBarTitle();
    }

    /**
     * 由于今日去电，今日来电的值可能为0
     */
    private void initView() {
        setComingTotalDuration(0);
        setCallOutTotalDuration(0);
        setComingTotalTimes(0);
        setCallOutTotalTimes(0);
    }

    private void initActionBarTitle() {
        initActionbar();
        Date d = new Date(mDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String week = Utils.getWeekText(this, year, month - 1, day);
        String actionBarTitle = month + "月" + day + "日" + "(" + week + ")";
        setActionbarTitle(actionBarTitle);
    }

    private void loadDate() {
        if (mCallLogInfo != null) {
            int year = mCallLogInfo.getYear();
            int month = mCallLogInfo.getMonth();
            int day = mCallLogInfo.getDay();
            mDate = mCallLogInfo.getDate();
            mDayCountPresenter.queryCallForDay(year, month, day);
            mDayCountPresenter.queryCallRecordForDay(year,month,day);
        }
    }

    /**
     * 通话录音详情
     */
    @OnClick(R.id.record_detail)
    void goRecordDetail() {
        Intent intent = new Intent(this,RecordDetailsActivity.class);
        intent.putExtra("call_date", mDate);
        startActivity(intent);
    }

    @Override
    public void fillDate(List<CallLogInfo> list) {
        if (list != null) {
            int callTimes = list.size();
            mTitleTv.setText("通话记录" + "(" + String.valueOf(callTimes) + ")");
            mCallTimesTv.setText(String.valueOf(callTimes) + "次");
        }
    }

    @Override
    public void fillRecordDate(List<CallLogInfo> list) {
        if (list != null) {
            mRecordTv.setText("通话录音" + "(" + String.valueOf(list.size()) + ")");
        }
    }

    @Override
    public void setTotalDuration(int duration) {
        mDuration.setText(CallLogUtils.getTime(String.valueOf(duration)));
    }

    @Override
    public void setComingTotalDuration(int duration) {
        if (duration != 0) {
            mCommingTv.setText(CallLogUtils.getTime(String.valueOf(duration)));
        } else {
            mCommingTv.setText(String.valueOf(0));
        }

    }

    @Override
    public void setCallOutTotalDuration(int duration) {
        if (duration != 0) {
            mCallOutTv.setText(CallLogUtils.getTime(String.valueOf(duration)));
        } else {
            mCallOutTv.setText(String.valueOf(0));
        }

    }

    @Override
    public void setComingTotalTimes(int size) {
        if (size != 0) {
            mComingCallTimesTv.setText(String.valueOf(size));
        } else {
            mComingCallTimesTv.setText(String.valueOf(0));
        }


    }

    @Override
    public void setCallOutTotalTimes(int size) {
        if (size != 0) {
            mCalloutTimesTv.setText(String.valueOf(size));
        } else {
            mCalloutTimesTv.setText(String.valueOf(0));
        }

    }

    @OnClick(R.id.goto_detail)
    void gotoDetail() {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("call_date", mDate);
        startActivity(intent);
    }

    @Override
    protected String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
