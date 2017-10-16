package com.zhudai.call.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhudai.R;
import com.zhudai.call.CallLogUtils;
import com.zhudai.call.entity.CallLogInfo;
import com.zhudai.call.entity.CallNumberBo;
import com.zhudai.callrecord.adapter.CallRecordAdapter;
import com.zhudai.main.BaseActivity;
import com.zhudai.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuchunmei on 9/26/17.
 */
public class RecordDetailsActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty)
    View mEmptyView;

    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.tip)
    TextView mTip;

    private long mDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private CallRecordAdapter mCallRecordDetailAdapter;
    private List<CallNumberBo> mNumberList = new ArrayList<>();
    List<CallLogInfo> mCallRecordList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail_layout);
        ButterKnife.bind(this);
        mDate = this.getIntent().getLongExtra("call_date", 0);
        initDate();
        initView();
    }

    private void initDate() {
        queryContact();
        Date d = new Date(mDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        mYear = calendar.get(calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mCallRecordList.clear();
        mCallRecordList.addAll(CallLogUtils.queryCallRecordForCurrentDay(mYear, mMonth, mDay));
    }

    /**
     * 从本地数据库中读取出通讯录
     *
     * @return
     */
    private void queryContact() {
        mNumberList.clear();
        mNumberList.addAll(DataSupport.where("name NOT NULL").find(CallNumberBo.class));
    }

    private void initView() {
        initActionbar();
        String week = Utils.getWeekText(this, mYear, mMonth - 1, mDay);
        String actionBarTitle = mMonth + "月" + mDay + "日" + "(" + week + ")";
        setActionbarTitle(actionBarTitle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(this.getResources().getDrawable(R.drawable.divider_white));
        mRecyclerView.addItemDecoration(divider);
        mCallRecordDetailAdapter = new CallRecordAdapter(this, mCallRecordList);
        mCallRecordDetailAdapter.setIsItemClickable(false);
        mRecyclerView.setAdapter(mCallRecordDetailAdapter);

        if (mCallRecordList != null && mCallRecordList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mImage.setBackgroundResource(R.drawable.no_permission);
            mTip.setText("您还没有通话录音～");
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCallRecordDetailAdapter != null){
            mCallRecordDetailAdapter.releaseMediaPlayer();
            mCallRecordDetailAdapter.cancelTimerTask();
        }
    }

//    private class CallRecordDetailAdapter extends CommonAdapter<CallLogInfo> {
//
//        private MediaPlayer mPlayer;
//
//        public CallRecordDetailAdapter(Context context, List<CallLogInfo> list) {
//            super(context, R.layout.detail_callrecord, list);
//            mPlayer = new MediaPlayer();
//        }
//
//        @Override
//        protected void convert(final ViewHolder holder, final CallLogInfo callLogInfo, int position) {
//            Timber.v("CallRecordAdapter", callLogInfo.toString());
//            if (callLogInfo.getType() == CallLogInfo.CALLTYPE_INCOMING) {
//                holder.setImageResource(R.id.ivtype, R.drawable.ic_incoming);
//            } else {
//                holder.setImageResource(R.id.ivtype, R.drawable.ic_outgoing);
//            }
//            String name = TextUtils.isEmpty(callLogInfo.getName()) ? "" : "(" + callLogInfo.getName() + ")";
//            int duration = 0;
//            try {
//                mPlayer.reset();
//                mPlayer.setDataSource(callLogInfo.getCallrecod());
//                mPlayer.prepare();
//                duration = mPlayer.getDuration();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if(!TextUtils.isEmpty(name)){
//                holder.setText(R.id.tvnumber, callLogInfo.getNumber() + name);
//            }else {
//                //如果通话记录中读不出姓名，则从通讯录中匹配，如果通讯录没有备注名，则不显示
//                String _name =CallLogUtils.matchName(callLogInfo.getNumber(), mNumberList);
//                if(!TextUtils.isEmpty(_name)){
//                    holder.setText(R.id.tvnumber, callLogInfo.getNumber() + "(" + _name + ")");
//                }else {
//                    holder.setText(R.id.tvnumber, callLogInfo.getNumber());
//                }
//            }
////            holder.setText(R.id.tvnumber, callLogInfo.getNumber() + name);
//            holder.setText(R.id.tvdetail, DateUtil.formatTime(true, duration) + "  "
//                    + DateUtil.getCommonDateFormat(callLogInfo.getDate()));
//            holder.setImageResource(R.id.ivplay, R.drawable.ic_play);
//
//            holder.setVisible(R.id.llprogress, false);
//            holder.setText(R.id.tvprogresstime, "00:00");
//            holder.setText(R.id.tvtotaltime,  DateUtil.formatTime(false, duration));
//            final SeekBar seekBar = holder.getView(R.id.pbrecord);
//            setSeekBarChangeListener(seekBar);
//            holder.setOnClickListener(R.id.ivplay, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (TextUtils.isEmpty(callLogInfo.getCallrecod())) {
//                        ToastUtil.show("录音文件为空");
//                        return;
//                    }
//                    if (holder.getView(R.id.llprogress).getVisibility() == View.VISIBLE) {
//                        holder.setVisible(R.id.llprogress, false);
//                        holder.setImageResource(R.id.ivplay, R.drawable.ic_play);
//                        mPlayer.stop();
//                        return;
//                    }
//                    holder.setVisible(R.id.llprogress, true);
//                    holder.setImageResource(R.id.ivplay, R.drawable.ic_stop);
//                    try {
//                        mPlayer.reset();
//                        mPlayer.setDataSource(callLogInfo.getCallrecod());
//                        mPlayer.prepare();
//                        seekBar.setMax(mPlayer.getDuration());
//                        setProgress(seekBar);
//                        mPlayer.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        holder.setVisible(R.id.llprogress, false);
//                        ToastUtil.show("录音文件播放失败");
//                    }
//                }
//            });
//        }
//
//        private void setProgress(final ProgressBar progressBar) {
//            mTimer = new Timer();
//            if (mTimer != null) {
//                if (mTimerTask != null) {
//                    mTimerTask.cancel();
//                }
//                // 每次放定时任务前，确保之前任务已从定时器队列中移除
//                mTimerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (mPlayer != null) {
//                            progressBar.setProgress(mPlayer.getCurrentPosition());
//                        }
//                    }
//                };
//                mTimer.schedule(mTimerTask, 0, 1000);
//            }
//
//        }
//
//        private void setSeekBarChangeListener(SeekBar seekBar) {
//            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (mPlayer != null && fromUser) {
//                        mPlayer.seekTo(progress);
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
//        }
//
//        public void releaseMediaPlayer() {
//            if (mPlayer != null) {
//                mPlayer.stop();
//                mPlayer.release();
//                mPlayer = null;
//            }
//        }
//
//        public void cancelTimerTask() {
//            if(mTimer != null){
//                mTimer.cancel();
//                mTimer = null;
//            }
//            if(mTimerTask != null){
//                mTimerTask.cancel();
//                mTimerTask = null;
//            }
//        }
//    }

}
