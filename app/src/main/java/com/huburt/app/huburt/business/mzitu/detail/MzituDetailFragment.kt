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
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.github.chrisbanes.photoview.OnViewTapListener
import com.github.chrisbanes.photoview.PhotoView
import com.huburt.app.common.base.BaseFragment
import com.huburt.app.common.ctx.BaseConstants
import com.huburt.app.common.imageloader.ImageLoaderManager
import com.huburt.app.common.imageloader.ImageOptions
import com.huburt.app.huburt.R
import com.huburt.app.huburt.util.DividerGridItemDecoration
import com.huburt.app.huburt.util.buildMzituUrl
import kotlinx.android.synthetic.main.fragment_mzitu_detail.*

/**
 * Created by hubert on 2018/4/13.
 *
 */
class MzituDetailFragment : BaseFragment(), OnViewTapListener {

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
            view_pager.adapter = Adapter(list, this@MzituDetailFragment)
            rv_small.adapter =
                    object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_mzitu_detail, list) {
                        override fun convert(helper: BaseViewHolder, item: String) {
                            val imageView = helper.getView<ImageView>(R.id.iv)
                            ImageLoaderManager.getInstance().load(imageView, buildMzituUrl(item),
                                    ImageOptions.newInstance().placeHolder(R.drawable.ic_insert_photo_black_24dp))
                        }
                    }
            total = list.size
            title_bar.setTitle("0/$total")
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
                title_bar.setTitle("$position/$total")
            }
        })

        viewModel.getData(arguments!!.getInt(BaseConstants.ID))
    }

    override fun onViewTap(view: View?, x: Float, y: Float) {
        changeTopAndBottomBar()
    }

    private fun changeTopAndBottomBar() {
        if (title_bar.visibility == View.VISIBLE) {
            title_bar.animation = AnimationUtils.loadAnimation(context, R.anim.top_out)
            rv_small.animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            title_bar.visibility = View.GONE
            rv_small.visibility = View.GONE
        } else {
            title_bar.animation = AnimationUtils.loadAnimation(context, R.anim.top_in)
            rv_small.animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            title_bar.visibility = View.VISIBLE
            rv_small.visibility = View.VISIBLE
        }
    }

    class Adapter(private val list: List<String>, val listener: OnViewTapListener) : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView = PhotoView(container.context)
            ImageLoaderManager.getInstance().load(photoView, buildMzituUrl(list[position]),
                    ImageOptions.newInstance().placeHolder(R.drawable.ic_insert_photo_black_24dp))
            photoView.setOnViewTapListener(listener)
            container.addView(photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }
    }
}