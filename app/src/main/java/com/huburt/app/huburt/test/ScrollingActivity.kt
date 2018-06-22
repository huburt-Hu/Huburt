package com.huburt.app.huburt.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.huburt.app.huburt.R
import kotlinx.android.synthetic.main.activity_scrolling.*
import timber.log.Timber

class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        toolbar.setNavigationIcon(R.drawable.icon_back)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            val dialogFragment = TestDialogFragment()
            dialogFragment.show(supportFragmentManager, "dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        Timber.i("available:$available")
        if (available != 0) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        }
    }
}
