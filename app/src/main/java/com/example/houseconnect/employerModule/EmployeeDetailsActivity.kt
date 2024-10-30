package com.example.houseconnect.employerModule

import Employee
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.houseconnect.R

class EmployeeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_details)

        // Retrieve the employee object from the intent
        val employee = intent.getParcelableExtra<Employee>("employee")

        // Display employee details in TextViews
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val workTextView: TextView = findViewById(R.id.workTextView)
        val priceTextView: TextView = findViewById(R.id.priceTextView)
        val locationTextView: TextView = findViewById(R.id.locationTextView)
        val phoneNumberTextView: TextView = findViewById(R.id.phoneNumberTextView)

        nameTextView.text = "Name: ${employee?.name}"
        workTextView.text = "Work: ${employee?.work}"
        priceTextView.text = "Price: ${employee?.price}"
        locationTextView.text = "Location: ${employee?.location}"
        phoneNumberTextView.text = "Phone Number: ${employee?.phoneNumber}"
    }
}

