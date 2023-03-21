package com.example.lessonretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lessonretrofit.databinding.ActivityMainBinding
import com.example.lessonretrofit.retrofit.AuthRequest
import com.example.lessonretrofit.retrofit.MainApi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val httpLoggingInterceptor = HttpLoggingInterceptor()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor.apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainApi = retrofit.create(MainApi::class.java)


        binding.btn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val user = mainApi.auth(
                    AuthRequest(
                        binding.edLogin.text.toString(),
                        binding.edPassword.text.toString()
                    )
                )
                    runOnUiThread {
                        binding.apply {
                            Picasso.get().load(user.image).into(imgAvatar)
                            tvFirsName.text = user.firstName
                            tvLastName.text = user.lastName
                        }
                    }
            }
        }
    }
}