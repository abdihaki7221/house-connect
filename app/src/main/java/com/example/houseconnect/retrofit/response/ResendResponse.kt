package com.example.houseconnect.retrofit.response

data class ResendResponse(
    val status: Boolean,
    val message: String,
    var otp: Int,
    var phoneNumber: String,
)
