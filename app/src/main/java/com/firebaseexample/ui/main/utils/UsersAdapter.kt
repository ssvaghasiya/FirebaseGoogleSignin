package com.firebaseexample.ui.main.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebaseexample.R
import com.firebaseexample.databinding.RowConversationBinding
import com.firebaseexample.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class UsersAdapter() : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private lateinit var mEventListener: EventListener

    private var data = mutableListOf<User>()
    lateinit var context: Context


    constructor(context: Context) : this() {
        this.context = context
    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }


    interface EventListener {
        fun onItemClick(pos: Int, item: User)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemBinding = DataBindingUtil.inflate<RowConversationBinding>(
            inflater,
            R.layout.row_conversation, parent, false
        )
        return MyViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(p: Int): User {
        return data[p]

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        try {
            val senderId = FirebaseAuth.getInstance().uid

            val senderRoom = senderId + item.uid

            FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val lastMsg = snapshot.child("lastMsg").getValue(String::class.java)
                            val time = snapshot.child("lastMsgTime").getValue(Long::class.java)!!!!
                            val dateFormat = SimpleDateFormat("hh:mm a")
                            holder.itemBinding.msgTime.setText(dateFormat.format(Date(time)))
                            holder.itemBinding.lastMsg.setText(lastMsg)
                        } else {
                            holder.itemBinding.lastMsg.setText("Tap to chat")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })


            holder.itemBinding.username.setText(item.name)

            Glide.with(context).load(item.profileImage)
                .placeholder(R.drawable.avatar)
                .into(holder.itemBinding.profile)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        holder.itemBinding.root.setOnClickListener {
            mEventListener.onItemClick(position, item)
        }

    }

    fun addAll(mData: List<User>) {
        data.clear()
        data.addAll(mData)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(internal var itemBinding: RowConversationBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}