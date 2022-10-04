package com.hex.kotlin_cameraar.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.hex.kotlin_cameraar.R
import com.hex.kotlin_cameraar.databinding.FragmentMainBinding
import com.hex.kotlin_cameraar.viewmodel.MainViewModel

class MainFragment : Fragment() {


    private lateinit var binding: FragmentMainBinding

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGo.setOnClickListener {
            turnOnCameraAR()
        }
    }

    private fun turnOnCameraAR(){
        NavHostFragment.findNavController(this).navigate(R.id.cameraFragment)
    }



}