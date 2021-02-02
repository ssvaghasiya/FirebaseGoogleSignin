package com.firebaseexample.ui.home.viewmodel


import android.app.*
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import com.firebaseexample.R
import com.firebaseexample.apputils.Debug
import com.firebaseexample.apputils.Utils
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivityHomeBinding
import com.firebaseexample.interfaces.TopBarClickListener
import com.firebaseexample.ui.home.view.HomeActivity
import com.firebaseexample.ui.login.view.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class HomeViewModel(application: Application) : BaseViewModel(application){

    private lateinit var binder: ActivityHomeBinding
    private lateinit var mContext: Context
    private var firebaseAuth: FirebaseAuth? = null
    private var googleApiClient: GoogleSignInClient? = null
    private val TAG = "HomeViewModel"

    fun setBinder(binder: ActivityHomeBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        this.binder.topbar.topBarClickListener = SlideMenuClickListener()
        this.binder.topbar.isSettingShow = true
        this.binder.topbar.isCenterTextShow = true

        init()

    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        var firebaseUser: FirebaseUser = firebaseAuth!!.currentUser!!
        if(firebaseUser != null){
            binder.txtName.text = firebaseUser.displayName
        }

        googleApiClient = GoogleSignIn.getClient(mContext as Activity, GoogleSignInOptions.DEFAULT_SIGN_IN)
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
            googleApiClient?.signOut()?.addOnCompleteListener(object : OnCompleteListener<Void>{
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



