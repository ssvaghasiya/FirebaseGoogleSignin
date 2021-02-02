package com.firebaseexample.ui.login.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.firebaseexample.R
import com.firebaseexample.apputils.Debug
import com.firebaseexample.base.viewmodel.BaseViewModel
import com.firebaseexample.databinding.ActivityLoginBinding
import com.firebaseexample.ui.home.view.HomeActivity
import com.firebaseexample.validator.EmailValidator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider


class LoginViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityLoginBinding
    private lateinit var mContext: Context
    private var googleApiClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 1
    var name: String? = null
    var email: kotlin.String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private val TAG = "LoginViewModel"


    fun setBinder(binder: ActivityLoginBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()
    }

    private fun init() {
        if (Debug.DEBUG) {
            binder.edtUserEmail.setText("")
            binder.edtPassword.setText("")
        }

        initGoogle()
    }

    fun initGoogle() {
        firebaseAuth = FirebaseAuth.getInstance()
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = AuthStateListener { firebaseAuth ->
            // Get signedIn user
            val user = firebaseAuth.currentUser

            //if user is signed in, we call a helper method to save the user details to Firebase
            if (user != null) {
                // User is signed in
                // you could place other firebase code
                //logic to save the user details to Firebase
                Debug.e(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                // User is signed out
                Debug.e(TAG, "onAuthStateChanged:signed_out")
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken((mContext as Activity).getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build()
        googleApiClient = GoogleSignIn.getClient(mContext as Activity, gso)
    }


    fun doGoogleLogin() {
        val intent = googleApiClient!!.signInIntent
        (mContext as Activity).startActivityForResult(intent, RC_SIGN_IN)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val result: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: Task<GoogleSignInAccount>) {
        try {
            val account =
                    result.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
//            updateUI(account)
            firebaseAuthWithGoogle(account)
            Debug.e(TAG, account.idToken.toString())
//            getGoogleProfile(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Debug.e("", "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
            firebaseAuthWithGoogle(null)
        }
    }

    private fun getGoogleProfile(acct: GoogleSignInAccount?) {
//        val acct = GoogleSignIn.getLastSignedInAccount(mContext)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val providerToken = acct.idToken
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl

            Debug.e("getGoogleProfile", "$personEmail $personName")

            // Do login social call here
//            FirebaseMessaging.getInstance().token
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        return@OnCompleteListener
//                    }
//                    // Get new Instance ID token
//                    val token = task.result
//
//                })
        }
    }

    private fun firebaseAuthWithGoogle(acc: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acc?.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(mContext as Activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Debug.e(TAG, "signInWithCredential:success")
                        val user = firebaseAuth!!.currentUser
                        val intent = Intent(mContext, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        (mContext as Activity).startActivity(intent)
                        (mContext as Activity).finish()
                        Debug.e(TAG, user.toString())
                    } else {
                        // If sign in fails, display a message to the user.
                        Debug.e("signInWithCredential:failure", task.exception?.printStackTrace().toString())
                        // ...
                    }

                    // ...
                }
    }


    inner class ViewClickHandler {

        fun onSignInClick(view: View) {
            try {
                if (isValidate()) {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun onGoogleLogin(view: View) {
            doGoogleLogin()
        }


    }

    fun isValidate(): Boolean {
        val emailValidator = EmailValidator(binder.edtUserEmail.text.toString())
        if (!emailValidator.isValid()) {
            showToast(emailValidator.msg)
            return false
        } else if (binder.edtPassword.text.isNullOrEmpty()) {
            showToast(mContext.getString(R.string.password_field_is_require))
            return false
        }

        return true
    }


}
