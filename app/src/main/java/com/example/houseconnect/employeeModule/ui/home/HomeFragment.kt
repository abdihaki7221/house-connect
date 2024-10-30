package com.example.houseconnect.employeeModule.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.houseconnect.R
import com.example.houseconnect.authModule.Otp
import com.example.houseconnect.databinding.FragmentHomeBinding
import com.example.houseconnect.databinding.HomeEmployerBinding
import com.example.houseconnect.employeeModule.EditProfile
import com.example.houseconnect.employeeModule.response.DashboardResponse
import com.example.houseconnect.retrofit.RetrofitClient
import com.example.houseconnect.retrofit.SignInResponse
import com.example.houseconnect.retrofit.request.SignInRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var sharedPreferences: SharedPreferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val empStatus: TextView = root.findViewById(R.id.first_card_title)
        val chargeTxt: Button = root.findViewById(R.id.first_charge_btn)

        sharedPreferences = requireContext().getSharedPreferences("your_pref_name", Context.MODE_PRIVATE)

        val cacheToken = sharedPreferences.getString("token", null)
        val cachePhone = sharedPreferences.getString("phone", null)

        val apiService = RetrofitClient.create(cacheToken)

        if (cachePhone != null) {
            val call = apiService.getDashboardStats(cachePhone)

            call.enqueue(object : Callback<DashboardResponse> {
                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (response.isSuccessful) {
                        val dashboardResponse = response.body()
                        println("Response: $dashboardResponse")

                        if (dashboardResponse?.responseCode == "00") {
                            val status = dashboardResponse.response
                            val charge = dashboardResponse.charge
                            val fullName = dashboardResponse.fullName
                            empStatus.text = status
                            chargeTxt.text = charge

                            if (status == "pending" || status == "active") {
                                chargeTxt.visibility = View.GONE
                            } else {
                                chargeTxt.visibility = View.VISIBLE
                            }
                        } else {
                            val errorMessage =
                                dashboardResponse?.response ?: "Fetching stats failed. Please try again."
                            showErrorUI(errorMessage)
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


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showErrorUI(errorMessage: String) {
        val snackbar = Snackbar.make(
            binding.root, errorMessage, Snackbar.LENGTH_LONG
        )
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
        snackbar.show()
    }
}
