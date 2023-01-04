package com.auf.cea.beatsapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recorder: AudioRecord
    private lateinit var recordingThread: Thread
    private val recordingInProgress = AtomicBoolean(false)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()

        with(binding){
            btnStart.isEnabled = true
            btnStop.isEnabled = false

            btnStart.setOnClickListener(this@HomeFragment)
            btnStop.setOnClickListener(this@HomeFragment)
        }
    }

    private inner class RecordingRunnable : Runnable {
        override fun run() {
//            obsolete na ire
//            val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/RecPCM" + ".pcm"
            val file = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/FinalRecPCM" + ".pcm"
            Log.d("FILESTORAGE: ", file)
            val buffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
            try {
                FileOutputStream(file).use { outStream ->
                    while (recordingInProgress.get()) {
                        val result = recorder.read(buffer, BUFFER_SIZE)
                        if (result < 0) {
                            throw RuntimeException(
                                "Reading of audio buffer failed: " + getBufferReadFailureReason(result)
                            )
                        }
                        outStream.write(
                            buffer.array(),
                            0,
                            BUFFER_SIZE
                        )
                        Log.d("BUFFER ARRAY:", buffer.array().toString())
                        buffer.clear()
                    }
                }
//                Log.d("BUFFER ARRAY:", buffer.array().toString())
//                Log.d("Encoded:", getBase64String())
            } catch (e: IOException) {
                throw RuntimeException("Writing of recorded audio failed", e)
            }
        }

        private fun getBufferReadFailureReason(result: Int): Any? {
            return when (result) {
                AudioRecord.ERROR_INVALID_OPERATION -> "ERROR_INVALID_OPERATION"
                AudioRecord.ERROR_BAD_VALUE -> "ERROR_BAD_VALUE"
                AudioRecord.ERROR_DEAD_OBJECT -> "ERROR_DEAD_OBJECT"
                AudioRecord.ERROR -> "ERROR"
                else -> "Unknown ($result)"
            }
        }
    }

    private fun checkPermission() {
        if ((ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                111
            )
        } else {
            binding.btnStart.isEnabled = true
        }
    }

    private fun startRecording(){
        checkPermission()
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC, SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE)
        recorder.startRecording()

        recordingInProgress.set(true)
        recordingThread = Thread(RecordingRunnable(), "Recording Thread")
        recordingThread.start()

    }

    private fun stopRecording(){
        recordingInProgress.set(false)
        recorder.stop()
        recorder.release()
    }

    private fun getBase64String() {
//        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/RecPCM" + ".pcm"
        val file = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/FinalRecPCM" + ".pcm"
        val byteArray:ByteArray = File(file).readBytes()
        val encodedString = Base64.getEncoder().encodeToString(byteArray)
        OutputStreamWriter(
//            FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/RecPCM" + ".txt"),
            FileOutputStream(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/FinalRecPCM" + ".txt"),
            StandardCharsets.UTF_8
        ).use { out ->
            out.write(
                encodedString
            )
        }
        Log.d(HomeFragment::class.java.simpleName.toString(),encodedString)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            (R.id.btnStart) -> {
                with(binding){
                    btnStart.isEnabled = false
                    btnStop.isEnabled = true
                }
                startRecording()
                object : CountDownTimer(5000, 1000){
                    override fun onTick(p0: Long) {

                    }
                    override fun onFinish() {
                        with(binding){
                            btnStart.isEnabled = true
                            btnStop.isEnabled = false
                        }
                        stopRecording()
                        getBase64String()
                    }
                }.start()
            }
            (R.id.btnStop) -> {
                with(binding){
                    btnStart.isEnabled = true
                    btnStop.isEnabled = false
                }
                stopRecording()
                getBase64String()
            }
        }
    }

    companion object {
        private const val SAMPLING_RATE_IN_HZ = 44_100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE_FACTOR = 3

        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLING_RATE_IN_HZ, CHANNEL_CONFIG,
            AUDIO_FORMAT) * BUFFER_SIZE_FACTOR
    }
}