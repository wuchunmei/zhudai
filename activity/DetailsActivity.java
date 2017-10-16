package com.zhudai.call.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.zhudai.R;
import com.zhudai.call.CallLogContract;
import com.zhudai.call.presenter.DetailPresenter;
import com.zhudai.call.adapter.CallDetailAdapter;
import com.zhudai.call.entity.CallLogInfo;
import com.zhudai.main.BaseActivity;
import com.zhudai.util.Utils;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wuchunmei on 9/18/17.
 */
public class DetailsActivity extends BaseActivity implements CallLogContract.DetailView {
    @BindView(R.id.detail_list)
    ListView mDetailListView;

    private CallDetailAdapter mAdapter;
    private DetailPresenter mDetailPresenter;
    private long mDate;
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        ButterKnife.bind(this);
        mDate = this.getIntent().getLongExtra("call_date", 0);
        mDetailPresenter = new DetailPresenter(this, this);
        initDate();
        initView();
        getCallList();
    }

    private void getCallList() {
        mDetailPresenter.loadLocalCallData(mYear, mMonth, mDay);
    }

    private void initDate() {
        Date d = new Date(mDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        mYear = calendar.get(calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void initView() {
        initActionbar();
        String week = Utils.getWeekText(this, mYear, mMonth - 1, mDay);
        String actionBarTitle = mMonth + "月" + mDay + "日" + "(" + week + ")";
        setActionbarTitle(actionBarTitle);
        mAdapter = new CallDetailAdapter(this);
        mDetailListView.setAdapter(mAdapter);
    }

    @Override
    public void notifyDataSetChanged(List<CallLogInfo> list) {
        mAdapter.notifyDataSetChanged(list);
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
