package com.firebaseexample.ui.main.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebaseexample.R
import com.firebaseexample.databinding.ItemStatusBinding
import com.firebaseexample.model.Status
import com.firebaseexample.model.UserStatus
import com.firebaseexample.ui.main.view.MainActivity
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.callback.StoryClickListeners
import omari.hamza.storyview.model.MyStory
import java.util.*

class TopStatusAdapter() : RecyclerView.Adapter<TopStatusAdapter.MyViewHolder>() {

    private lateinit var mEventListener: EventListener

    private var data = mutableListOf<UserStatus>()
    lateinit var context: Context


    constructor(context: Context) : this() {
        this.context = context
    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }


    interface EventListener {
        fun onItemClick(pos: Int, item: UserStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemBinding = DataBindingUtil.inflate<ItemStatusBinding>(
            inflater,
            R.layout.item_status, parent, false
        )
        return MyViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(p: Int): UserStatus {
        return data[p]

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        try {

            val lastStatus: Status =
                item.statuses!!.get(item.statuses!!.size - 1)

            Glide.with(context).load(lastStatus.imageUrl)
                .into(holder.itemBinding.image)

            holder.itemBinding.circularStatusView.setPortionsCount(item.statuses!!.size)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        holder.itemBinding.root.setOnClickListener {

            mEventListener.onItemClick(position, item)
        }

    }

    fun addAll(mData: List<UserStatus>) {
        data.clear()
        data.addAll(mData)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(internal var itemBinding: ItemStatusBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}