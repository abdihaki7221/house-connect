package com.example.houseconnect.employeeModule.requests

import okhttp3.MultipartBody

data class ActivateRequest(
    val idNumber: String,
    val frontImage: MultipartBody.Part,
    val jobType: String,
    val backImage: MultipartBody.Part,
    val location: String
)

