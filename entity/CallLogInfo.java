package com.zhudai.call.entity;

import com.zhudai.common.bo.Entity;

import org.litepal.annotation.Column;

import java.io.Serializable;


/**
 * Created by wuchunmei on 9/14/17.
 */
public class CallLogInfo extends Entity implements Comparable, Serializable {

    //1.呼入2.呼出3未接
    public final static int CALLTYPE_INCOMING = 1;
    public final static int CALLTYPE_OUTGOING = 2;
    public final static int CALLTYPE_MISSEDCALL = 3;

    private String name;
    private String number;
    private int type;
    private long date;
    private int duration;
    //加上这三个字段是为了方便业务根据时间筛选数据
    private int year;
    private int month;
    private int day;
    private String location;
    private String callrecod; //录音文件路径

    @Column(defaultValue = "0")
    private int calllog_uploadstate; //通话记录上传情况 0 未上传  1 已上传

    @Column(defaultValue = "0")
    private int callrecord_uploadstate; //通话录音上传情况 0 未上传  1 已上传

    public CallLogInfo() {
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCallrecod() {
        return callrecod;
    }

    public void setCallrecod(String callrecod) {
        this.callrecod = callrecod;
    }

    public int getCallrecord_uploadstate() {
        return callrecord_uploadstate;
    }

    public void setCallrecord_uploadstate(int callrecord_uploadstate) {
        this.callrecord_uploadstate = callrecord_uploadstate;
    }

    public int getCalllog_uploadstate() {
        return calllog_uploadstate;
    }

    public void setCalllog_uploadstate(int calllog_uploadstate) {
        this.calllog_uploadstate = calllog_uploadstate;
    }

    @Override
    public int compareTo(Object object) {
        if (this == object) {
            return 0;
        }
        if(object instanceof CallLogInfo) {
            CallLogInfo callLogInfo = (CallLogInfo)object;
            if (number == callLogInfo.number
                    && date == callLogInfo.date){
                return 0;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "CallLogInfo{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", duration=" + duration +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", location='" + location + '\'' +
                ", callrecod='" + callrecod + '\'' +
                ", calllog_uploadstate=" + calllog_uploadstate +
                ", callrecord_uploadstate=" + callrecord_uploadstate +
                '}';
    }
}
