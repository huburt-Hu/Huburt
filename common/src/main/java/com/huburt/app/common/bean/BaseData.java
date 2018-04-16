package com.huburt.app.common.bean;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * Created by Yune on 2017/4/27.
 */
@Keep
public class BaseData<D> implements Serializable{
    public D data;
    public String message;
    public String status;

}
