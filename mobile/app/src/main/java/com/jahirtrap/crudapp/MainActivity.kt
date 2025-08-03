package com.jahirtrap.crudapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var pager: ViewPager2
    private lateinit var navMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager = findViewById(R.id.pager)
        navMenu = findViewById(R.id.nav_menu)

        val fragments = listOf(LoginFragment(), RegisterFragment())

        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        navMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_login -> pager.currentItem = 0
                R.id.nav_register -> pager.currentItem = 1
            }
            true
        }

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                navMenu.menu[position].isChecked = true
            }
        })
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
