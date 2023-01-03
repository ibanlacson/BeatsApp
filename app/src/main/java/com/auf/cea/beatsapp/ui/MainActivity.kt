package com.auf.cea.beatsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}