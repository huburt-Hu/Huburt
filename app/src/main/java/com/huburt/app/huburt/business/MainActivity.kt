package com.huburt.app.huburt.business

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.huburt.app.huburt.R
import com.huburt.app.huburt.business.mzitu.list.MzituFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)

        val fragment = MzituFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fl_root, fragment).commit()
    }
}
