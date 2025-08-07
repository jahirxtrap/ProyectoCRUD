package com.jahirtrap.crudapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jahirtrap.crudapp.api.ApiProvider

class SettingsActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, SettingsFragment()).commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var preferences: SharedPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

            findPreference<Preference>(getString(R.string.api_url_key))?.let { preference ->
                var value =
                    preferences.getString(preference.key, getString(R.string.api_url_default))
                preference.summary = getString(R.string.api_url_summary, value)

                preference.setOnPreferenceClickListener {
                    showDialog(
                        title = preference.title,
                        inputText = value,
                        onPositive = { input ->
                            value = input?.trim().orEmpty()
                                .ifEmpty { getString(R.string.api_url_default) }
                            preferences.edit { putString(preference.key, value) }
                            ApiProvider.reset()
                            preference.summary = getString(R.string.api_url_summary, value)
                        }
                    )
                    true
                }
            }

            findPreference<Preference>(getString(R.string.reset_preferences_key))?.setOnPreferenceClickListener {
                showDialog(
                    title = getString(R.string.reset_preferences_title),
                    message = getString(R.string.reset_preferences_alert_message),
                    onPositive = {
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                            .edit { clear() }

                        PreferenceManager.setDefaultValues(
                            requireContext(),
                            R.xml.root_preferences,
                            true
                        )

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.settings_container, SettingsFragment()).commit()
                    }
                )
                true
            }
        }

        fun showDialog(
            title: CharSequence? = "",
            message: CharSequence? = null,
            inputText: String? = null,
            onPositive: (input: String?) -> Unit,
            onNegative: (() -> Unit)? = null
        ) {
            val context = requireContext()
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
