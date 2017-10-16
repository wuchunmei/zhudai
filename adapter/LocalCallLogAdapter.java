package com.zhudai.call.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhudai.R;
import com.zhudai.call.CallLogUtils;
import com.zhudai.call.entity.CallLogInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuchunmei on 9/18/17.
 */
public class LocalCallLogAdapter extends BaseAdapter {
    private List<CallLogInfo> mCallList = new ArrayList<>();
    private Context mContext;

    public LocalCallLogAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCallList == null ? 0 : mCallList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCallList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.call_log_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CallLogInfo localCallLog = (CallLogInfo) this.getItem(position);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(calendar.YEAR);
        int currentMonth = calendar.get(calendar.MONTH) + 1;
        int currentDay = calendar.get(calendar.DAY_OF_MONTH);
        if (localCallLog != null) {
            int year = localCallLog.getYear();
            int month = localCallLog.getMonth();
            int day = localCallLog.getDay();
            if (year == currentYear && month == currentMonth) {
                if (day == currentDay) {
                    viewHolder.date_title.setText(mContext.getResources().getString(R.string.today));
                } else if (day == currentDay - 1) {
                    viewHolder.date_title.setText(mContext.getResources().getString(R.string.yesterday));
                } else {
                    viewHolder.date_title.setText(month + "月" + day + "日");
                }
            } else {
                viewHolder.date_title.setText(month + "月" + day + "日");
            }
            //查询当日数据
            List<CallLogInfo> dayCallList = CallLogUtils.queryDayforCall(year, month, day);
            if (dayCallList != null) {
                int durations = 0;
                for (int i = 0; i < dayCallList.size(); i++) {
                    CallLogInfo dayCallBo = dayCallList.get(i);
                    durations += (dayCallBo.getDuration());
                }
                int callTimes = dayCallList.size();
                viewHolder.call_times.setText(String.valueOf(callTimes));
                if(durations != 0){
                    viewHolder.call_duration_num.setText(CallLogUtils.getTime(String.valueOf(durations)));
                }else {
                    viewHolder.call_duration_num.setText("0秒");
                }
            }
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.date_title)
        TextView date_title;
        @BindView(R.id.call_times)
        TextView call_times;
        @BindView(R.id.call_duration_num)
        TextView call_duration_num;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void notifyData(List<CallLogInfo> list) {
        mCallList.clear();
        mCallList.addAll(list);
        super.notifyDataSetChanged();
    }
}
