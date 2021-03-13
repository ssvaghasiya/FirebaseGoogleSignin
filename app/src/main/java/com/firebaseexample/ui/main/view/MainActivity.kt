package com.firebaseexample.ui.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebaseexample.R
import com.firebaseexample.base.view.BaseActivity
import com.firebaseexample.databinding.ActivityHomeBinding
import com.firebaseexample.databinding.ActivityMainBinding
import com.firebaseexample.ui.home.viewmodel.HomeViewModel
import com.firebaseexample.ui.main.viewmodel.MainViewModel

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(activity).get(MainViewModel::class.java)
        viewModel.setBinder(binding)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        viewModel.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }
}