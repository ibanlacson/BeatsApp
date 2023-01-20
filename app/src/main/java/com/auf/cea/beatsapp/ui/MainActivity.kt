package com.auf.cea.beatsapp.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.*
import com.auf.cea.beatsapp.databinding.ActivityMainBinding
import com.auf.cea.beatsapp.models.musixmatch.tracksearch.Track
import com.auf.cea.beatsapp.models.shazammodels.MusicDetectionModel
import com.auf.cea.beatsapp.services.helpers.DatabaseOperationsManager
import com.auf.cea.beatsapp.services.realm.config.RealmConfig
import com.auf.cea.beatsapp.services.realm.operations.UserDbOperations
import com.auf.cea.beatsapp.ui.fragments.*
import com.auf.cea.beatsapp.ui.landing.LoginActivity
import com.auf.cea.beatsapp.ui.lyrics.LyricsActivity
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(),
    ChipNavigationBar.OnItemSelectedListener,
    HomeFragment.HomeFragmentInterface,
    SearchFragment.SearchFragmentInterface,
    FavoritesFragment.FavoritesFragmentInterface,
    ProfileFragment.ProfileFragmentInterface,
    DatabaseOperationsManager{

    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation Bar
        binding.chipNavigationBar.setItemSelected(R.id.home,true)
        binding.chipNavigationBar.setOnItemSelectedListener(this)
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, HomeFragment())
            .commit()

        // Shared Preference
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString(USER_ID,"")
        if (userUID != null) {
            // check if user have data
            checkUserData(userUID)
        }
    }

    private fun checkUserData(userID:String){
        val realmConfiguration = RealmConfig.getConfiguration()
        val userDbOperations = UserDbOperations(realmConfiguration)
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("CheckUserData"))

        scope.launch (Dispatchers.IO) {
            val result = userDbOperations.retrieveUserData(userID)
            if (result != null) {
                val editor = sharedPreferences.edit()
                editor.putString(USER_NAME,result.firstName)
            } else {
                val userDetailsDialog = UserDetailsDialog()
                userDetailsDialog.isCancelable = false
                userDetailsDialog.show(supportFragmentManager,null)
            }
        }
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

    override fun transferLyricModel(lyricsData: MusicDetectionModel, responseType: String) {
        val intent = Intent(this, LyricsActivity::class.java)
        intent.putExtra(SHAZAM_MODEL_DATA,lyricsData)
        intent.putExtra(RESPONSE_TYPE,responseType) // shazam
        startActivity(intent)
    }

    override fun transferTrackData(trackData: com.auf.cea.beatsapp.models.realm.Track, responseType: String) {
        val intent = Intent(this, LyricsActivity::class.java)
        intent.putExtra(TRACK_DATA, trackData)
        intent.putExtra(RESPONSE_TYPE,responseType) // realm
        startActivity(intent)
    }

    override fun transferTrackModel(trackID: String, trackData: Track) {
        val intent = Intent(this, LyricsActivity::class.java)
        intent.putExtra(TRACK_ID, trackID)
        intent.putExtra(MXM_MODEL_DATA, trackData)
        intent.putExtra(RESPONSE_TYPE, "musixmatch")
        startActivity(intent)
    }

    override fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun changeAccountInfo() {
        val changeUserDetailsDialog = DialogUserInformation()
        changeUserDetailsDialog.show(supportFragmentManager,null)
    }
}
