package com.firebaseexample.ui.otp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebaseexample.R
import com.firebaseexample.base.view.BaseActivity
import com.firebaseexample.databinding.ActivityMainBinding
import com.firebaseexample.databinding.ActivityOTPBinding
import com.firebaseexample.ui.main.viewmodel.MainViewModel
import com.firebaseexample.ui.otp.viewmodel.OTPViewModel

class OTPActivity : BaseActivity() {

    lateinit var binding: ActivityOTPBinding
    lateinit var viewModel: OTPViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        viewModel = ViewModelProvider(activity).get(OTPViewModel::class.java)
        viewModel.setBinder(binding)
    }
}