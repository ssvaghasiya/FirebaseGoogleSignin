package com.firebaseexample.ui.chat.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebaseexample.R
import com.firebaseexample.apputils.Utils
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivityChatBinding
import com.firebaseexample.interfaces.TopBarClickListener
import com.firebaseexample.model.Message
import com.firebaseexample.ui.chat.utils.MessagesAdapter
import com.firebaseexample.ui.chat.view.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class ChatViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityChatBinding
    private lateinit var mContext: Context
    lateinit var messagesAdapter: MessagesAdapter
    var messages: ArrayList<Message>? = null

    var senderRoom: String? = null
    var receiverRoom:String? = null
    var senderUid:String? = null


    var database: FirebaseDatabase? = null

    fun setBinder(binder: ActivityChatBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {
        messages = ArrayList()

        val name: String = (mContext as Activity).intent.getStringExtra("name")!!
        val receiverUid: String = (mContext as Activity).intent.getStringExtra("uid")!!
        senderUid = FirebaseAuth.getInstance().uid

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        binder.recyclerView.setLayoutManager(LinearLayoutManager(mContext))
        messagesAdapter = MessagesAdapter(mContext, senderRoom!!, receiverRoom!!)
        binder.recyclerView.adapter = messagesAdapter
        messagesAdapter.setEventListener(object : MessagesAdapter.EventListener {

            override fun onItemClick(pos: Int, item: Message) {
            }
        })

        database = FirebaseDatabase.getInstance()
        database!!.reference.child("chats")
            .child(senderRoom!!)
            .child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages!!.clear()
                    for (snapshot1 in snapshot.children) {
                        val message = snapshot1.getValue(Message::class.java)
                        message?.messageId = (snapshot1.key)
                        messages!!.add(message!!)
                    }
                    messagesAdapter.addAll(messages!!)
                    messagesAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        (mContext as ChatActivity).getSupportActionBar()?.setTitle(name)
        (mContext as ChatActivity).getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

    }


    inner class SlideMenuClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getContext(), view!!)
            if (value.equals(getLabelText(R.string.menu))) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onBackClicked(view: View?) {
            (mContext as Activity).finish()
        }
    }


    inner class ViewClickHandler {

        fun onSendMessage(view: View) {
            val messageTxt: String = binder.messageBox.getText().toString()

            val date = Date()
            val message = Message(messageTxt, senderUid!!, date.time)
            binder.messageBox.setText("")

            val randomKey = database!!.reference.push().key

            val lastMsgObj = HashMap<String, Any>()
            lastMsgObj["lastMsg"] = message.message!!
            lastMsgObj["lastMsgTime"] = date.time

            database!!.reference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
            database!!.reference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)

            database!!.reference.child("chats")
                .child(senderRoom!!)
                .child("messages")
                .child(randomKey!!)
                .setValue(message).addOnSuccessListener {
                    database!!.reference.child("chats")
                        .child(receiverRoom!!)
                        .child("messages")
                        .child(randomKey!!)
                        .setValue(message).addOnSuccessListener { }
                }

        }
    }

    fun onCreateOptionsMenu(menu: Menu?) {
        (mContext as Activity).menuInflater.inflate(R.menu.chat_menu, menu)
    }

    fun onSupportNavigateUp() {
        (mContext as Activity).finish()
    }

}



