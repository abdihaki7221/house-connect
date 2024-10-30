package com.example.houseconnect.authModule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.houseconnect.R
import com.example.houseconnect.employeeModule.EmployeeDashboard
import com.example.houseconnect.employerModule.EmployerDashboard
import com.example.houseconnect.retrofit.RetrofitClient
import com.example.houseconnect.retrofit.request.ResendOtp
import com.example.houseconnect.retrofit.request.ValidateToken
import com.example.houseconnect.retrofit.response.IsTokenValid
import com.example.houseconnect.retrofit.response.ResendResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Otp : AppCompatActivity() {


    //count down variables
    private lateinit var countdownTextView: TextView
    private lateinit var resendTxt: TextView
    private var countDownTimer: CountDownTimer? = null
    private var countdownValue = 30
    private var isResendClickable = false
    private val cacheKey = "otp"


    private lateinit var digitFields: Array<EditText>

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        val digit1: EditText = findViewById(R.id.digit1)
        val digit2: EditText = findViewById(R.id.digit2)
        val digit3: EditText = findViewById(R.id.digit3)
        val digit4: EditText = findViewById(R.id.digit4)


        /**digit fields to change color**/
        digitFields = arrayOf(
            findViewById(R.id.digit1),
            findViewById(R.id.digit2),
            findViewById(R.id.digit3),
            findViewById(R.id.digit4)
        )

        val backArrow: ImageView = findViewById(R.id.back_btn)
        backArrow.setOnClickListener{
            val intent: Intent = Intent(this,SignIn::class.java)
            startActivity(intent)
        }

        // Set up TextWatcher for each EditText to automatically move focus
        digit1.addTextChangedListener(createTextWatcher(digit2))
        digit2.addTextChangedListener(createTextWatcher(digit3))
        digit3.addTextChangedListener(createTextWatcher(digit4))

        // Set up listener for the last EditText (digit4)
        digit4.addTextChangedListener(createLastDigitTextWatcher())

        //countdown timer
        countdownTextView = findViewById(R.id.countdownTextView)
        resendTxt = findViewById(R.id.resendOtp)
        startCountdownTimer()

        resendTxt.setOnClickListener {
            handleResendClick()
        }

    }

    private fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer((countdownValue * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownValue--
                countdownTextView.text = countdownValue.toString()
            }

            override fun onFinish() {

                countdownTextView.visibility = View.INVISIBLE
                resendTxt.text="Resend code"
                resendTxt.setTextColor(ContextCompat.getColor(resendTxt.context, R.color.primary))
                isResendClickable = true
                val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.remove("otp")
                editor.remove("phone")
                editor.apply()
            }
        }.start()
    }

    private fun resetCountdown() {
        countdownValue = 30
        countdownTextView.text = countdownValue.toString()
        countdownTextView.visibility = View.VISIBLE
        resendTxt.text="Resend code In"
        resendTxt.setTextColor(ContextCompat.getColor(resendTxt.context, R.color.black))
        isResendClickable = false
        startCountdownTimer()
    }

    private fun resetOtpInCache(resentOtp: Int, resentPhone: String) {
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("phone", resentPhone)
        editor.putString("otp", resentOtp.toString())
        editor.apply()
        val cacheOTp =sharedPreferences.getString("otp", null)
        val cachePhone = sharedPreferences.getString("phone",null)

        println("resent cached phone $cachePhone")
        println("resent cache otp is $cacheOTp")

    }

    private fun handleResendClick() {
        if (isResendClickable) {
            val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            // TODO: Implement resend code logic (send SMS)
            //get the token from the cache
            val cacheOTp =sharedPreferences.getString("otp", null)
            val cachePhone = sharedPreferences.getString("phone",null)
            val cacheToken = sharedPreferences.getString("token",null)

            //pass it through request headers if valid token return, status true or false, with message and phone
            if (cacheToken!=null){
                println("token in cache is $cacheToken")
                val api = RetrofitClient.create(cacheToken)
                val request:ResendOtp = ResendOtp(cacheToken)
                val call = api.resendOtp(request)
                call.enqueue(object : Callback<ResendResponse>{

                    override fun onResponse(
                        call: Call<ResendResponse>,
                        response: Response<ResendResponse>
                    ){

                        if (response.isSuccessful){
                            val responseBody = response.body()
                            val status = responseBody?.status
                            if (status == true){
                                val resentOtp = responseBody.otp
                                val resentPhone = responseBody.phoneNumber
                                val message = responseBody.message

                                resetCountdown()
                                resetOtpInCache(resentOtp,resentPhone)
                            }else{
//                                val responseBody = response.body()
                                val message = responseBody?.message
                                if (message != null) {
                                    showErrorUI(message)
                                }
                            }
                        }else{
                            showErrorUI("unknown error occurred")
                        }
                    }

                    override fun onFailure(call: Call<ResendResponse>, t: Throwable) {
                        showErrorUI("failed to resend code")
                    }

                })
            }

            //send sms to phone gotten from token passed as header and generate new otp
            //after response, put the otp and phone in the cache


        }
    }






    private fun createTextWatcher(nextEditText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && s.length == 1) {
                    nextEditText.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun createLastDigitTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && s.length == 1) {
                    // Fourth digit entered, navigate to the next activity
                    if (areAllDigitsEntered()) {

                        val enteredOtp = getEnteredOtp()
                        val intentNumber = intent.getStringExtra("enteredPhone")
                        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)


                        /**get the data from the cache**/
                        val cacheOTp =sharedPreferences.getString("otp", null)
                        val phoneNumber = sharedPreferences.getString("phone", null)
                        val cacheToken = sharedPreferences.getString("token", null)
                        val isVerified = sharedPreferences.getString("isVerified", null)
                        val userType = sharedPreferences.getString("userType",null)
                        println("is verified is $isVerified")

                        if(enteredOtp == cacheOTp && intentNumber==phoneNumber) {
                            if (isVerified != null) {
                                if (isVerified == "true") {
                                    if (userType.equals("Employee")){
                                        startActivity(Intent(this@Otp, EmployeeDashboard::class.java))

                                    }else if (userType.equals("Employer")){
                                        startActivity(Intent(this@Otp, EmployerDashboard::class.java))
                                    }else{
                                        showErrorUI("invalid job category")
                                    }

                                }else{
                                    startActivity(Intent(this@Otp, UserInfo::class.java))
                                }
                        }else{
                                showErrorUI("please login again")
                            }


                        }else{
                            showErrorUI("Incorrect Otp")
                        }


//
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun showErrorUI(errorMessage: String) {
        val digit1: EditText = findViewById(R.id.digit1)
        val digit2: EditText = findViewById(R.id.digit2)
        val digit3: EditText = findViewById(R.id.digit3)
        val digit4: EditText = findViewById(R.id.digit4)
        val redBackgroundDrawable = ContextCompat.getDrawable(this, R.drawable.red_ouline_backgrounf)

        digit1.background = redBackgroundDrawable
        digit2.background = redBackgroundDrawable
        digit3.background = redBackgroundDrawable
        digit4.background = redBackgroundDrawable

        val errorTextView: TextView = findViewById(R.id.errorTextView)
        errorTextView.text = errorMessage
        errorTextView.visibility = View.VISIBLE

    }

    private fun areAllDigitsEntered(): Boolean {


     val digit1: EditText = findViewById(R.id.digit1)
         val digit2: EditText = findViewById(R.id.digit2)
         val digit3: EditText = findViewById(R.id.digit3)
         val digit4: EditText = findViewById(R.id.digit4)

        val otpDigit1 = digit1.text.toString()
        val otpDigit2 = digit2.text.toString()
        val otpDigit3 = digit3.text.toString()
        val otpDigit4 = digit4.text.toString()

        return otpDigit1.isNotEmpty() && otpDigit2.isNotEmpty() && otpDigit3.isNotEmpty() && otpDigit4.isNotEmpty()
    }

    private fun getEnteredOtp(): String {

        val digit1: EditText = findViewById(R.id.digit1)
        val digit2: EditText = findViewById(R.id.digit2)
        val digit3: EditText = findViewById(R.id.digit3)
        val digit4: EditText = findViewById(R.id.digit4)

        val otpDigit1 = digit1.text.toString()
        val otpDigit2 = digit2.text.toString()
        val otpDigit3 = digit3.text.toString()
        val otpDigit4 = digit4.text.toString()

        return "$otpDigit1$otpDigit2$otpDigit3$otpDigit4"
    }
}

