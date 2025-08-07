package com.jahirtrap.crudapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        pager = findViewById(R.id.pager)

        val fragments = listOf(LoginFragment(), RegisterFragment())

        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        toolbar.menu[1].isVisible = false
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    fun showPage(page: Int) {
        pager.currentItem = page
    }

    companion object {
        private var toast: Toast? = null

        fun showToast(context: Context, message: String?) {
            toast?.cancel()
            toast = Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
            toast!!.show()
        }
    }
}
