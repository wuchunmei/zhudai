package com.zhudai.call.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zhudai.R;
import com.zhudai.call.CallLogUtils;
import com.zhudai.call.entity.CallLogInfo;
import com.zhudai.call.entity.CallNumberBo;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by wuchunmei on 9/16/17.
 */
public class CallDetailAdapter extends BaseAdapter {

    private List<CallLogInfo> mList = new ArrayList<>();
    private List<CallNumberBo> mNumberList = new ArrayList<>();
    private final Context mContext;

    public CallDetailAdapter(Context context) {
        super();
        queryContact();
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.call_detail_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CallLogInfo entity = (CallLogInfo) this.getItem(position);
        if (entity != null) {
            if (entity.getName() != null) {
                viewHolder.number.setText(entity.getNumber() + "(" + entity.getName() + ")");
            } else {
                //如果通话记录中读不出姓名，则从通讯录中匹配，如果通讯录没有备注名，则不显示
                String _name = matchName(entity.getNumber());
                if (_name != null) {
                    viewHolder.number.setText(entity.getNumber() + "(" + _name + ")");
                } else {
                    viewHolder.number.setText(entity.getNumber());
                }
            }
            if (entity.getType() == 3) {
                viewHolder.type.setTextColor(mContext.getResources().getColor(R.color.text_selected));
            } else {
                viewHolder.type.setTextColor(mContext.getResources().getColor(R.color.call_text_color));
            }
            viewHolder.type.setText(CallLogUtils.getType(entity.getType()));
            viewHolder.local.setText(entity.getLocation());
            if (entity.getDuration() != 0) {
                viewHolder.duration.setText(CallLogUtils.getTime(String.valueOf(entity.getDuration())));
            } else {
                viewHolder.duration.setText("0秒");
            }

            viewHolder.date.setText(CallLogUtils.getHour(entity.getDate()));
        }


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.number)
        TextView number;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.data)
        TextView date;
        @BindView(R.id.local)
        TextView local;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void notifyDataSetChanged(List<CallLogInfo> list) {
        mList.clear();
        mList.addAll(list);
        super.notifyDataSetChanged();
    }

    /**
     * 从本地数据库中读取出通讯录
     *
     * @return
     */
    private List<CallNumberBo> queryContact() {
        DataSupport.findAllAsync(CallNumberBo.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                mNumberList = (List<CallNumberBo>) t;
            }
        });
        return mNumberList;
    }

    /**
     * 当通话记录姓名查询不到时去通讯录匹配,这里的匹配原则是通话记录中的电话号码必须跟通讯录备注名的电话号码一模一样,才能找到
     * 如果像这样子的情况：通讯录中保存的电话号码是加上区号保存，比如：179511528753468这样子把区号页保存，而通话记录中如果是
     * 1528753468这样子不加区号的拨打,那么备注名是找不到的，必须一模一样才可以匹配成功，目前是按这样子的原则去匹配。
     */
    private String matchName(String number) {
        String name = null;
        //遍历通讯录里面的所有电话号码，如果通话记录中的电话号码在通讯录中，则获取其对应的备注姓名
        List<CallNumberBo> list = filterList(mNumberList);
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

    /**
     * 去重（同一个一模一样的号码,留一个就可以了）
     *
     * @param list
     * @return
     */
    private List<CallNumberBo> filterList(List<CallNumberBo> list) {
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

}
