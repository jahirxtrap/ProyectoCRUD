package com.jahirtrap.crudapp

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.jahirtrap.crudapp.LoginActivity.Companion.showToast
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.RetrofitInstance
import com.jahirtrap.crudapp.api.UserProfile
import com.jahirtrap.crudapp.api.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUsersActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var rvwUsers: RecyclerView
    private lateinit var adapter: UsersAdapter
    private val users = mutableListOf<UserProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

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
                val position = parent.getChildAdapterPosition(view)
                val itemCount = parent.adapter?.itemCount ?: 0
                rect.bottom = if (position < itemCount - 1) marginPx else 0
            }
        })

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btn_logout -> {
                    logout()
                    true
                }

                else -> false
            }
        }

        refresh.setOnRefreshListener {
            loadUsers()
        }

        loadUsers()
    }

    private fun loadUsers() {
        refresh.isRefreshing = true
        RetrofitInstance.api.getUsers().enqueue(object : Callback<UsersResponse> {
            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    users.clear()
                    users.addAll(response.body()!!.users)
                    adapter.notifyDataSetChanged()
                } else {
                    showToast(this@AdminUsersActivity, "Error al obtener usuarios")
                }
                refresh.isRefreshing = false
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                refresh.isRefreshing = false
                showToast(this@AdminUsersActivity, "Error de conexión")
            }
        })
    }

    private fun logout() {
        RetrofitInstance.api.logout().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                startActivity(Intent(this@AdminUsersActivity, LoginActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showToast(this@AdminUsersActivity, "Error al cerrar sesión")
            }
        })
    }
}
