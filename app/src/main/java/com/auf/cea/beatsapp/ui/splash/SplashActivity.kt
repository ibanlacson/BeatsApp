package com.auf.cea.beatsapp.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.auf.cea.beatsapp.constants.PREFERENCE_NAME
import com.auf.cea.beatsapp.databinding.ActivitySplashBinding
import com.auf.cea.beatsapp.ui.landing.LoginActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Shared Preference Configuration
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

        // Animation
        object : CountDownTimer(2000,1000){
            override fun onTick(p0: Long) {
            }
            override fun onFinish() {

                val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }.start()
    }
}