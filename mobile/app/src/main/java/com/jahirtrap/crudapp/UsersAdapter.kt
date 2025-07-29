package com.jahirtrap.crudapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jahirtrap.crudapp.api.UserProfile

class UsersAdapter(
    private val users: List<UserProfile>,
    private val onClick: (UserProfile) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvUsername.text = user.username
        holder.tvEmail.text = "Correo: " + user.email
        holder.tvRole.text = if (user.is_admin) "Admin: Si" else "Admin: No"
        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount() = users.size
}
