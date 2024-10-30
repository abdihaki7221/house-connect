package com.example.houseconnect.retrofit

data class SignInResponse(


    var otp: Int,
    var phone: String,
    var isVerified: Boolean,
    var token: String,
    var message: String,
    var status: Boolean,
    var userType: String

)
