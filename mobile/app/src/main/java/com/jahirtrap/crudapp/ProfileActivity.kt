package com.jahirtrap.crudapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.android.material.appbar.MaterialToolbar
import com.jahirtrap.crudapp.MainActivity.Companion.showSnackbar
import com.jahirtrap.crudapp.api.ApiProvider
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var progress: FrameLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var txtWelcome: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtAdmin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        progress = findViewById(R.id.progress)
        toolbar = findViewById(R.id.toolbar)
        txtWelcome = findViewById(R.id.txt_welcome)
        txtEmail = findViewById(R.id.txt_email)
        txtAdmin = findViewById(R.id.txt_admin)

        toolbar.menu[0].isVisible = true
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }

                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                R.id.action_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }

                else -> false
            }
        }

        loadProfile()

        onBackPressedDispatcher.addCallback(this) {
            logout()
        }
    }

    private fun loadProfile() {
        ApiProvider.getApi(this).getProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    val adminStatus = getString(if (profile.is_admin) R.string.yes else R.string.no)
                    txtWelcome.text = getString(R.string.welcome_temp, profile.username)
                    txtEmail.text = getString(R.string.email_temp, profile.email)
                    txtAdmin.text = getString(R.string.admin_temp, adminStatus)
                } else {
                    showSnackbar(this@ProfileActivity, "Error al cargar perfil")
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                showSnackbar(this@ProfileActivity, "Error de conexión")
            }
        })
    }

    private fun logout() {
        MainActivity.Companion.showDialog(
            context = this@ProfileActivity,
            title = getString(R.string.logout),
            message = getString(R.string.logout_alert_message),
            onPositive = {
                progress.visibility = View.VISIBLE
                ApiProvider.getApi(this).logout().enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {
                        progress.visibility = View.GONE
                        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        progress.visibility = View.GONE
                        showSnackbar(this@ProfileActivity, "Error al cerrar sesión")
                    }
                })
            }
        )
    }
}
