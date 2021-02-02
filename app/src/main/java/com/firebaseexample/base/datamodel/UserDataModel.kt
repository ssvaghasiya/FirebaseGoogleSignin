package com.firebaseexample.base.datamodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.firebaseexample.datasource.UserRepository
import com.firebaseexample.network.APIClient
import com.firebaseexample.network.APIinterface

class UserDataModel {
    fun getArea(context: Context): MutableLiveData<AllArea> {
        val apInterface: APIinterface =
            APIClient.newRequestRetrofit(context).create(APIinterface::class.java)
        val userRepository = UserRepository(apInterface)
        return userRepository.getArea("")
    }
}