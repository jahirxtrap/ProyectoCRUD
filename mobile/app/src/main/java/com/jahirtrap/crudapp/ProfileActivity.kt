package com.jahirtrap.crudapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.jahirtrap.crudapp.MainActivity.Companion.showToast
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.RetrofitInstance
import com.jahirtrap.crudapp.api.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var txtWelcome: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtAdmin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        toolbar = findViewById(R.id.toolbar)
        txtWelcome = findViewById(R.id.txt_welcome)
        txtEmail = findViewById(R.id.txt_email)
        txtAdmin = findViewById(R.id.txt_admin)

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btn_logout -> {
                    logout()
                    true
                }

                else -> false
            }
        }

        loadProfile()
    }

    private fun loadProfile() {
        RetrofitInstance.api.getProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    val adminStatus = getString(if (profile.is_admin) R.string.yes else R.string.no)
                    txtWelcome.text = getString(R.string.welcome_temp, profile.username)
                    txtEmail.text = getString(R.string.email_temp, profile.email)
                    txtAdmin.text = getString(R.string.admin_temp, adminStatus)
                } else {
                    showToast(this@ProfileActivity, "Error al cargar perfil")
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                showToast(this@ProfileActivity, "Error de red")
            }
        })
    }

    private fun logout() {
        RetrofitInstance.api.logout().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showToast(this@ProfileActivity, "Error al cerrar sesión")
            }
        })
    }
}
