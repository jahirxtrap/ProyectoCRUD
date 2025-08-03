package com.jahirtrap.crudapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jahirtrap.crudapp.api.ApiResponse
import com.jahirtrap.crudapp.api.RetrofitInstance
import com.jahirtrap.crudapp.api.UserProfile
import com.jahirtrap.crudapp.api.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var adapter: UsersAdapter
    private val users = mutableListOf<UserProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

        rvUsers = findViewById(R.id.rvUsers)
        adapter = UsersAdapter(users) { user ->
            Toast.makeText(this, "Seleccionaste: ${user.username}", Toast.LENGTH_SHORT).show()
            // Actividad para editar/eliminar
        }

        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter

        val marginPx = (16 * resources.displayMetrics.density).toInt()

        rvUsers.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: android.view.View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val itemCount = parent.adapter?.itemCount ?: 0
                outRect.bottom = if (position < itemCount - 1) marginPx else 0
            }
        })

        loadUsers()
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

    private fun loadUsers() {
        RetrofitInstance.api.getUsers().enqueue(object : Callback<UsersResponse> {
            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    users.clear()
                    users.addAll(response.body()!!.users)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AdminUsersActivity, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                Toast.makeText(this@AdminUsersActivity, "Error de red", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(
                    this@AdminUsersActivity,
                    "Error al cerrar sesión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
