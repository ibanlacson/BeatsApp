package com.auf.cea.beatsapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.MXM_MODEL_DATA
import com.auf.cea.beatsapp.constants.RESPONSE_TYPE
import com.auf.cea.beatsapp.constants.SHAZAM_MODEL_DATA
import com.auf.cea.beatsapp.constants.TRACK_ID
import com.auf.cea.beatsapp.databinding.ActivityMainBinding
import com.auf.cea.beatsapp.models.musixmatch.tracksearch.Track
import com.auf.cea.beatsapp.models.shazammodels.MusicDetectionModel
import com.auf.cea.beatsapp.ui.fragments.*
import com.auf.cea.beatsapp.ui.lyrics.LyricsActivity
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity(),
    ChipNavigationBar.OnItemSelectedListener,
    HomeFragment.HomeFragmentInterface,
    SearchFragment.SearchFragmentInterface{
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup Navigation Bar
        binding.chipNavigationBar.setItemSelected(R.id.home,true)
        binding.chipNavigationBar.setOnItemSelectedListener(this)
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, HomeFragment())
            .commit()
    }

    override fun onResume() {
        super.onResume()
        //Setup Navigation Bar on Resume
        var id = binding.chipNavigationBar.getSelectedItemId()

        binding.chipNavigationBar.setOnItemSelectedListener(this)
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, getFragment(id))
            .commit()
    }

    private fun getFragment(id: Int):Fragment {
        lateinit var switchToFragment: Fragment

        when(id){
            (R.id.home) -> {
                switchToFragment = HomeFragment()
            }
            (R.id.history) -> {
                switchToFragment = HistoryFragment()
            }
            (R.id.search) -> {
                switchToFragment = SearchFragment()
            }
            (R.id.favorites) -> {
                switchToFragment = FavoritesFragment()
            }
            (R.id.profile) -> {
                switchToFragment = ProfileFragment()
            }
        }
        return switchToFragment
    }

    override fun onItemSelected(id: Int) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, getFragment(id))
            .commit()
    }


    override fun passLyricsData(lyricsData: MusicDetectionModel, responseType: String) {
        val intent = Intent(this, LyricsActivity::class.java)
        intent.putExtra(SHAZAM_MODEL_DATA,lyricsData)
        intent.putExtra(RESPONSE_TYPE,responseType)
        startActivity(intent)
    }

    override fun passTrackID(trackID: String, trackData: Track) {
        val intent = Intent(this, LyricsActivity::class.java)
        intent.putExtra(TRACK_ID, trackID)
        intent.putExtra(MXM_MODEL_DATA, trackData)
        intent.putExtra(RESPONSE_TYPE, "musixmatch")
        startActivity(intent)
    }
}
