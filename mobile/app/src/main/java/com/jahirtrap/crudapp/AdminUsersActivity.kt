package com.jahirtrap.crudapp

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.jahirtrap.crudapp.MainActivity.Companion.showToast
import com.jahirtrap.crudapp.api.ApiProvider
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.UserProfile
import com.jahirtrap.crudapp.api.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUsersActivity : AppCompatActivity() {
    private lateinit var progress: FrameLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var rvwUsers: RecyclerView
    private lateinit var adapter: UsersAdapter
    private val users = mutableListOf<UserProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

        progress = findViewById(R.id.progress)
        toolbar = findViewById(R.id.toolbar)
        refresh = findViewById(R.id.refresh)
        rvwUsers = findViewById(R.id.rvw_users)

        adapter = UsersAdapter(users) { user ->
            showToast(this@AdminUsersActivity, "Seleccionaste: ${user.username}")
            // Actividad para editar/eliminar
        }

        rvwUsers.layoutManager = LinearLayoutManager(this)
        rvwUsers.adapter = adapter

        val marginPx = (16 * resources.displayMetrics.density).toInt()
        rvwUsers.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                rect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    rect.top = marginPx
                }
            }
        })

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

        refresh.setOnRefreshListener {
            loadUsers()
        }

        loadUsers()

        onBackPressedDispatcher.addCallback(this) {
            logout()
        }
    }

    private fun loadUsers() {
        refresh.isRefreshing = true
        ApiProvider.getApi(this).getUsers().enqueue(object : Callback<UsersResponse> {
            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                refresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    users.clear()
                    users.addAll(response.body()!!.users)
                    adapter.notifyDataSetChanged()
                } else {
                    showToast(this@AdminUsersActivity, "Error al obtener usuarios")
                }
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                refresh.isRefreshing = false
                showToast(this@AdminUsersActivity, "Error de conexión")
            }
        })
    }

    private fun logout() {
        MainActivity.Companion.showDialog(
            context = this@AdminUsersActivity,
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
                        val intent = Intent(this@AdminUsersActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        progress.visibility = View.GONE
                        showToast(this@AdminUsersActivity, "Error al cerrar sesión")
                    }
                })
            }
        )
    }
}
