package com.auf.cea.beatsapp.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.SHAZAM_BASE_URL
import com.auf.cea.beatsapp.databinding.FragmentHomeBinding
import com.auf.cea.beatsapp.models.shazammodels.Track
import com.auf.cea.beatsapp.services.helpers.DetectionHelper
import com.auf.cea.beatsapp.services.helpers.Retrofit
import com.auf.cea.beatsapp.services.repositories.ShazamAPI
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody


class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var detectedMusicData: Track
    private lateinit var helper: DetectionHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Variable Init
        helper = DetectionHelper(this,binding)

        // Configuring Listeners and Initial States
        with(binding){
            fabStart.setOnClickListener(this@HomeFragment)
        }
    }


    override fun onClick(p0: View?) {
        when(p0!!.id){
            (R.id.fab_start) -> {

                // Configure View States
                binding.llTrackView.visibility = View.GONE
                with(binding.anDetecting){
                    visibility = View.VISIBLE
                    playAnimation()
                }

                // Start Thread
                helper.startRecording()

                object : CountDownTimer(3000, 1000){
                    override fun onTick(p0: Long) {
                    }
                    override fun onFinish() {
                        helper.stopRecording()
                        detectMusic(helper.getBase64String())

                        // Hide and Cancel Animation
                        with(binding.anDetecting){
                            visibility = View.GONE
                            cancelAnimation()
                        }

                        // Show Track View
                        binding.llTrackView.visibility = View.VISIBLE
                    }
                }.start()
            }
        }
    }

    private fun detectMusic(encodedString: String){
        val requestBody = RequestBody.create(MediaType.parse("text/plain"),encodedString)

        val shazamAPI = Retrofit.getInstance(SHAZAM_BASE_URL).create(ShazamAPI::class.java)
        GlobalScope.launch (Dispatchers.IO){
            val result = shazamAPI.detectMusic(requestBody)
            Log.d("RESPONSE RESULT", result.toString())
            val musicDetectionData = result.body()
            if (musicDetectionData != null){
                detectedMusicData = musicDetectionData.track
                Log.d("Success:", detectedMusicData.title)
                withContext(Dispatchers.IO) {
                    with(binding){
                        txtTrackTitle.text = detectedMusicData.title
                        txtTrackArtist.text = detectedMusicData.subtitle
                    }
                }
            } else {
                Log.d("ERROR", "NULL OUTPUT")
            }
        }
    }
}