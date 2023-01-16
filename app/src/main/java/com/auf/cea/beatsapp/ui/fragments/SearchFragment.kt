package com.auf.cea.beatsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.MXM_API_KEY
import com.auf.cea.beatsapp.constants.MXM_BASE_URL
import com.auf.cea.beatsapp.databinding.FragmentSearchBinding
import com.auf.cea.beatsapp.models.musixmatch.tracksearch.Track
import com.auf.cea.beatsapp.services.helpers.Retrofit
import com.auf.cea.beatsapp.services.repositories.MusixmatchAPI
import com.auf.cea.beatsapp.ui.adapters.TrackListAdapter
import kotlinx.coroutines.*

class SearchFragment : Fragment(),
    View.OnClickListener,
    TrackListAdapter.TrackListAdapterInterface{
    private lateinit var binding: FragmentSearchBinding
    private lateinit var trackData: ArrayList<Track>
    private lateinit var adapter: TrackListAdapter
    private lateinit var searchFragmentInterface: SearchFragmentInterface
    private var isLoading: Boolean = false
    private var pageCounter = 1
    private var query = ""

    interface SearchFragmentInterface{
        fun transferTrackModel(trackID: String, trackData: Track)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchFragmentInterface = context as SearchFragmentInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Variables
        trackData = arrayListOf()


        // Recycler View Configuration
        adapter = TrackListAdapter(trackData,requireContext(), this)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        with(binding){
            rvTrackList.adapter = adapter
            rvTrackList.layoutManager = layoutManager
        }

        // Configuring Listeners
        binding.btnSearch.setOnClickListener(this)

        // On Scroll Listeners
        binding.rvTrackList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((!recyclerView.canScrollVertically(1)) && (newState == RecyclerView.SCROLL_STATE_IDLE)){
                    if (!isLoading) {
                        isLoading = true
                        whileLoading()

                        // Start Animation
                        with(binding.anDragLoading){
                            visibility = View.VISIBLE
                            playAnimation()
                        }
                    }
                }
            }
        })

    }

    private fun whileLoading(){
        object : CountDownTimer(2000,1000){
            override fun onTick(p0: Long) {

            }
            override fun onFinish() {
                isLoading = false
                // Increment Page Counter
                pageCounter++
                // call track data
                getTrackData(query)
                // Hide loading animation
                with(binding.anDragLoading){
                    cancelAnimation()
                    visibility = View.GONE
                }
            }
        }.start()
    }

    private fun showLoading(){
        with(binding){
            rvTrackList.visibility = View.GONE
            anListLoading.visibility = View.VISIBLE
            anListLoading.playAnimation()
        }
        object : CountDownTimer(3000,1000){
            override fun onTick(p0: Long) {
            }
            override fun onFinish() {
                with(binding){
                    rvTrackList.visibility = View.VISIBLE
                    anListLoading.visibility = View.GONE
                    anListLoading.cancelAnimation()
                }
            }
        }.start()
    }

    private fun getTrackData(query:String) {
        val mxmAPI = Retrofit.getInstance(MXM_BASE_URL).create(MusixmatchAPI::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val result = mxmAPI.searchTrack(MXM_API_KEY,query,pageCounter,10)
            val trackDataResult = result.body()
            if (trackDataResult != null){
                trackData.addAll(trackDataResult.message.body.track_list)
                withContext(Dispatchers.Main){
                    adapter.updateData(trackData)
                }
            }
        }

    }

    override fun onClick(p0: View?) {
        when(p0!!.id) {
            (R.id.btn_search) -> {
                // Reset Page Counter
                pageCounter = 1

                // Call animation
                showLoading()

                // Query and API Calls
                if (query != binding.txtSearch.text.toString()){
                    // Reset the view
                    trackData = arrayListOf()

                    query = binding.txtSearch.text.toString()
                    Log.d("STRING", query)
                    getTrackData(query)
                } else {
                    query = binding.txtSearch.text.toString()
                    Log.d("STRING", query)
                    getTrackData(query)
                }
            }
        }
    }

    override fun getLyrics(trackID: String, trackData: Track) {
        searchFragmentInterface.transferTrackModel(trackID, trackData)
    }
}