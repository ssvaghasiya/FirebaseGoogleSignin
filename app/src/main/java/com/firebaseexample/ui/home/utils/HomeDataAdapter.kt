package com.firebaseexample.ui.home.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebaseexample.R
import com.firebaseexample.databinding.ItemHomeBinding

class HomeDataAdapter() : RecyclerView.Adapter<HomeDataAdapter.MyViewHolder>() {

    private lateinit var mEventListener: EventListener

    private var data = mutableListOf<String>()
    lateinit var context: Context
    var isClickable = true


    constructor(context: Context) : this() {
        this.context = context
    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }


    interface EventListener {
        fun onItemClick(pos: Int, item: String)
        fun onSkipClicked(pos: Int, item: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemBinding = DataBindingUtil.inflate<ItemHomeBinding>(
            inflater,
            R.layout.item_home, parent, false
        )
        return MyViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(p: Int): String {
        return data[p]

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.setIsRecyclable(false)
//        val item = getItem(position)
        try {

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    fun addAll(mData: List<String>) {
        data.clear()
        data.addAll(mData)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(internal var itemBinding: ItemHomeBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}