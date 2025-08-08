package com.jahirtrap.crudapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

        toolbar.menu[0].isVisible = false
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
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
    }

    fun showPage(page: Int) {
        pager.currentItem = page
    }

    companion object {
        fun showSnackbar(activity: FragmentActivity, message: String?) {
            message?.let {
                Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    it,
                    Snackbar.LENGTH_SHORT
                ).setAction(R.string.close) { }.show()
            }
        }

        fun showDialog(
            context: Context,
            title: CharSequence? = "",
            message: CharSequence? = null,
            inputText: String? = null,
            onPositive: (input: String?) -> Unit,
            onNegative: (() -> Unit)? = null
        ) {
            val inputLayout: TextInputLayout? = inputText?.let {
                val layout = TextInputLayout(context).apply {
                    setPadding(
                        (16 * context.resources.displayMetrics.density).toInt(),
                        0,
                        (16 * context.resources.displayMetrics.density).toInt(),
                        0
                    )
                }
                val editText = TextInputEditText(context).apply { setText(it) }
                layout.addView(editText)
                layout.postDelayed({
                    editText.apply {
                        requestFocus()
                        setSelection(text?.length ?: 0)
                        val imm =
                            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                    }
                }, 100)
                layout
            }

            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .apply {
                    message?.let { setMessage(it) }
                    inputLayout?.let { setView(it) }
                }
                .setPositiveButton(R.string.accept) { _, _ ->
                    val input = inputLayout?.editText?.text?.toString()
                    onPositive(input)
                }.setNegativeButton(R.string.cancel) { _, _ -> onNegative?.invoke() }.show()
        }
    }
}
