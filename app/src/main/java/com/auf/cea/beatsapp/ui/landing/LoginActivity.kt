package com.auf.cea.beatsapp.ui.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.databinding.ActivityLogin2Binding
import com.auf.cea.beatsapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityLogin2Binding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()

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