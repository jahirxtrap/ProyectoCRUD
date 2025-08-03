package com.jahirtrap.crudapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.jahirtrap.crudapp.api.LoginRequest
import com.jahirtrap.crudapp.api.LoginResponse
import com.jahirtrap.crudapp.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var inpUsername: TextInputEditText
    private lateinit var inpPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inpUsername = findViewById(R.id.inp_username)
        inpPassword = findViewById(R.id.inp_password)
        btnLogin = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val username = inpUsername.text.toString()
            val password = inpPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showToast(this@LoginActivity, "Todos los campos son obligatorios")
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
                    if (user.is_admin) {
                        startActivity(Intent(this@LoginActivity, AdminUsersActivity::class.java))
                    } else {
                        val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    showToast(this@LoginActivity, "Credenciales inválidas")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showToast(this@LoginActivity, "Error de conexión")
            }
        })
    }

    companion object {
        private var toast: Toast? = null

        fun showToast(context: Context, message: String?) {
            toast?.cancel()
            toast = Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
            toast!!.show()
        }
    }
}
