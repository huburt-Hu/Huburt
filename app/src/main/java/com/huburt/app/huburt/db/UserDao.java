package com.huburt.app.huburt.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.huburt.app.huburt.bean.User;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {
    @Query("SELECT * FROM tb_user")
    Flowable<List<User>> getAll();

    @Query("SELECT * FROM tb_user WHERE uid IN (:userIds)")
    Flowable<List<User>> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM tb_user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    Flowable<User> findByName(String first, String last);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Delete()
    void delete(User user);

}