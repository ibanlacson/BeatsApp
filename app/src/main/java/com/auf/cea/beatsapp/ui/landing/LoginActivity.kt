package com.auf.cea.beatsapp.ui.landing

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.constants.IS_KEPT
import com.auf.cea.beatsapp.constants.PREFERENCE_NAME
import com.auf.cea.beatsapp.constants.USER_ID
import com.auf.cea.beatsapp.databinding.ActivityLogin2Binding
import com.auf.cea.beatsapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityLogin2Binding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        // Set Listeners
        with(binding){
            btnLogin.setOnClickListener(this@LoginActivity)
            txtSignUp.setOnClickListener(this@LoginActivity)
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id) {
            (R.id.btn_login) -> {
                // Check if empty
                if (binding.txtEmail.text.isNullOrEmpty()){
                    binding.txtEmail.error = "Required"
                    return
                }
                if (binding.txtPassword.text.isNullOrEmpty()){
                    binding.txtPassword.error = "Required"
                    return
                }

                // Firebase Authentication
                val email = binding.txtEmail.text.toString()
                val password = binding.txtPassword.text.toString()
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if (it.isSuccessful){

                        if(binding.chkStayLoggedIn.isChecked) {
                            val editor = sharedPreferences.edit()
                            editor.putString(IS_KEPT,"true")
                            editor.putString(USER_ID, it.result.user?.uid.toString())
                            editor.apply()
                        } else {
                            val editor = sharedPreferences.edit()
                            editor.putString(USER_ID, it.result.user?.uid.toString())
                            editor.apply()
                        }

                        Log.d("USER ID:", it.result.user?.uid.toString())
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            (R.id.txt_sign_up) -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }


}