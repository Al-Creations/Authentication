package com.example.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek apakah pengguna sudah login
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Pengguna sudah login, pindah ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Tutup SplashActivity agar tidak bisa kembali
        } else {
            // Pengguna belum login, pindah ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup SplashActivity
        }
    }
}
