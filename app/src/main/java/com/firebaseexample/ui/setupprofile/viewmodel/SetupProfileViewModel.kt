package com.firebaseexample.ui.setupprofile.viewmodel

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebaseexample.R
import com.firebaseexample.apputils.Utils
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivitySetupProfileBinding
import com.firebaseexample.interfaces.TopBarClickListener
import com.firebaseexample.model.User
import com.firebaseexample.ui.main.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SetupProfileViewModel(application: Application) : BaseViewModel(application){

    private lateinit var binder: ActivitySetupProfileBinding
    private lateinit var mContext: Context
    var fauth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null

    fun setBinder(binder: ActivitySetupProfileBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {

        dialog = ProgressDialog(mContext)
        dialog!!.setMessage("Updating profile...")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        fauth = FirebaseAuth.getInstance()

        (mContext as AppCompatActivity).getSupportActionBar()?.hide()


    }


    inner class SlideMenuClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getContext(), view!!)
            if (value.equals(getLabelText(R.string.menu))) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }         }

        override fun onBackClicked(view: View?) {
            (mContext as Activity).finish()
        }
    }


    inner class ViewClickHandler {

        fun onProfileImage(view: View) {
            try {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                (mContext as Activity).startActivityForResult(intent, 45)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun onContinue(view: View) {
            try {
                val name: String = binder.nameBox.getText().toString()

                if (name.isEmpty()) {
                    binder.nameBox.setError("Please type a name")
                    return
                }

                dialog!!.show()
                if (selectedImage != null) {
                    val reference = storage!!.reference.child("Profiles").child(fauth!!.uid!!) 
                    reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                val uid = fauth!!.uid
                                val phone = fauth!!.currentUser.phoneNumber
                                val name: String = binder.nameBox.getText().toString()
                                val user = User(uid, name, phone, imageUrl)
                                database!!.reference
                                    .child("users")
                                    .child(uid!!)
                                    .setValue(user)
                                    .addOnSuccessListener {
                                        dialog!!.dismiss()
                                        val intent = Intent(
                                            mContext,
                                            MainActivity::class.java
                                        )
                                        mContext.startActivity(intent)
                                        (mContext as Activity).finish()
                                    }
                            }
                        }
                    }
                } else {
                    val uid = fauth!!.uid
                    val phone = fauth!!.currentUser.phoneNumber
                    val user = User(uid, name, phone, "No Image")
                    database!!.reference
                        .child("users")
                        .child(uid!!)
                        .setValue(user)
                        .addOnSuccessListener {
                            dialog!!.dismiss()
                            val intent = Intent(mContext, MainActivity::class.java)
                            mContext.startActivity(intent)
                            (mContext as Activity).finish()
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}



