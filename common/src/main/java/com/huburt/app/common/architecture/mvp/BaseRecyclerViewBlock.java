package com.huburt.app.common.architecture.mvp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huburt.app.common.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import timber.log.Timber;

/**
 * Created by hubert on 2018/1/29.
 * <p>
 * 该View块不能直接使用,配置参数可以在onCreate方法中调用
 */

public abstract class BaseRecyclerViewBlock<B, P extends BasePresenter> extends BaseLifecycleView<P>
        implements BaseRvView<B, P> {

    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private int page;
    private BaseQuickAdapter<B, BaseViewHolder> mAdapter;

    public BaseRecyclerViewBlock(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseRecyclerViewBlock(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseRecyclerViewBlock(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setContentView(R.layout.block_recycler_view);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()).setTextSizeTitle(14));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onPullToRefresh(page = 0);
            }
        });

        recyclerView.setLayoutManager(provideLayoutManager());
        mAdapter = onCreateAdapter();
        mAdapter.setHeaderAndEmpty(true);
        if (provideLoadingView() != 0) {
            mAdapter.setEmptyView(provideLoadingView(), recyclerView);
        }
        recyclerView.setAdapter(mAdapter);
        Timber.i("init complete");
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.autoRefresh();
    }

    /**
     * 请求数据失败或为空时调用，停止刷新
     */
    @Override
    public void onGetDataFailed() {
        onDataReceived(null);
    }

    protected void finishLoading() {
        refreshLayout.finishRefresh();
        mAdapter.loadMoreComplete();
    }

    @Override
    public void onDataReceived(List<B> beans) {
        finishLoading();
        if (page == 0) {
            mAdapter.setNewData(beans);
            if ((beans == null || beans.size() == 0) && provideEmptyView() != 0) {
                mAdapter.setEmptyView(provideEmptyView(), recyclerView);
            }
        } else {
            if (beans != null && beans.size() > 0) {
                mAdapter.addData(beans);
            } else {
                mAdapter.loadMoreEnd(true);
            }
        }
    }

    protected abstract BaseQuickAdapter<B, BaseViewHolder> onCreateAdapter();

    /**
     * 设置下拉刷新是否可用,在beforeInit中调用
     *
     * @param enable
     */
    public void setEnablePullToRefresh(boolean enable) {
        refreshLayout.setEnableRefresh(enable);
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
     * 设置加载更多是否可用
     *
     * @param enableLoadMore
     */
    public void setEnableLoadMore(boolean enableLoadMore) {
        Timber.i("setEnableLoadMore:%s", enableLoadMore);
        mAdapter.setEnableLoadMore(enableLoadMore);
        if (enableLoadMore) {
            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    onLoadMore(++page);
                }
            }, recyclerView);
        }
    }

    /**
     * 加载更多时回调,子类复写实现数据获取。
     * 默认不执行，需要调用{@link BaseRecyclerViewBlock#setEnableLoadMore}设置为true才会触发该会方法
     *
     * @param page 目标页
     */
    public void onLoadMore(int page) {
        //empty
    }

    public void startRefresh() {
        refreshLayout.autoRefresh();
    }

    public boolean isEnablePullToRefresh() {
        return refreshLayout.isEnableRefresh();
    }

    public boolean isEnableLoadMore() {
        return mAdapter.isLoadMoreEnable();
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
     * 获取当前页数
     *
     * @return page
     */
    public int getCurrentPage() {
        return page;
    }

    /**
     * 子类重写改变LayoutManager
     *
     * @return LayoutManager
     */
    @NonNull
    protected RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    /**
     * 子类重写以修改空布局
     */
    protected int provideEmptyView() {
        return 0;
    }

    /**
     * 子类重写修改加载布局
     */
    protected int provideLoadingView() {
        return 0;
    }
}
