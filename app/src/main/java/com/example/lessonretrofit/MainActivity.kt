package com.example.lessonretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lessonretrofit.adapter.RvAdapter
import com.example.lessonretrofit.databinding.ActivityMainBinding
import com.example.lessonretrofit.retrofit.MainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RvAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RvAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter

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

            CoroutineScope(Dispatchers.IO).launch {
                val list = mainApi.getAllProducts()
                    runOnUiThread {
                        binding.apply {
                            adapter.submitList(list.products)
                        }
                    }
            }
    }
}