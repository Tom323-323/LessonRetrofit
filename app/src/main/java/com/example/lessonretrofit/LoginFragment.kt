package com.example.lessonretrofit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.lessonretrofit.databinding.FragmentLoginBinding
import com.example.lessonretrofit.retrofit.AuthRequest
import com.example.lessonretrofit.retrofit.MainApi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mainApi: MainApi
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRetrofit()
        binding.apply {
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_LoginFragment_to_productsFragment)

            }
            btnOk.setOnClickListener {
                auth(
                    AuthRequest(
                        etLogin.text.toString(),
                        etPassword.text.toString()
                    )
                )
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

    private fun auth(autRequest:AuthRequest){
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.auth(autRequest)
            val message = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message")
            }
            requireActivity().runOnUiThread {
                binding.tvError.text = message
                val user = response.body()
                if(user!=null){
                    Picasso.get().load(user.image).into(binding.imageView)
                    binding.tvName.text = user.firstName
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnOk.visibility = View.INVISIBLE
                    viewModel.token.value = user.token
                }
            }
        }
        return
    }

}