package com.huburt.app.huburt.business.mzitu.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.github.chrisbanes.photoview.PhotoView
import com.huburt.app.common.base.BaseFragment
import com.huburt.app.common.ctx.BaseConstants
import com.huburt.app.common.imageloader.ImageLoaderManager
import com.huburt.app.huburt.R
import com.huburt.app.huburt.util.DividerGridItemDecoration
import com.huburt.app.huburt.util.buildMzituUrl
import kotlinx.android.synthetic.main.fragment_mzitu_detail.*

/**
 * Created by hubert on 2018/4/13.
 *
 */
class MzituDetailFragment : BaseFragment() {

    companion object {
        fun newInstance(id: Int): MzituDetailFragment {
            val fragment = MzituDetailFragment()
            val bundle = Bundle()
            bundle.putInt(BaseConstants.ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var viewModel: MzituDetailViewModel
    private var total: Int = 0

    override fun getLayoutId(): Int {
        return R.layout.fragment_mzitu_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[MzituDetailViewModel::class.java]
        viewModel.liveData.observe(this, Observer {
            val list = it ?: listOf()
            view_pager.adapter = Adapter(list)
            rv_small.adapter =
                    object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_mzitu_detail, list) {
                        override fun convert(helper: BaseViewHolder, item: String) {
                            val imageView = helper.getView<ImageView>(R.id.iv)
                            ImageLoaderManager.getInstance().load(imageView, buildMzituUrl(item))
                        }
                    }
            total = list.size
            tv_index.text = "0/$total"
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(rv_small) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(
                    DividerGridItemDecoration(context, ConvertUtils.dp2px(5F), Color.TRANSPARENT))
            addOnItemTouchListener(object : OnItemClickListener() {
                override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                    view_pager.currentItem = position
                }
            })
        }
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                tv_index.text = "$position/$total"
            }
        })

        viewModel.getData(arguments!!.getInt(BaseConstants.ID))
    }


    class Adapter(private val list: List<String>) : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView = PhotoView(container.context)
            ImageLoaderManager.getInstance().load(photoView, buildMzituUrl(list[position]))
            container.addView(photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }
    }
}