package com.jahirtrap.crudapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.jahirtrap.crudapp.MainActivity.Companion.showSnackbar
import com.jahirtrap.crudapp.api.ApiProvider
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {
    private lateinit var progress: FrameLayout
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
        progress = requireActivity().findViewById(R.id.progress)
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
                showSnackbar(requireActivity(), "Todos los campos son obligatorios")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showSnackbar(requireActivity(), "Las contraseñas no coinciden")
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
        progress.visibility = View.VISIBLE
        btnRegister.isEnabled = false
        val request = RegisterRequest(username, email, password)
        ApiProvider.getApi(requireContext()).register(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    progress.visibility = View.GONE
                    btnRegister.isEnabled = true
                    if (response.isSuccessful) {
                        showSnackbar(requireActivity(), "Usuario registrado correctamente")
                        (requireActivity() as? MainActivity)?.showPage(0)
                    } else {
                        showSnackbar(requireActivity(), "Error de registro")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    progress.visibility = View.GONE
                    btnRegister.isEnabled = true
                    showSnackbar(requireActivity(), "Error de conexión")
                }
            })
    }
}
