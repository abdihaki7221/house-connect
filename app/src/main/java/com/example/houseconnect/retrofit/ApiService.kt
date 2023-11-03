package com.example.houseconnect.retrofit

import com.example.houseconnect.employeeModule.requests.ActivateRequest
import com.example.houseconnect.employeeModule.requests.DashboardRequests
import com.example.houseconnect.employeeModule.response.ActivateResponse
import com.example.houseconnect.employeeModule.response.DashboardResponse
import com.example.houseconnect.retrofit.request.ResendOtp
import com.example.houseconnect.retrofit.request.SignInRequest
import com.example.houseconnect.retrofit.request.UserInfoRequest
import com.example.houseconnect.retrofit.request.UserTypeRequest
import com.example.houseconnect.retrofit.request.ValidateToken
import com.example.houseconnect.retrofit.response.IsTokenValid
import com.example.houseconnect.retrofit.response.ResendResponse
import com.example.houseconnect.retrofit.response.UserInfoResponse
import com.example.houseconnect.retrofit.response.UserTypeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {


    @POST("/api/auth/login")
    fun loginRequest(@Body request: SignInRequest): Call<SignInResponse>

    @POST("/api/auth/validate")
    fun validateToken(@Body request: ValidateToken): Call<IsTokenValid>

    @POST("/api/auth/resend")
    fun resendOtp(@Body request: ResendOtp): Call<ResendResponse>

    @POST("/api/auth/info")
    fun updateUserInfo(@Body request: UserInfoRequest): Call<UserInfoResponse>

    @POST("/api/auth/category")
    fun updateUserType(@Body request: UserTypeRequest): Call<UserTypeResponse>


    @PUT("/activate")
    @Multipart
    fun activateEmployee(
        @Part("idNumber") idNumber: RequestBody,
        @Part("jobType") jobType: RequestBody,
        @Part("location") location: RequestBody,
        @Part frontImage: MultipartBody.Part,
        @Part backImage: MultipartBody.Part
    ): Call<ActivateResponse>


    @GET("/dashboard/stats")
    fun getDashboardStats(@Query("phone") phone: String): Call<DashboardResponse>

}