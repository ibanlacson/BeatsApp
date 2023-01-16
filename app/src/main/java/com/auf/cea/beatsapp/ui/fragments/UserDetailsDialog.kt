package com.auf.cea.beatsapp.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.PREFERENCE_NAME
import com.auf.cea.beatsapp.constants.USER_ID
import com.auf.cea.beatsapp.databinding.FragmentUserDetailsDialogBinding
import com.auf.cea.beatsapp.services.realm.config.RealmConfig
import com.auf.cea.beatsapp.services.realm.operations.UserDbOperations
import kotlinx.coroutines.*

class UserDetailsDialog : DialogFragment() {
    private lateinit var binding: FragmentUserDetailsDialogBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserDetailsDialogBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SharedPreference
        val sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString(USER_ID,"")

        binding.btnSaveUser.setOnClickListener{
            with(binding){
                if (edtFirstName.text.isNullOrEmpty()){
                    edtFirstName.error = "Required"
                    return@setOnClickListener
                }

                if (edtLastName.text.isNullOrEmpty()){
                    edtLastName.error = "Required"
                    return@setOnClickListener
                }
            }
            val realmConfig = RealmConfig.getConfiguration()
            val userDbOperations = UserDbOperations(realmConfig)

            val firstName = binding.edtFirstName.text.toString()
            val lastName = binding.edtLastName.text.toString()

            val coroutineContext = Job() + Dispatchers.IO
            val scope = CoroutineScope(coroutineContext + CoroutineName("CreateUser"))
            scope.launch (Dispatchers.IO) {
                if (userUID != null) {
                    userDbOperations.insertNewUser(userUID, firstName,lastName)
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(),"User Details Updated", Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                    }
                }
            }
        }
    }

}