package com.auf.cea.beatsapp.ui.lyrics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.auf.cea.beatsapp.databinding.ActivityLyricsBinding

class LyricsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLyricsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLyricsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}