package com.auf.cea.beatsapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.PREFERENCE_NAME
import com.auf.cea.beatsapp.constants.USER_ID
import com.auf.cea.beatsapp.constants.USER_NAME
import com.auf.cea.beatsapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profileFragmentInterface: ProfileFragmentInterface
    private lateinit var userID:String
    private lateinit var userName:String

    interface ProfileFragmentInterface {
        fun logout()
        fun changeAccountInfo()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        profileFragmentInterface = context as ProfileFragmentInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //INITIALIZE SHARED PREFERENCES
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        userID = sharedPreferences.getString(USER_ID,"").toString()
        userName = sharedPreferences.getString(USER_NAME,"").toString()

        // Set Listeners
        with(binding){
            btnChangeAccountInfo.setOnClickListener(this@ProfileFragment)
            btnChangeAccountSecurity.setOnClickListener(this@ProfileFragment)
            btnLogout.setOnClickListener(this@ProfileFragment)
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            (R.id.btn_change_account_info) -> {
                profileFragmentInterface.changeAccountInfo()
            }
            (R.id.btn_change_account_security) -> {

            }
            (R.id.btn_logout) -> {
                val editor = sharedPreferences.edit()
                    .clear()
                    .apply()

                if (sharedPreferences.getString(USER_ID,null).isNullOrEmpty()) {
                    profileFragmentInterface.logout()
                }
            }
        }
    }
}