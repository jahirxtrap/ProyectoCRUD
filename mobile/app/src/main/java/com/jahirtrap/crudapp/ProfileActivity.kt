package com.jahirtrap.crudapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var btnLogout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvWelcome = findViewById(R.id.tvWelcome)
        tvEmail = findViewById(R.id.tvEmail)
        tvRole = findViewById(R.id.tvRole)
        btnLogout = findViewById(R.id.btnLogout)

        loadProfile()

        btnLogout.setOnClickListener {
            RetrofitInstance.api.logout().enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                    finish()
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error al cerrar sesión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun loadProfile() {
        RetrofitInstance.api.getProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    tvWelcome.text = "Bienvenido, ${profile.username}"
                    tvEmail.text = "Email: ${profile.email}"
                    tvRole.text = "Rol: ${if (profile.is_admin) "Admin" else "Usuario"}"
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error al cargar perfil",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
