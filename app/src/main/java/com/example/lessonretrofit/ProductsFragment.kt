package com.example.lessonretrofit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lessonretrofit.adapter.RvAdapter
import com.example.lessonretrofit.databinding.FragmentProductsBinding
import com.example.lessonretrofit.retrofit.MainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private val viewModel:LoginViewModel by activityViewModels()
    private lateinit var mainApi: MainApi
    private lateinit var adapter: RvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRetrofit()
        initRv()
        viewModel.token.observe(viewLifecycleOwner){ token->
            CoroutineScope(Dispatchers.IO).launch {
                val list = mainApi.getAllProducts(token)
                requireActivity().runOnUiThread{
                    adapter.submitList(list.products)
                }
            }
        }
    }

    private fun initRetrofit(){
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
        mainApi = retrofit.create(MainApi::class.java)
    }

    private fun initRv() = with(binding){
        adapter = RvAdapter()
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter
    }
}