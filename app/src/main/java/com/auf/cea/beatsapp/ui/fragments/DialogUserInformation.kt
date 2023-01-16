package com.auf.cea.beatsapp.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.PREFERENCE_NAME
import com.auf.cea.beatsapp.constants.USER_ID
import com.auf.cea.beatsapp.constants.USER_NAME
import com.auf.cea.beatsapp.databinding.DialogUserInformationBinding
import com.auf.cea.beatsapp.models.realm.User
import com.auf.cea.beatsapp.services.realm.config.RealmConfig
import com.auf.cea.beatsapp.services.realm.operations.UserDbOperations
import io.realm.RealmConfiguration
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private const val ARG_PARAM1 = "userData"

class DialogUserInformation : DialogFragment() {
    private var userData: java.io.Serializable? = null
    private lateinit var binding: DialogUserInformationBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userUID:String
    private lateinit var userObject: User
    private lateinit var realmConfig:RealmConfiguration
    private lateinit var userDbOperations:UserDbOperations
    private lateinit var coroutineContext:CoroutineContext

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userData = it.getSerializable(ARG_PARAM1)
        }

        // SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        userUID = sharedPreferences.getString(USER_ID,"").toString()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogUserInformationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Database - Retrieve Information
        realmConfig = RealmConfig.getConfiguration()
        userDbOperations = UserDbOperations(realmConfig)
        coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("CreateUser"))
        scope.launch (Dispatchers.IO) {
            val result = userDbOperations.retrieveUserData(userUID)
            withContext(Dispatchers.Main) {
                if (result != null) {
                    userObject = User(result.id,result.firstName,result.lastName,result.track)

                with(binding){
                    edtFirstName.setText(userObject.firstName)
                    edtLastName.setText(userObject.lastName)
                    btnSaveUser.setOnClickListener{
                        if (edtFirstName.text.isNullOrEmpty()){
                            edtFirstName.error = "Required"
                            return@setOnClickListener
                        }

                        if (edtLastName.text.isNullOrEmpty()){
                            edtLastName.error = "Required"
                            return@setOnClickListener
                        }
                        val coroutineContext = Job() + Dispatchers.IO
                        val scope = CoroutineScope(coroutineContext + CoroutineName("Update User"))
                        scope.launch (Dispatchers.IO) {
                            withContext(Dispatchers.Main) {
                                userDbOperations.updateUser(userUID,edtFirstName.text.toString(),edtLastName.text.toString())
                                Toast.makeText(requireContext(), "User Details Updated", Toast.LENGTH_SHORT).show()
                                sharedPreferences.edit()
                                    .putString(USER_NAME,edtFirstName.text.toString())
                                    .apply()
                                dialog?.dismiss()
                            }
                        }
                    }
                }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(userData: java.io.Serializable) =
            DialogUserInformation().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, userData)
                }
            }
    }
}