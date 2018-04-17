package com.huburt.app.huburt.business.mzitu.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.huburt.app.common.base.BaseActivity
import com.huburt.app.common.ctx.BaseConstants
import com.huburt.app.huburt.R

/**
 * Created by hubert on 2018/4/14.
 *
 */
class MzituDetailActivity : BaseActivity() {

    companion object {
        fun start(context: Context, id: Int) {
            val intent = Intent(context, MzituDetailActivity::class.java)
            intent.putExtra(BaseConstants.ID, id)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_empty)

        val id = intent.extras[BaseConstants.ID] as Int
        val fragment = MzituDetailFragment.newInstance(id)
        supportFragmentManager.beginTransaction().add(R.id.fl_root, fragment).commit()

    }
}