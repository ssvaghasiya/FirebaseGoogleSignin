package com.firebaseexample.ui.otp.viewmodel

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebaseexample.R
import com.firebaseexample.apputils.Utils
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivityOTPBinding
import com.firebaseexample.interfaces.TopBarClickListener
import com.firebaseexample.ui.setupprofile.view.SetupProfileActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.mukesh.OnOtpCompletionListener
import java.util.concurrent.TimeUnit

class OTPViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityOTPBinding
    private lateinit var mContext: Context
    var fauth: FirebaseAuth? = null

    var verificationId: String? = null
    var dialog: ProgressDialog? = null


    fun setBinder(binder: ActivityOTPBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {
        dialog = ProgressDialog(mContext)
        dialog!!.setMessage("Sending OTP...")
        dialog!!.setCancelable(false)
        dialog!!.show()

        fauth = FirebaseAuth.getInstance()

        (mContext as AppCompatActivity).getSupportActionBar()?.hide()

        val phoneNumber: String = (mContext as Activity).getIntent().getStringExtra("phoneNumber")!!

        binder.phoneLbl.setText("Verify $phoneNumber")

        val options = PhoneAuthOptions.newBuilder(fauth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(mContext as Activity)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}
                override fun onVerificationFailed(e: FirebaseException) {}
                override fun onCodeSent(
                    verifyId: String,
                    forceResendingToken: ForceResendingToken
                ) {
                    super.onCodeSent(verifyId, forceResendingToken)
                    dialog!!.dismiss()
                    verificationId = verifyId
                    val imm =
                        (mContext).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    binder.otpView.requestFocus()
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        binder.otpView.setOtpCompletionListener(OnOtpCompletionListener { otp ->
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            fauth!!.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(mContext, SetupProfileActivity::class.java)
                    mContext.startActivity(intent)
                    (mContext as Activity).finishAffinity()
                } else {
                    Toast.makeText(mContext, "Failed.", Toast.LENGTH_SHORT).show()
                }
            }
        })


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


}



