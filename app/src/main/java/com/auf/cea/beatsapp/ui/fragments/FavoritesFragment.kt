package com.auf.cea.beatsapp.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.PREFERENCE_NAME
import com.auf.cea.beatsapp.constants.USER_ID
import com.auf.cea.beatsapp.databinding.FragmentFavoritesBinding
import com.auf.cea.beatsapp.models.realm.Track
import com.auf.cea.beatsapp.services.helpers.DatabaseOperationsManager
import com.auf.cea.beatsapp.ui.adapters.RealmTrackAdapter


class FavoritesFragment : Fragment(),
    RealmTrackAdapter.RealmTrackAdapterInterface,
    DatabaseOperationsManager{

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var trackList: ArrayList<Track>
    private lateinit var adapter: RealmTrackAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userID:String
    private lateinit var favoriteFragmentInterface: FavoritesFragmentInterface

    interface FavoritesFragmentInterface{
        fun transferTrackData(trackData: Track, responseType:String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        favoriteFragmentInterface = context as FavoritesFragmentInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //sharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
        userID = sharedPreferences.getString(USER_ID,"").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Tracklist
        trackList = arrayListOf()

        // Setup Adapter
        adapter = RealmTrackAdapter(trackList, this)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        with(binding){
            rvTrackList.adapter = adapter
            rvTrackList.layoutManager = layoutManager
        }

        retrieveTracks(userID,adapter)
    }

    override fun removeTrack(id: String) {
        removeTracks(id, userID, adapter)
        Toast.makeText(requireContext(),"Track removed from list.",Toast.LENGTH_SHORT).show()
    }

    override fun viewLyrics(trackData: Track) {
        favoriteFragmentInterface.transferTrackData(trackData, "realm")
    }
}