package com.firebaseexample.ui.setupprofile.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebaseexample.R
import com.firebaseexample.base.view.BaseActivity
import com.firebaseexample.databinding.ActivityMainBinding
import com.firebaseexample.databinding.ActivitySetupProfileBinding
import com.firebaseexample.ui.main.viewmodel.MainViewModel
import com.firebaseexample.ui.setupprofile.viewmodel.SetupProfileViewModel

class SetupProfileActivity : BaseActivity() {

    lateinit var binding: ActivitySetupProfileBinding
    lateinit var viewModel: SetupProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup_profile)
        viewModel = ViewModelProvider(activity).get(SetupProfileViewModel::class.java)
        viewModel.setBinder(binding)
    }
}