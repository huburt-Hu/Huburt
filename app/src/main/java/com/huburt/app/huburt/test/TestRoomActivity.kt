package com.huburt.app.huburt.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.huburt.app.common.executor.AsyncTaskManager
import com.huburt.app.common.rx.SchedulersTransformer
import com.huburt.app.huburt.R
import com.huburt.app.huburt.bean.User
import com.huburt.app.huburt.db.AppDatabase
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_test_room.*


/**
 * Created by hubert on 2018/6/27.
 *
 */
class TestRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_room)

        val db = AppDatabase.getInstance(this)

        AsyncTaskManager.getExecutor().runOnBackground {
            db.userDao().insertAll(User().apply { firstName = "hu";lastName = "xi" },
                    User().apply { firstName = "yu";lastName = "jj" })
        }
        db.userDao().all
                .compose(SchedulersTransformer.io_ui())
                .subscribe(Consumer { tv.text = it.toString() })

    }
}