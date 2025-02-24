package com.example.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailRegister: EditText
    private lateinit var passwordRegister: EditText
    private lateinit var fullname: EditText
    private lateinit var btnRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        fullname = findViewById(R.id.fullname)
        emailRegister = findViewById(R.id.txEmailRegister)
        passwordRegister = findViewById(R.id.txPasswordRegister)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        val email = emailRegister.text.toString()
        val password = passwordRegister.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegister.error = "Enter a valid email"
            return
        }

        if (password.isEmpty() || password.length < 6) {
            passwordRegister.error = "Password must be at least 6 characters"
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering...")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = User(email)
                    database.reference.child("users").child(userId!!).setValue(user)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Database error: ${dbTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Log.e(TAG, "Registration error: ${task.exception?.message}")
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }

    data class User(val email: String)
}
