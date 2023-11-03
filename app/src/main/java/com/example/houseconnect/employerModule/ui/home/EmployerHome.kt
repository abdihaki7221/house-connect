package com.example.houseconnect.employerModule.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.houseconnect.R
import com.example.houseconnect.databinding.FragmentHomeBinding
import com.example.houseconnect.databinding.HomeEmployerBinding

class EmployerHome : Fragment() {

    private var _binding: HomeEmployerBinding? = null
    private var isAdditionalOptionsVisible = false

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

        _binding = HomeEmployerBinding.inflate(inflater, container, false)

        val expandIcon = binding.expandIc
        val additionalOptionsLayout = binding.additionalOptionsLayout
         val spinner = binding.locationSpinner
        val data = arrayOf("South C", "Eastleigh", "South B", "Pangani", "Westland")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        // Set a click listener on the expand icon
        expandIcon.setOnClickListener {
            if (isAdditionalOptionsVisible) {
                // Hide the additional options layout
                additionalOptionsLayout.visibility = View.GONE
                // Change the icon to expand
//                expandIcon.setImageResource(R.drawable.ic_expand)
            } else {
                // Show the additional options layout
                additionalOptionsLayout.visibility = View.VISIBLE
                // Change the icon to minimise
//                expandIcon.setImageResource(R.drawable.ic_minimise)
            }
            // Toggle the flag
            isAdditionalOptionsVisible = !isAdditionalOptionsVisible
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}