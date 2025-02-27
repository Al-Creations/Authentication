package com.example.authentication

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.10.17.198:8080/" // Ganti dengan URL API Anda
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRJZCI6IjE5MjYyMDE0IiwiY2xpZW50U2VjcmV0IjoiYmlzbWlsbGFoIiwiaWF0IjoxNTc5MTY2NDc3fQ.T5xuFl750KDGyblYBGDPpZ-fl4UDcOq4Rc6TWxux1VE")) // Menambahkan Interceptor dengan token
        .build()

//    val retrofit = Retrofit.Builder()
//        .baseUrl("http://api.unida.gontor.ac.id:1926/") // URL dasar API
//        .client(okHttpClient) // Menambahkan OkHttpClient ke Retrofit
//        .addConverterFactory(GsonConverterFactory.create()) // Menggunakan Gson untuk mengonversi JSON ke objek Kotlin
//        .build()

    val instance: LoginApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(LoginApi::class.java)
    }
}