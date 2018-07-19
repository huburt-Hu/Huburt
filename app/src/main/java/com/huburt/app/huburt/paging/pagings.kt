package com.huburt.app.huburt.paging

import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.arch.paging.PageKeyedDataSource
import android.arch.paging.PagedListAdapter
import android.arch.paging.RxPagedListBuilder
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.huburt.app.huburt.R

/**
 * Created by hubert on 2018/7/6.
 *
 */
data class User(val name: String)


class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: User?) {
        itemView.findViewById<TextView>(R.id.tv_name).apply {
            text = item?.name
        }
    }
}

class UserPageListAdapter : PagedListAdapter<User, UserViewHolder>(object : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User?, newItem: User?): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User?, newItem: User?): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class UserViewModel : ViewModel() {
    private val dataSourceFactory = object : DataSource.Factory<Int, User>() {
        override fun create(): DataSource<Int, User> {
            return object : PageKeyedDataSource<Int, User>() {
                override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Int>, callback: PageKeyedDataSource.LoadInitialCallback<Int, User>) {

                }

                override fun loadBefore(params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, User>) {

                }

                override fun loadAfter(params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, User>) {

                }
            }
        }
    }
    val observable = RxPagedListBuilder(dataSourceFactory, 10).buildObservable()
}