package com.huburt.app.huburt.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hubert on 2018/6/27.
 */
@Entity
public class Student {
    @PrimaryKey
    public int id;
    public int gender;
    public int level;
}
