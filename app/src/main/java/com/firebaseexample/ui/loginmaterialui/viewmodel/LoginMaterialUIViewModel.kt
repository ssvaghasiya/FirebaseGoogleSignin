package com.firebaseexample.ui.loginmaterialui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import com.firebaseexample.R
import com.firebaseexample.apputils.Utils
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivityHomeBinding
import com.firebaseexample.databinding.ActivityLoginMaterialUIBinding
import com.firebaseexample.interfaces.TopBarClickListener
import com.firebaseexample.ui.login.view.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginMaterialUIViewModel(application: Application) : BaseViewModel(application){

    private lateinit var binder: ActivityLoginMaterialUIBinding
    private lateinit var mContext: Context
    private var firebaseAuth: FirebaseAuth? = null
    private var googleApiClient: GoogleSignInClient? = null
    private val TAG = "HomeViewModel"

    fun setBinder(binder: ActivityLoginMaterialUIBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {

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

        fun onSignOut(view: View) {
            googleApiClient?.signOut()?.addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if(task.isSuccessful){
                        firebaseAuth?.signOut()
                        val intent = Intent(mContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        (mContext as Activity).startActivity(intent)
                        (mContext as Activity).finish()
                    }
                }
            })
        }


        fun onSearch(view: View) {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


}



