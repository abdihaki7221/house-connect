package com.example.houseconnect.authModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.example.houseconnect.R
import com.example.houseconnect.employeeModule.EmployeeDashboard
import com.example.houseconnect.employerModule.EmployerDashboard

import com.example.houseconnect.retrofit.RetrofitClient
import com.example.houseconnect.retrofit.request.UserTypeRequest
import com.example.houseconnect.retrofit.response.UserTypeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserType : AppCompatActivity() {

    private lateinit var request: UserTypeRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type)

        val employeeCategory: Button = findViewById(R.id.job_seeker)
        val employerCategory: Button = findViewById(R.id.recruiter_btn)
        val backArrow: ImageView = findViewById(R.id.back_btn)

        backArrow.setOnClickListener {
            val intent = Intent(this, UserInfo::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val cacheToken = sharedPreferences.getString("token", null)

        employerCategory.setOnClickListener {
            handleUserTypeSelection("Employer", cacheToken)
        }

        employeeCategory.setOnClickListener {
            handleUserTypeSelection("Employee", cacheToken)
        }
    }

    private fun handleUserTypeSelection(userType: String, cacheToken: String?) {
        if (cacheToken != null) {
            request = UserTypeRequest(userType, cacheToken)
            val apiService = RetrofitClient.api()
            val call = apiService.updateUserType(request)
            call.enqueue(object : Callback<UserTypeResponse> {
                override fun onResponse(call: Call<UserTypeResponse>, response: Response<UserTypeResponse>) {
                    handleResponse(response.body())
                }

                override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                    showErrorUI(t.message.toString())
                }
            })
        } else {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }

    private fun handleResponse(responseBody: UserTypeResponse?) {
        if (responseBody != null) {
            val status = responseBody.status
            val userType = responseBody.category;
            println("response status is $status ")
            println("user type is ${responseBody.category}")


            if (status) {
                if (userType=="Employer"){
                    val intent = Intent(this@UserType, EmployerDashboard::class.java)
                    startActivity(intent)
                }else{
                    val intent = Intent(this@UserType, EmployeeDashboard::class.java)
                    startActivity(intent)
                }

            } else {
                showErrorUI(responseBody.message ?: "Failed to choose. Please try again.")
            }
        } else {
            showErrorUI("Failed. Please try again.")
        }
    }

    private fun showErrorUI(errorMessage: String) {
        val errorText: TextView = findViewById(R.id.userType_errorTextView)
        errorText.text = errorMessage
        errorText.visibility = View.VISIBLE
    }
}
