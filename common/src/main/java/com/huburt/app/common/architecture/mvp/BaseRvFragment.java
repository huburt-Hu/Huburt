package com.huburt.app.common.architecture.mvp;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huburt.app.common.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hubert
 * <p>
 * Created on 2017/7/6.
 */
public abstract class BaseRvFragment<B, P extends BasePresenter>
        extends BaseViewImplFragment<P> implements BaseRvView<B, P> {

    protected RecyclerView recyclerView;
    protected BaseQuickAdapter<B, BaseViewHolder> mAdapter;

    protected int page;
    private boolean enablePullToRefresh = true;
    private boolean enableLoadMore = true;

    private SmartRefreshLayout refreshLayout;
    private ViewGroup root;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refresh_layout);
        root = (ViewGroup) view.findViewById(R.id.rl_root);
        init();
    }

    private void init() {
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()).setTextSizeTitle(14));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onPullToRefresh(page = 0);
            }
        });
        refreshLayout.setEnableRefresh(enablePullToRefresh);
        if (enablePullToRefresh) refreshLayout.autoRefresh();

        recyclerView.setLayoutManager(getLayoutManager());
        mAdapter = onCreateAdapter(new ArrayList<B>());
        if (enableLoadMore) {
            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    onLoadMore(++page);
                }
            }, recyclerView);
        }
        mAdapter.setEnableLoadMore(enableLoadMore);
        mAdapter.setHeaderAndEmpty(true);
        if (getLoadingView() != 0) {
            mAdapter.setEmptyView(getLoadingView(), recyclerView);
        }
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 请求数据失败或为空时调用，停止刷新
     */
    @Override
    public void onGetDataFailed() {
        onDataReceived(null);
    }

    protected void finishLoading() {
        if (enablePullToRefresh)
            refreshLayout.finishRefresh();
        if (enableLoadMore)
            mAdapter.loadMoreComplete();
    }

    @Override
    public void onDataReceived(List<B> beans) {
        finishLoading();
        if (page == 0) {
            mAdapter.setNewData(beans);
            if ((beans == null || beans.size() == 0) && getEmptyView() != 0) {
                mAdapter.setEmptyView(getEmptyView(), recyclerView);
            }
        } else {
            if (beans != null && beans.size() > 0) {
                mAdapter.addData(beans);
            } else if (enableLoadMore) {
                mAdapter.loadMoreEnd(true);
            }
        }
    }

    public abstract BaseQuickAdapter<B, BaseViewHolder> onCreateAdapter(List<B> list);

    /**
     * 设置下拉刷新是否可用,在onViewCreated()前调用
     *
     * @param enable
     */
    public void setEnablePullToRefresh(boolean enable) {
        enablePullToRefresh = enable;
    }

    /**
     * 下拉刷新时回调,子类复写实现数据获取
     *
     * @param page always equals 0
     */
    public void onPullToRefresh(int page) {
        //empty
    }

    /**
     * 设置加载更多是否可用,在onViewCreated()前调用
     *
     * @param enableLoadMore
     */
    public void setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
    }

    /**
     * 加载更多时回调,子类复写实现数据获取
     *
     * @param page 目标页
     */
    public void onLoadMore(int page) {
        //empty
    }

    public void startRefresh() {
        refreshLayout.autoRefresh();
    }

    /**
     * 获取根布局
     *
     * @return viewGroup
     */
    public ViewGroup getRootView() {
        return root;
    }

    /**
     * 获取RecyclerView
     *
     * @return RecyclerView
     */
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * 获取adapter
     *
     * @return adapter
     */
    public BaseQuickAdapter<B, BaseViewHolder> getAdapter() {
        return mAdapter;
    }

    /**
     * 子类重写改变LayoutManager
     *
     * @return LayoutManager
     */
    @NonNull
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    /**
     * 在onCreateView()方法之后调用
     *
     * @param resId
     */
    protected void setBackgroundResource(@DrawableRes int resId) {
        root.setBackgroundResource(resId);
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    protected void setBackgroundColor(int color) {
        root.setBackgroundColor(color);
    }

    /**
     * 子类重写以修改空布局
     */
    protected int getEmptyView() {
        return 0;
    }

    /**
     * 子类重写修改加载布局
     */
    protected int getLoadingView() {
        return 0;
    }
}
