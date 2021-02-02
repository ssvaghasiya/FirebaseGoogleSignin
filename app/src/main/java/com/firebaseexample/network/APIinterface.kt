package com.firebaseexample.network

import com.firebaseexample.base.datamodel.AllArea
import retrofit2.Call
import retrofit2.http.*


interface APIinterface {


    @GET("listing/area/{id}")
    fun getArea(
        @Path(
            value = "id",
            encoded = true
        ) id: String
    ): Call<AllArea>


    @GET("attendance/{employeeId}")
    fun getAttendance(@Path("employeeId") employeeId: Double)

    @GET("attendance/{employeeId}/{date}")
    fun getAttendanceForDate(@Path("employeeId") employeeId: Double, @Path("date") date: Double)


}