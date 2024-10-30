package com.example.houseconnect.employerModule

import Employee
import EmployeeAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.houseconnect.R
import com.example.houseconnect.databinding.ActivityEmployerDashboardBinding
import com.example.houseconnect.employerModule.ui.home.SearchListener
import com.google.android.material.navigation.NavigationView

class EmployerDashboard : AppCompatActivity(), SearchListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEmployerDashboardBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeList: List<Employee>
    private lateinit var adapter: EmployeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmployerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarEmployerDashboard.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_employer_dashboard)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        employeeList = listOf(
            Employee("abdihakim isa", "Plumbing", 50.0, "Eastleigh", "1234567890"),
            Employee("Mohamed Burhan", "Electrician", 60.0, "South C", "9876543210"),
            Employee("abdihakim isa", "Plumbing", 50.0, "Eastleigh", "1234567890"),
            Employee("Mohamed Burhan", "Electrician", 60.0, "South B", "9876543210"),
            // Add more sample data as needed
        )

        recyclerView = findViewById(R.id.recyclerView)
        adapter = EmployeeAdapter(employeeList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : EmployeeAdapter.OnItemClickListener {
            override fun onCardClick(position: Int) {
                val employee = employeeList[position]

                // You can perform any action you want here based on the clicked employee
                // For example, you can open a new activity to display more details about the employee

                // Example: Opening a new activity to display employee details
                val intent = Intent(this@EmployerDashboard, EmployeeDetailsActivity::class.java)
                intent.putExtra("employee", employee)
                startActivity(intent)
            }


            override fun onPhoneButtonClick(position: Int) {
                val phoneNumber = employeeList[position].phoneNumber
                val phoneUri = Uri.Builder()
                    .scheme("tel")
                    .encodedOpaquePart(phoneNumber)
                    .build()
                val callIntent = Intent(Intent.ACTION_CALL, phoneUri)
                startActivity(callIntent)
            }

            override fun onSmsButtonClick(position: Int) {
                val phoneNumber = employeeList[position].phoneNumber
                val smsUri = Uri.Builder()
                    .scheme("sms to")
                    .encodedOpaquePart(phoneNumber)
                    .build()
                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
                smsIntent.putExtra("sms_body", "Hello, I'm interested in your services.")
                startActivity(smsIntent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.employer_dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_employer_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onSearchPerformed(selectedLocation: String) {
        val filteredList = employeeList.filter { it.location == selectedLocation }
        adapter.submitList(filteredList)

        if (filteredList.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.GONE
        }
    }
}
