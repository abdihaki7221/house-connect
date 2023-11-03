package com.example.houseconnect.employeeModule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.houseconnect.R
import com.example.houseconnect.authModule.SignIn
import com.example.houseconnect.databinding.ActivityEmployeeDashboardBinding
import com.example.houseconnect.employeeModule.response.DashboardResponse
import com.example.houseconnect.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeDashboard : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEmployeeDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarEmployeeDashboard.toolbar)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })


        val activateButton: Button = findViewById(R.id.activate_card_button)
        activateButton.setOnClickListener {
            startActivity(Intent(this@EmployeeDashboard, EditProfile::class.java))
            finish()
        }
//
//        binding.appBarEmployeeDashboard.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_employee_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    private fun showErrorUI(errorMessage: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        snackbar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.employee_dashboard, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                // Handle the "action_settings" menu item click here
                // Implement your logout logic here, such as clearing session data, etc.
                // You can launch a login activity or perform any necessary actions.
                // For example, you can use Intent to start a LoginActivity.
                // Replace LoginActivity::class.java with your actual login activity.
                val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.remove("token")
                editor.apply()
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
                finish() // Close the current activity to prevent going back to it.
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_employee_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        // Place your API request logic here

        val empStatus: TextView = findViewById(R.id.first_card_title)
        val chargeTxt: Button = findViewById(R.id.first_charge_btn)
        val activateButton: Button = findViewById(R.id.activate_card_button)
        val fullName: TextView = findViewById(R.id.nav_header_title)
        val jobRequestCard: CardView = findViewById(R.id.job_request_card)
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val phone = sharedPreferences.getString("phone", null)

        if (token != null && phone != null) {
            val apiService = RetrofitClient.create(token)
            val call = apiService.getDashboardStats(phone)

            call.enqueue(object : Callback<DashboardResponse> {
                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (response.isSuccessful) {
                        val dashboardResponse = response.body()
                        // Handle the dashboard data here
                        if (dashboardResponse?.responseCode == "00") {
                            val status = dashboardResponse.response
                            val charge = dashboardResponse.charge
                            val fullNameTxt = dashboardResponse.fullName
                            empStatus.text = status
                            val text = "ksh $charge"
                            chargeTxt.text = text
                            fullName.text = fullNameTxt

                            if (status == "pending") {
                                activateButton.visibility = View.GONE
                                chargeTxt.text = text
                                jobRequestCard.visibility = View.GONE
                            } else if (status == "inActive") {
                                activateButton.visibility = View.VISIBLE
                                chargeTxt.text = text
                                jobRequestCard.visibility = View.GONE
                            } else {
                                activateButton.visibility = View.GONE
                                chargeTxt.text = text

                            }
                        } else {
                            showErrorUI("Fetching stats failed. Please try again.")
                        }
                    } else {
                        showErrorUI("Request Failed")
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    t.printStackTrace()
                    showErrorUI("Request Failed: ${t.message}")
                }
            })
        }
    }

}