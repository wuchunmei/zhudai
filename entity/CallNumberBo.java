package com.zhudai.call.entity;

import com.zhudai.common.bo.Entity;

import java.io.Serializable;

/**
 * Created by wuchunmei on 9/22/17.
 * 通讯录
 */
public class CallNumberBo extends Entity implements Comparable, Serializable {
    private String number;
    private String name;

    public CallNumberBo(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object object) {
        if (this == object) {
            return 0;
        }
        return -1;
    }
}
