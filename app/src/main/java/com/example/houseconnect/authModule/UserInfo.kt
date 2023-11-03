package com.example.houseconnect.authModule

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.example.houseconnect.R
import com.example.houseconnect.retrofit.RetrofitClient
import com.example.houseconnect.retrofit.SignInResponse
import com.example.houseconnect.retrofit.request.SignInRequest
import com.example.houseconnect.retrofit.request.UserInfoRequest
import com.example.houseconnect.retrofit.response.UserInfoResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale.Category

class UserInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

        val continueBtn : Button = findViewById(R.id.continue_btn)
        val  firstnameTxt: TextInputEditText = findViewById(R.id.first_name_edittext)
        val  lastnameTxt:TextInputEditText = findViewById(R.id.last_name_edittext)

        continueBtn.setOnClickListener{
            val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val cacheToken = sharedPreferences.getString("token",null)
            println("token cached is $cacheToken")
            val firstname: String = firstnameTxt.text.toString()
            val lastname: String = lastnameTxt.text.toString()

           if (cacheToken!=null){


               val apiService = RetrofitClient.api()
               val request: UserInfoRequest = UserInfoRequest(firstname,lastname,cacheToken)
               val call = apiService.updateUserInfo(request)

               call.enqueue(object : Callback<UserInfoResponse> {
                   override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                       val userInfo = response.body()
                       if (response.isSuccessful) {

                           println("Response: $userInfo")

                           if (userInfo?.status == true) {
                               /** get the token, phone, otp
                                * to be stored in the cache
                                * **/

                               val message = userInfo?.message

                               val intent = Intent(this@UserInfo, UserType::class.java)
                               startActivity(intent)
                           } else {
                               val errorMessage = userInfo?.message ?: "Login failed. Please try again."
                               showErrorUI(errorMessage)
                           }
                       } else {
                           showErrorUI("Request Failed")
                       }
                   }

                   override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                       t.printStackTrace()
                       showErrorUI("Request Failed")
                   }
               })
           }



        }


    }
    private fun showErrorUI(errorMessage: String) {

        println("error message is $errorMessage")

        when (errorMessage) {
            "firstname is required" -> {
                val firstname: TextInputLayout = findViewById(R.id.firstNameInputLayout)
                firstname.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
                firstname.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))

                val firstNameErrorText: TextView = findViewById(R.id.firstnameIncorrect)
                firstNameErrorText.text = errorMessage
                firstNameErrorText.visibility = View.VISIBLE
            }

            "lastname is required" -> {
                val lastName: TextInputLayout = findViewById(R.id.lastNameinfoInputLayout)
                lastName.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
                lastName.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))

                val lastNameError: TextView = findViewById(R.id.lastnameIncorrect)
                lastNameError.text = errorMessage
                lastNameError.visibility = View.VISIBLE
            }
            else -> {
                val firstNameErrorText: TextView = findViewById(R.id.firstnameIncorrect)
                firstNameErrorText.text = errorMessage
                firstNameErrorText.visibility = View.VISIBLE
            }
        }




    }
}