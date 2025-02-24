package com.example.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailLogin: EditText
    private lateinit var passwordLogin: EditText
    private lateinit var btnLogin: Button
    private lateinit var textRegister: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()


        emailLogin = findViewById(R.id.et_email)
        passwordLogin = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        textRegister = findViewById(R.id.txRegister)

        btnLogin.setOnClickListener { login() }


        textRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        Log.d(TAG, "Login")

        if (!validate()) {
            onLoginFailed()
            return
        }

        btnLogin.isEnabled = false

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging in...")
        progressDialog.show()

        val email = emailLogin.text.toString()
        val password = passwordLogin.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {

                    onLoginSuccess()
                } else {

                    onLoginFailed()
                    Log.e(TAG, "Login error: ${task.exception?.message}")
                }
            }
    }

    private fun onLoginSuccess() {
        btnLogin.isEnabled = true
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onLoginFailed() {
        Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_LONG).show()
        btnLogin.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val email = emailLogin.text.toString()
        val password = passwordLogin.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLogin.error = "Enter a valid email"
            valid = false
        } else {
            emailLogin.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            passwordLogin.error = "Password must be at least 6 characters"
            valid = false
        } else {
            passwordLogin.error = null
        }

        return valid
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
