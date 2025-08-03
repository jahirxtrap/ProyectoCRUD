package com.jahirtrap.crudapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.jahirtrap.crudapp.MainActivity.Companion.showToast
import com.jahirtrap.crudapp.api.LoginRequest
import com.jahirtrap.crudapp.api.LoginResponse
import com.jahirtrap.crudapp.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {
    private lateinit var inpUsername: TextInputEditText
    private lateinit var inpPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inpUsername = view.findViewById(R.id.inp_username)
        inpPassword = view.findViewById(R.id.inp_password)
        btnLogin = view.findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val username = inpUsername.text.toString().trim()
            val password = inpPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showToast(requireContext(), "Todos los campos son obligatorios")
                return@setOnClickListener
            }

            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        val request = LoginRequest(username, password)
        RetrofitInstance.api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val context = requireContext()

                    if (user.is_admin) {
                        startActivity(Intent(context, AdminUsersActivity::class.java))
                    } else {
                        val intent = Intent(context, ProfileActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                    requireActivity().finish()
                } else {
                    showToast(requireContext(), "Credenciales inválidas")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showToast(requireContext(), "Error de conexión")
            }
        })
    }
}