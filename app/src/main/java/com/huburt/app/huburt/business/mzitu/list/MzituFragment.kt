package com.huburt.app.huburt.business.mzitu.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.huburt.app.common.base.BaseFragment
import com.huburt.app.common.imageloader.ImageLoaderManager
import com.huburt.app.common.imageloader.ImageOptions
import com.huburt.app.huburt.R
import com.huburt.app.huburt.bean.BaseResult
import com.huburt.app.huburt.bean.MeiZiTu
import com.huburt.app.huburt.business.mzitu.detail.MzituDetailActivity
import com.huburt.app.huburt.util.DividerGridItemDecoration
import com.huburt.app.huburt.util.buildMzituUrl
import kotlinx.android.synthetic.main.fragment_mzitu.*

/**
 * Created by hubert on 2018/4/13.
 *
 */
class MzituFragment : BaseFragment() {
    companion object {
        fun newInstance(): MzituFragment {
            return MzituFragment()
        }
    }


    private val titles: Array<String> = arrayOf("首页", "最热", "最佳", "性感", "日本", "台湾", "MM")
    private lateinit var mzituViewModel: MzituViewModel
    private var page: Int = 0
    private var currentType: Int = 0
    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        page = 0
        mzituViewModel.getData(currentType, page)
    }

    private val adapter = object : BaseQuickAdapter<MeiZiTu, BaseViewHolder>(R.layout.item_mzitu) {
        override fun convert(helper: BaseViewHolder, item: MeiZiTu) {
            val imageView = helper.getView<ImageView>(R.id.iv)
            ImageLoaderManager.getInstance().load(imageView, buildMzituUrl(item.thumbUrl),
                    ImageOptions.newInstance().placeHolder(R.drawable.ic_insert_photo_black_24dp))

            val height: Int = item.height * (ScreenUtils.getScreenWidth() / 3) / item.width
            val layoutParams = helper.itemView.layoutParams
            layoutParams.height = height
            helper.itemView.layoutParams = layoutParams
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mzitu
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mzituViewModel = ViewModelProviders.of(this)[MzituViewModel::class.java]
        mzituViewModel.result.observe(this, Observer<BaseResult<List<MeiZiTu>>> {
            swipe_refresh.isRefreshing = false
            adapter.loadMoreComplete()
            val list = it?.data
            if (page == 0) {
                adapter.setNewData(list)
            } else {
                if (list != null && list.isNotEmpty()) {
                    adapter.addData(list)
                } else {
                    adapter.loadMoreEnd()
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
        swipe_refresh.setOnRefreshListener(onRefreshListener)
        swipe_refresh.post {
            swipe_refresh.isRefreshing = true
            onRefreshListener.onRefresh()
        }

        adapter.setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener {
            mzituViewModel.getData(currentType, ++page)
        }, recycler_view)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = GridLayoutManager(context, 3)
        recycler_view.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val meiZiTu = this@MzituFragment.adapter.data[position]
                MzituDetailActivity.start(context!!, meiZiTu.id)
            }
        })
        recycler_view.addItemDecoration(
                DividerGridItemDecoration(context, ConvertUtils.dp2px(5F), Color.TRANSPARENT))

        for (title in titles) {
            tab_layout.addTab(tab_layout.newTab().setText(title))
        }
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                currentType = titles.indexOf(tab.text)
                swipe_refresh.isRefreshing = true
                onRefreshListener.onRefresh()
            }
        })
    }
}