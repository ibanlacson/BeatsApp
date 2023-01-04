package com.auf.cea.beatsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.databinding.ActivityMainBinding
import com.auf.cea.beatsapp.ui.fragments.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity(), ChipNavigationBar.OnItemSelectedListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup Navigation Bar
        binding.chipNavigationBar.setItemSelected(R.id.home,true)
        binding.chipNavigationBar.setOnItemSelectedListener(this)
        var fragmentManger = supportFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, HomeFragment())
            .commit()
    }

    override fun onItemSelected(id: Int) {
        var switchToFragment:Fragment? = null
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
        if (switchToFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.mainContainer.id, switchToFragment)
                .commit()
        }
    }
}
