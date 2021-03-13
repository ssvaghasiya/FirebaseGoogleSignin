package com.firebaseexample.ui.chat.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebaseexample.R
import com.firebaseexample.databinding.DeleteDialogBinding
import com.firebaseexample.databinding.ItemReceiveBinding
import com.firebaseexample.databinding.ItemSentBinding
import com.firebaseexample.model.Message
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionsConfig
import com.github.pgreze.reactions.ReactionsConfigBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessagesAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mEventListener: EventListener

    private var data = mutableListOf<Message>()
    lateinit var context: Context

    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2

    var senderRoom: String? = null
    var receiverRoom: String? = null

    constructor(context: Context, senderRoom: String, receiverRoom: String) : this() {
        this.context = context
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }


    interface EventListener {
        fun onItemClick(pos: Int, item: Message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return if (viewType == ITEM_SENT) {
            val itemBinding = DataBindingUtil.inflate<ItemSentBinding>(
                inflater,
                R.layout.item_sent, parent, false
            )
            SentViewHolder(itemBinding)
        } else {
            val itemBinding = DataBindingUtil.inflate<ItemReceiveBinding>(
                inflater,
                R.layout.item_receive, parent, false
            )
            ReceiverViewHolder(itemBinding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = data.get(position)
        return if (FirebaseAuth.getInstance().uid == message.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(p: Int): Message {
        return data[p]

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        try {
            val reactions = intArrayOf(
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
            )

            val config: ReactionsConfig = ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build()

            val popup = ReactionPopup(context, config) { pos: Int? ->
                if (holder.javaClass == SentViewHolder::class.java) {
                    val viewHolder = holder as SentViewHolder
                    if(pos!! >= 0) {
                        viewHolder.itemBinding.feeling.setImageResource(reactions[pos])
                    }
                    viewHolder.itemBinding.feeling.setVisibility(View.VISIBLE)
                } else {
                    val viewHolder = holder as ReceiverViewHolder
                    if(pos!! >= 0) {
                        viewHolder.itemBinding.feeling.setImageResource(reactions[pos!!])
                    }
                    viewHolder.itemBinding.feeling.setVisibility(View.VISIBLE)
                }
                item.feeling = (pos)
                FirebaseDatabase.getInstance().reference
                    .child("chats")
                    .child(senderRoom!!)
                    .child("messages")
                    .child(item.messageId!!).setValue(item)
                FirebaseDatabase.getInstance().reference
                    .child("chats")
                    .child(receiverRoom!!)
                    .child("messages")
                    .child(item.messageId!!).setValue(item)
                true // true is closing popup, false is requesting a new selection
            }

            if (holder.javaClass == SentViewHolder::class.java) {
                val viewHolder = holder as SentViewHolder
                viewHolder.itemBinding.message.setText(item.message)
                if (item.feeling >= 0) {
                    viewHolder.itemBinding.feeling.setImageResource(reactions[item.feeling])
                    viewHolder.itemBinding.feeling.setVisibility(View.VISIBLE)
                } else {
                    viewHolder.itemBinding.feeling.setVisibility(View.GONE)
                }
                viewHolder.itemBinding.message.setOnTouchListener(View.OnTouchListener { v, event ->
                    popup.onTouch(v, event)
                    false
                })
                viewHolder.itemView.setOnLongClickListener {
                    val view: View =
                        LayoutInflater.from(context).inflate(R.layout.delete_dialog, null)
                    val binding: DeleteDialogBinding = DeleteDialogBinding.bind(view)
                    val dialog: AlertDialog = AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.getRoot())
                        .create()
                    binding.everyone.setOnClickListener(View.OnClickListener {
                        item.message = ("This message is removed.")
                        item.feeling = (-1)
                        FirebaseDatabase.getInstance().reference
                            .child("chats")
                            .child(senderRoom!!)
                            .child("messages")
                            .child(item.messageId!!).setValue(item)
                        FirebaseDatabase.getInstance().reference
                            .child("chats")
                            .child(receiverRoom!!)
                            .child("messages")
                            .child(item.messageId!!).setValue(item)
                        dialog.dismiss()
                    })
                    binding.delete.setOnClickListener(View.OnClickListener {
                        FirebaseDatabase.getInstance().reference
                            .child("chats")
                            .child(senderRoom!!)
                            .child("messages")
                            .child(item.messageId!!).setValue(null)
                        dialog.dismiss()
                    })
                    binding.cancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
                    dialog.show()
                    false
                }
            } else{
                val viewHolder = holder as ReceiverViewHolder
                viewHolder.itemBinding.message.setText(item.message)

                if (item.feeling >= 0) {
                    //message.setFeeling(reactions[message.getFeeling()]);
                    viewHolder.itemBinding.feeling.setImageResource(reactions[item.feeling])
                    viewHolder.itemBinding.feeling.setVisibility(View.VISIBLE)
                } else {
                    viewHolder.itemBinding.feeling.setVisibility(View.GONE)
                }

                viewHolder.itemBinding.message.setOnTouchListener(View.OnTouchListener { v, event ->
                    popup.onTouch(v, event)
                    false
                })

                viewHolder.itemView.setOnLongClickListener {
                    val view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null)
                    val binding = DeleteDialogBinding.bind(view)
                    val dialog = AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.root)
                        .create()
                    binding.everyone.setOnClickListener {
                        item.message = ("This message is removed.")
                        item.feeling = (-1)
                        FirebaseDatabase.getInstance().reference
                            .child("chats")
                            .child(senderRoom!!)
                            .child("messages")
                            .child(item.messageId!!).setValue(item)
                        FirebaseDatabase.getInstance().reference
                            .child("chats")
                            .child(receiverRoom!!)
                            .child("messages")
                            .child(item.messageId!!).setValue(item)
                        dialog.dismiss()
                    }
                    binding.delete.setOnClickListener {
                        FirebaseDatabase.getInstance().reference
                            .child("chats")
                            .child(senderRoom!!)
                            .child("messages")
                            .child(item.messageId!!).setValue(null)
                        dialog.dismiss()
                    }
                    binding.cancel.setOnClickListener { dialog.dismiss() }
                    dialog.show()
                    false
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

//        holder.itemBinding.root.setOnClickListener {
//
//            mEventListener.onItemClick(position, item)
//        }

    }

    fun addAll(mData: List<Message>) {
        data.clear()
        data.addAll(mData)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    inner class SentViewHolder(internal var itemBinding: ItemSentBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    inner class ReceiverViewHolder(internal var itemBinding: ItemReceiveBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}