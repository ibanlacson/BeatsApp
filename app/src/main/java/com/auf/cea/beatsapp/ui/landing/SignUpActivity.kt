package com.auf.cea.beatsapp.ui.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        // Set Listeners
        binding.btnLogin.setOnClickListener(this)
        binding.txtLogin.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            (R.id.btn_login) -> {
                // Check if empty or Password Match
                if (binding.txtEmail.text.isNullOrEmpty()){
                    binding.txtEmail.error = "Required"
                    return
                }
                if (binding.txtPassword.text.isNullOrEmpty()){
                    binding.txtPassword.error = "Required"
                    return
                }
                if (binding.txtConfirmPassword.text.isNullOrEmpty()){
                    binding.txtConfirmPassword.error = "Required"
                    return
                }
                if (binding.txtPassword.text.toString() != binding.txtConfirmPassword.text.toString()){
                    binding.txtPassword.error = "Password doesn't match"
                    binding.txtConfirmPassword.error = "Password doesn't match"
                }

                // Firebase Authentication
                val email = binding.txtEmail.text.toString()
                val password = binding.txtPassword.text.toString()
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            (R.id.txt_login) -> {
                // Go to Login Screen
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}