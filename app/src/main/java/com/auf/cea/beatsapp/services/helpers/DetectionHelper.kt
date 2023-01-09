package com.auf.cea.beatsapp.services.helpers

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import com.auf.cea.beatsapp.databinding.FragmentHomeBinding
import com.auf.cea.beatsapp.models.shazammodels.Track
import com.auf.cea.beatsapp.ui.fragments.HomeFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class DetectionHelper (val parent: HomeFragment, val binding: FragmentHomeBinding) {
    private lateinit var recorder: AudioRecord
    private lateinit var recordingThread: Thread
    private val recordingInProgress = AtomicBoolean(false)

    fun checkPermission() {
        if ((ActivityCompat.checkSelfPermission(parent.requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(parent.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(
                parent.requireActivity(),
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                111
            )
        } else {
            binding.fabStart.isEnabled = true
        }
    }

    private inner class RecordingRunnable : Runnable {
        override fun run() {
            val file = parent.requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/FinalRecPCM" + ".pcm"
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
//                        Log.d("BUFFER ARRAY:", buffer.array().toString())
                        buffer.clear()
                    }
                }
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

     fun startRecording(){
        checkPermission()
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE
        )
        recorder.startRecording()

        recordingInProgress.set(true)
        recordingThread = Thread(RecordingRunnable(), "Recording Thread")
        recordingThread.start()
    }

     fun stopRecording(){
        recordingInProgress.set(false)
        recorder.stop()
        recorder.release()
    }

     fun getBase64String(): String {
        val file = parent.requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/FinalRecPCM" + ".pcm"
        val byteArray: ByteArray = File(file).readBytes()
        return Base64.getEncoder().encodeToString(byteArray)
    }

    companion object {
        private const val SAMPLING_RATE_IN_HZ = 44_100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE_FACTOR = 2
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLING_RATE_IN_HZ, CHANNEL_CONFIG,
            AUDIO_FORMAT) * BUFFER_SIZE_FACTOR
    }
}