package com.huburt.app.huburt.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.huburt.app.huburt.bean.Student;
import com.huburt.app.huburt.bean.User;

@Database(entities = {User.class, Student.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract StudentDao studentDao();

    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "database-name")
//                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()//版本升级时，删除所有原表，重新创建
                .build();
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("drop table tb_user");
        }
    };
}