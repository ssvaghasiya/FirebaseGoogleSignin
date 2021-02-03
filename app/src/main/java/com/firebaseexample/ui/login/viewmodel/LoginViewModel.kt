package com.firebaseexample.ui.login.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.facebook.*
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
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
    private lateinit var callbackManager: CallbackManager


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
        initFacebook()
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
        }else {
            try {
                callbackManager.onActivityResult(requestCode, resultCode, data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    private fun initFacebook() {
        callbackManager = CallbackManager.Factory.create()
    }

    private fun loginFacebook() {
        LoginManager.getInstance().logOut()
        LoginManager.getInstance().logInWithReadPermissions(
            mContext as Activity,
            listOf("email", "public_profile")
        )
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    getFacebookProfile(loginResult.accessToken)
                }

                override fun onCancel() {
                    Debug.e("Facebook", "Cancelled")
                }

                override fun onError(exception: FacebookException) {
                    Debug.e("Facebook", "onError : " + exception.message)
                }
            })
    }

    private fun getFacebookProfile(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { data, response ->
            try {
                Debug.e("getFacebookProfile", "" + data.toString())
//                val fbRes =
//                    Gson().fromJson<FbRes>(data.toString(), object : TypeToken<FbRes>() {}.type)

//                var providerToken = accessToken.token
                // Do Login here
                //          checkFacebookLogin(fbRes)
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        // Get new Instance ID token
                        val token = task.result
//                        googleRegisterDataModel.Fname = fbRes.firstName
//                        googleRegisterDataModel.email = fbRes.email
//                        googleRegisterDataModel.lname = fbRes.lastName
//                        googleRegisterDataModel.username = ""
//                        googleRegisterDataModel.phone = ""
//                        googleRegisterDataModel.login_provider = "facebook"
//                        googleRegisterDataModel.provider_token = fbRes.id
//                        googleRegisterDataModel.device_token = token

                    })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id,name,first_name,last_name,email,picture.type(large)"
        )
        request.parameters = parameters
        request.executeAsync()

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

        fun onFacebookLogin(view: View) {
            loginFacebook()
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
