package com.example.houseconnect.authModule

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.houseconnect.R
import com.example.houseconnect.retrofit.RetrofitClient
import com.example.houseconnect.retrofit.SignInResponse
import com.example.houseconnect.retrofit.request.SignInRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigin_in)

        /**get the data from the layout view**/
        val signIn: Button = findViewById(R.id.sign_in_btn)
        val  phoneTxt:TextInputEditText = findViewById(R.id.phone_signIn)


        signIn.setOnClickListener {
            val phoneNumber: String = phoneTxt.text.toString()




            val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val cacheToken = sharedPreferences.getString("token", null)

            val apiService = RetrofitClient.api()
            val request: SignInRequest = SignInRequest(phoneNumber)
            val call = apiService.loginRequest(request)
            call.enqueue(object : Callback<SignInResponse> {
                override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                    if (response.isSuccessful) {
                        val signInResponse = response.body()
                        println("Response: $signInResponse")

                        if (signInResponse?.status == true) {
                            /** get the token, phone, otp
                             * to be stored in the cache
                             * **/

                            val token = signInResponse.token
                            val phone = signInResponse.phone
                            val otp = signInResponse.otp
                            val isVerified = signInResponse.isVerified
                            val userType = signInResponse.userType
                            val editor = sharedPreferences.edit()
                            editor.putString("token", token)
                            editor.putString("userType",userType)
                            editor.putString("phone", phone)
                            editor.putString("otp", otp.toString())
                            editor.putString("isVerified", isVerified.toString())
                            editor.apply()

                            val intent = Intent(this@SignIn, Otp::class.java)
                            intent.putExtra("enteredPhone", phoneNumber)
                            startActivity(intent)
                        } else {
                            val errorMessage = signInResponse?.message ?: "Login failed. Please try again."
                            showErrorUI(errorMessage)
                        }
                    } else {
                        showErrorUI("Request Failed")
                    }
                }

                override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                    t.printStackTrace()
                    showErrorUI(String.format("Request Failed:-> %s " ,t))
                }
            })

        }

    }

    private fun showErrorUI(errorMessage: String) {

        val phoneNumberEditLayout: TextInputLayout = findViewById(R.id.textInputLayout)
        phoneNumberEditLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
        phoneNumberEditLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))

        val errorText: TextView = findViewById(R.id.SignIn_errorTextView)
        errorText.text = errorMessage
        errorText.visibility = View.VISIBLE

    }

}