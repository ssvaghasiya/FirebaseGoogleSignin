package com.firebaseexample.ui.phonenumber.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebaseexample.R
import com.firebaseexample.apputils.Utils
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivityPhoneNumberBinding
import com.firebaseexample.interfaces.TopBarClickListener
import com.firebaseexample.ui.main.view.MainActivity
import com.firebaseexample.ui.otp.view.OTPActivity
import com.google.firebase.auth.FirebaseAuth

class PhoneNumberViewModel(application: Application) : BaseViewModel(application){

    private lateinit var binder: ActivityPhoneNumberBinding
    private lateinit var mContext: Context
    var fauth: FirebaseAuth? = null


    fun setBinder(binder: ActivityPhoneNumberBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {
        binder.phoneBox.setText("+917990558210")
        fauth = FirebaseAuth.getInstance()
        if (fauth!!.currentUser != null) {
            val intent = Intent(mContext, MainActivity::class.java)
            mContext.startActivity(intent)
            (mContext as Activity).finish()
        }
        (mContext as AppCompatActivity).supportActionBar?.hide()
        binder.phoneBox.requestFocus()

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

        fun onContinue(view: View) {
            val intent = Intent(mContext, OTPActivity::class.java)
            intent.putExtra("phoneNumber", binder.phoneBox.getText().toString())
            mContext.startActivity(intent)
        }
    }


}



