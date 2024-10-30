
package com.example.houseconnect

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import com.example.houseconnect.authModule.SignIn
import com.example.houseconnect.authModule.UserInfo
import com.example.houseconnect.employeeModule.EmployeeDashboard
import com.example.houseconnect.employerModule.EmployerDashboard
import com.example.houseconnect.retrofit.RetrofitClient
import com.example.houseconnect.retrofit.request.ValidateToken
import com.example.houseconnect.retrofit.response.IsTokenValid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val cacheToken = sharedPreferences.getString("token", null)

        val delayMillis = 5000
        Handler(Looper.myLooper()!!).postDelayed({
            if (cacheToken!=null){
                val api = RetrofitClient.create(cacheToken)
                val request: ValidateToken = ValidateToken(cacheToken)
                val call = api.validateToken(request)
                call.enqueue(object: Callback<IsTokenValid> {
                    override fun onResponse(
                        call: Call<IsTokenValid>,
                        response: Response<IsTokenValid>
                    ) {
                        val responseData = response.body()
                        /**get THE RESPONSE DATA**/
                        if (response.isSuccessful){

                            if (responseData?.status == true){
                                if(responseData?.userType=="EMPLOYER") {


                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            EmployerDashboard::class.java
                                        )
                                    )
                                }else{
                                    startActivity(Intent(this@MainActivity, EmployeeDashboard::class.java))
                                }
                            }else{
                                startActivity(Intent(this@MainActivity, SignIn::class.java))


                            }
                        }else{
                            startActivity(Intent(this@MainActivity, SignIn::class.java))
                        }

                    }

                    override fun onFailure(call: Call<IsTokenValid>, t: Throwable) {
//
//                                        //request failed please try again later
                        startActivity(Intent(this@MainActivity, SignIn::class.java))

                    }

                })
            }else{
                startActivity(Intent(this@MainActivity, SignIn::class.java))
            }


        }, delayMillis.toLong())



        }



}
