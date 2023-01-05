package com.auf.cea.beatsapp.ui.fragments

import android.os.Bundle
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
import com.auf.cea.beatsapp.models.MusixMatchModels.TrackSearchModel.Track
import com.auf.cea.beatsapp.services.helpers.Retrofit
import com.auf.cea.beatsapp.services.repositories.MusixmatchAPI
import com.auf.cea.beatsapp.ui.adapters.TrackListAdapter
import com.google.android.gms.auth.api.signin.internal.Storage
import kotlinx.coroutines.*

class SearchFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var trackData: ArrayList<Track>
    private lateinit var adapter: TrackListAdapter
    private var isLoading: Boolean = false
    private var pageCounter = 1
    private var query = ""

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
        adapter = TrackListAdapter(trackData,requireContext())
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        with(binding){
            rvTrackList.adapter = adapter
            rvTrackList.layoutManager = layoutManager
        }

        // Configuring Listeners
        binding.btnSearch.setOnClickListener(this)

        // On Scroll Listeners
        // TODO("add loading animation visible gone here")
        binding.rvTrackList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((!recyclerView.canScrollVertically(1)) && (newState == RecyclerView.SCROLL_STATE_IDLE)){
                    if (!isLoading) {
                        isLoading = true
                        whileLoading()
                        // TODO("change loading animation visibility to View.Visible here")
                    }
                }
            }
        })

    }

    private fun whileLoading(){
        // Increment Page Counter
        pageCounter++
        // call track data
        getTrackData(query)
    }

    private fun getTrackData(query:String) {
        val mxmAPI = Retrofit.getInstance(MXM_BASE_URL).create(MusixmatchAPI::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val result = mxmAPI.searchTrack(MXM_API_KEY,query,pageCounter,10)
            val trackDataResult = result.body()
            if (trackDataResult != null){
                trackData.addAll(trackDataResult.message.body.track_list)
                Log.d("WHATSAPPPP", query)
                withContext(Dispatchers.Main){
                    adapter.updateData(trackData)
                }
            } else {
                Log.d("NULLLLL ITO:", query)
            }
        }

    }

    override fun onClick(p0: View?) {
        when(p0!!.id) {
            (R.id.btn_search) -> {
                // Reset Page Counter
                pageCounter = 1

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
}