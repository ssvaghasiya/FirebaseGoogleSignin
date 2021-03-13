package com.firebaseexample.ui.phonenumber.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebaseexample.R
import com.firebaseexample.base.view.BaseActivity
import com.firebaseexample.databinding.ActivityMainBinding
import com.firebaseexample.databinding.ActivityPhoneNumberBinding
import com.firebaseexample.ui.main.viewmodel.MainViewModel
import com.firebaseexample.ui.phonenumber.viewmodel.PhoneNumberViewModel

class PhoneNumberActivity : BaseActivity() {

    lateinit var binding: ActivityPhoneNumberBinding
    lateinit var viewModel: PhoneNumberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_number)
        viewModel = ViewModelProvider(activity).get(PhoneNumberViewModel::class.java)
        viewModel.setBinder(binding)
    }
}