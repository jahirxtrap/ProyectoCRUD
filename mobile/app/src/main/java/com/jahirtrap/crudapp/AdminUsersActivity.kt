package com.jahirtrap.crudapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        loadUsers()
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
}
