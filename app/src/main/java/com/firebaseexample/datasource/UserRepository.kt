package com.firebaseexample.datasource

import androidx.lifecycle.MutableLiveData
import com.firebaseexample.apputils.Constant
import com.firebaseexample.apputils.Debug
import com.firebaseexample.base.datamodel.AllArea
import com.firebaseexample.network.APIinterface
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(apiInterface: APIinterface) {

    private var apiInterface: APIinterface? = apiInterface
    fun getArea(id: String): MutableLiveData<AllArea> {
        val data = MutableLiveData<AllArea>()
        val call = apiInterface!!.getArea(id)
        Debug.e("API", call.request().url.encodedPath)
        Debug.e("body", Gson().toJson(id))
        call.enqueue(object : Callback<AllArea> {
            override fun onFailure(call: Call<AllArea>, t: Throwable) {
                data.value = AllArea(Constant.getFailureCode())
                Debug.e("getArea isFailed", t.message)
            }

            override fun onResponse(call: Call<AllArea>, response: Response<AllArea>) {
                val loginStatus: Int = response.code()
                try {
                    if (response.isSuccessful) {
                        val loginData = response.body()
                        loginData!!.statusCode = loginStatus
                        data.value = loginData
                        Debug.e("getArea isSuccessful", Gson().toJson(loginData))
                    } else {
                        try {
                            val inputAsString =
                                response.errorBody()!!.source().inputStream().bufferedReader()
                                    .use { it.readText() }
                            Debug.e("Input", inputAsString)
                            val sb = StringBuilder()
                            sb.append(inputAsString)
                            val loginData = Gson().fromJson<AllArea>(
                                JSONObject(inputAsString).toString(),
                                object : TypeToken<AllArea>() {}.type
                            )
                            loginData.statusCode = loginStatus
                            data.value = loginData
                        } catch (e: Exception) {
                            e.printStackTrace()
                            data.value = AllArea(loginStatus)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    data.value = AllArea(loginStatus)
                }
            }
        })
        return data
    }


}