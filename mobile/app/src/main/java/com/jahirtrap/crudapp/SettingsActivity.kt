package com.jahirtrap.crudapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import com.jahirtrap.crudapp.MainActivity.Companion.showDialog
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
                        context = requireContext(),
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
                    context = requireContext(),
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
    }
}
