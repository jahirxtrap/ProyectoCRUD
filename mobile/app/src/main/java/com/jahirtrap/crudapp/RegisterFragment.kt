package com.jahirtrap.crudapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.jahirtrap.crudapp.MainActivity.Companion.showToast
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.RegisterRequest
import com.jahirtrap.crudapp.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {
    private lateinit var inpUsername: TextInputEditText
    private lateinit var inpEmail: TextInputEditText
    private lateinit var inpPassword: TextInputEditText
    private lateinit var inpConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var txtLogin: MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inpUsername = view.findViewById(R.id.inp_username)
        inpEmail = view.findViewById(R.id.inp_email)
        inpPassword = view.findViewById(R.id.inp_password)
        inpConfirmPassword = view.findViewById(R.id.inp_confirm_password)
        btnRegister = view.findViewById(R.id.btn_register)
        txtLogin = view.findViewById(R.id.txt_login_)

        btnRegister.setOnClickListener {
            val username = inpUsername.text.toString().trim()
            val email = inpEmail.text.toString().trim()
            val password = inpPassword.text.toString()
            val confirmPassword = inpConfirmPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast(requireContext(), "Todos los campos son obligatorios")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showToast(requireContext(), "Las contraseñas no coinciden")
                return@setOnClickListener
            }

            register(username, email, password)
        }

        txtLogin.text = "<< " + getString(R.string.login)
        txtLogin.setOnClickListener {
            (requireActivity() as? MainActivity)?.showPage(0)
        }
    }

    private fun register(username: String, email: String, password: String) {
        val request = RegisterRequest(username, email, password)
        RetrofitInstance.api.register(request).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    showToast(requireContext(), "Usuario registrado correctamente")
                    (requireActivity() as? MainActivity)?.showPage(0)
                } else {
                    showToast(requireContext(), response.body()?.message ?: "Error de registro")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showToast(requireContext(), "Error de conexión")
            }
        })
    }
}
