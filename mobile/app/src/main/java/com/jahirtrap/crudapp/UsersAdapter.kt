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
        val txtUsername: TextView = itemView.findViewById(R.id.txt_username)
        val txtEmail: TextView = itemView.findViewById(R.id.txt_email)
        val txtAdmin: TextView = itemView.findViewById(R.id.txt_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context
        val adminStatus = context.getString(if (user.is_admin) R.string.yes else R.string.no)
        holder.txtUsername.text = user.username
        holder.txtEmail.text = context.getString(R.string.email_temp, user.email)
        holder.txtAdmin.text = context.getString(R.string.admin_temp, adminStatus)
        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount() = users.size
}
