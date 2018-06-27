package com.huburt.app.huburt.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.huburt.app.huburt.bean.Student;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by hubert on 2018/6/27.
 */
@Dao
public interface StudentDao {
    @Query("select * from student")
    Flowable<List<Student>> getAll();
}
