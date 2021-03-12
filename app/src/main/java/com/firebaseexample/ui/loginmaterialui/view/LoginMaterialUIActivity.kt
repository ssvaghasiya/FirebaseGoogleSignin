package com.firebaseexample.ui.loginmaterialui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebaseexample.R
import com.firebaseexample.base.view.BaseActivity
import com.firebaseexample.databinding.ActivityHomeBinding
import com.firebaseexample.databinding.ActivityLoginMaterialUIBinding
import com.firebaseexample.ui.home.viewmodel.HomeViewModel
import com.firebaseexample.ui.loginmaterialui.viewmodel.LoginMaterialUIViewModel

class LoginMaterialUIActivity : BaseActivity() {

    lateinit var binding: ActivityLoginMaterialUIBinding
    lateinit var viewModel: LoginMaterialUIViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_material_u_i)
        viewModel = ViewModelProvider(activity).get(LoginMaterialUIViewModel::class.java)
        viewModel.setBinder(binding)
    }
}