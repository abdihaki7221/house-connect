package com.example.houseconnect.employerModule.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.houseconnect.databinding.HomeEmployerBinding

interface SearchListener {
    fun onSearchPerformed(selectedLocation: String)
}

class EmployerHome : Fragment() {

    private var _binding: HomeEmployerBinding? = null
    private var isAdditionalOptionsVisible = false
    private lateinit var spinner: Spinner
    private var searchListener: SearchListener? = null

    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchListener = context as? SearchListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeEmployerBinding.inflate(inflater, container, false)
        val view = binding.root

        val expandIcon = binding.expandIc
        val additionalOptionsLayout = binding.additionalOptionsLayout
        val spinner = binding.locationSpinner
        val data = arrayOf("South C", "Eastleigh", "South B", "Pangani", "Westland")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        expandIcon.setOnClickListener {
            if (isAdditionalOptionsVisible) {
                additionalOptionsLayout.visibility = View.GONE
            } else {
                additionalOptionsLayout.visibility = View.VISIBLE
            }
            isAdditionalOptionsVisible = !isAdditionalOptionsVisible
        }

        binding.searchButton.setOnClickListener {
            val selectedLocation = spinner.selectedItem.toString()
            searchListener?.onSearchPerformed(selectedLocation)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
