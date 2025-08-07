package com.jahirtrap.crudapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class AboutActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var versionText: MaterialTextView
    private lateinit var btnAppInfo: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        toolbar = findViewById(R.id.toolbar)
        versionText = findViewById(R.id.version_text)
        btnAppInfo = findViewById(R.id.btn_app_info)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        try {
            versionText.text = getString(
                R.string.version,
                packageManager.getPackageInfo(packageName, 0).versionName
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.fillInStackTrace()
        }

        btnAppInfo.setOnClickListener { v: View? ->
            val uri = Uri.fromParts("package", packageName, null)
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri))
        }
    }
}