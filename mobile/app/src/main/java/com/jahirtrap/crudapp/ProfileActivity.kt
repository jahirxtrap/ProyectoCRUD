package com.jahirtrap.crudapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.RetrofitInstance
import com.jahirtrap.crudapp.api.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvWelcome = findViewById(R.id.tvWelcome)
        tvEmail = findViewById(R.id.tvEmail)
        tvRole = findViewById(R.id.tvRole)

        loadProfile()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.order) {
            100 -> {
                logout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun loadProfile() {
        RetrofitInstance.api.getProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    tvWelcome.text = getString(R.string.welcome, profile.username)
                    tvEmail.text = getString(R.string.email, profile.email)
                    tvRole.text = getString(R.string.admin, if (profile.is_admin) "Si" else "No")
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

    private fun logout() {
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
