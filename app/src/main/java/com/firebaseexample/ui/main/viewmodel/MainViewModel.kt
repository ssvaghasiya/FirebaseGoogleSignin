package com.firebaseexample.ui.main.viewmodel

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebaseexample.R
import com.firebaseexample.apputils.Utils
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivityMainBinding
import com.firebaseexample.interfaces.TopBarClickListener
import com.firebaseexample.model.Status
import com.firebaseexample.model.User
import com.firebaseexample.model.UserStatus
import com.firebaseexample.ui.chat.view.ChatActivity
import com.firebaseexample.ui.main.utils.TopStatusAdapter
import com.firebaseexample.ui.main.utils.UsersAdapter
import com.firebaseexample.ui.main.view.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.callback.StoryClickListeners
import omari.hamza.storyview.model.MyStory
import java.util.*

class MainViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityMainBinding
    private lateinit var mContext: Context
    var database: FirebaseDatabase? = null
    var users: ArrayList<User>? = null
    lateinit var usersAdapter: UsersAdapter
    lateinit var statusAdapter: TopStatusAdapter
    var userStatuses: ArrayList<UserStatus>? = null
    var dialog: ProgressDialog? = null

    var user: User? = null

    fun setBinder(binder: ActivityMainBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {
        dialog = ProgressDialog(mContext)
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        users = ArrayList()
        userStatuses = ArrayList<UserStatus>()

        database?.reference?.child("users")?.child(FirebaseAuth.getInstance().uid!!)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        usersAdapter = UsersAdapter(mContext)
        binder.recyclerView.adapter = usersAdapter
        usersAdapter.setEventListener(object : UsersAdapter.EventListener {
            override fun onItemClick(pos: Int, item: User) {
                val intent = Intent(mContext, ChatActivity::class.java)
                intent.putExtra("name", item.name)
                intent.putExtra("uid", item.uid)
                mContext.startActivity(intent)
            }
        })
        binder.recyclerView.showShimmerAdapter()

        val layoutManager = LinearLayoutManager(mContext)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binder.statusList.setLayoutManager(layoutManager)
        statusAdapter = TopStatusAdapter(mContext)
        binder.statusList.adapter = statusAdapter
        statusAdapter.setEventListener(object : TopStatusAdapter.EventListener {
            override fun onItemClick(pos: Int, item: UserStatus) {
                val myStories: ArrayList<MyStory> = ArrayList<MyStory>()
                for (status in item.statuses!!) {
                    myStories.add(MyStory(status.imageUrl))
                }

                StoryView.Builder((mContext as MainActivity).supportFragmentManager)
                    .setStoriesList(myStories) // Required
                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                    .setTitleText(item.name) // Default is Hidden
                    .setSubtitleText("") // Default is Hidden
                    .setTitleLogoUrl(item.profileImage) // Default is Hidden
                    .setStoryClickListeners(object : StoryClickListeners {
                        override fun onDescriptionClickListener(position: Int) {
                            //your action
                        }

                        override fun onTitleIconClickListener(position: Int) {
                            //your action
                        }
                    }) // Optional Listeners
                    .build() // Must be called before calling show method
                    .show()
            }
        })
        binder.statusList.showShimmerAdapter()

        database!!.reference.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users!!.clear()
                for (snapshot1 in snapshot.children) {
                    val user = snapshot1.getValue(User::class.java)
                    if (!user?.uid.equals(FirebaseAuth.getInstance().uid)) {
                        users!!.add(user!!)
                    }
                }
                usersAdapter.addAll(users!!)
                binder.recyclerView.hideShimmerAdapter()
                usersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        database!!.reference.child("stories").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userStatuses!!.clear()
                    for (storySnapshot in snapshot.children) {
                        val status = UserStatus()
                        status.name = (storySnapshot.child("name").getValue(String::class.java))
                        status.profileImage = (
                                storySnapshot.child("profileImage").getValue(String::class.java)
                                )
                        status.lastUpdated = (
                                storySnapshot.child("lastUpdated").getValue(Long::class.java)!!
                                )
                        val statuses: ArrayList<Status>? = ArrayList<Status>()
                        for (statusSnapshot in storySnapshot.child("statuses").children) {
                            val sampleStatus: Status? = statusSnapshot.getValue(Status::class.java)
                            if (sampleStatus != null) {
                                statuses?.add(sampleStatus)
                            }
                        }
                        status.statuses = (statuses)
                        userStatuses!!.add(status)
                    }
                    statusAdapter.addAll(userStatuses!!)
                    binder.statusList.hideShimmerAdapter()
                    statusAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        binder.bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.status -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    (mContext as Activity).startActivityForResult(intent, 75)
                }
            }
            false
        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            if (data.data != null) {
                dialog!!.show()
                val storage = FirebaseStorage.getInstance()
                val date = Date()
                val reference = storage.reference.child("status").child(date.time.toString() + "")
                reference.putFile(data.data!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            val userStatus = UserStatus()
                            userStatus.name = (user?.name)
                            userStatus.profileImage = (user?.profileImage)
                            userStatus.lastUpdated = (date.time)
                            val obj = HashMap<String, Any>()
                            obj["name"] = userStatus.name!!
                            obj["profileImage"] = userStatus.profileImage!!
                            obj["lastUpdated"] = userStatus.lastUpdated
                            val imageUrl = uri.toString()
                            val status = Status(imageUrl, userStatus.lastUpdated)
                            database!!.reference
                                .child("stories")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj)
                            database!!.reference.child("stories")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .child("statuses")
                                .push()
                                .setValue(status)
                            dialog!!.dismiss()
                        }
                    }
                }
            }
        }
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

        fun onSignOut(view: View) {

        }
    }

    fun onOptionsItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.search -> Toast.makeText(mContext, "Search clicked.", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(mContext, "Settings Clicked.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun onCreateOptionsMenu(menu: Menu?) {
        (mContext as Activity).menuInflater.inflate(R.menu.topmenu, menu)
    }
}



