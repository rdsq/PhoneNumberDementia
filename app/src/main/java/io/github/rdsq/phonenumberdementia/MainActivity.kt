package io.github.rdsq.phonenumberdementia

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var phoneNumberText: TextView
    private lateinit var copyButton: Button

    // This handles asking for permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getPhoneNumber()
            } else {
                phoneNumberText.text = getString(R.string.permission_denied)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneNumberText = findViewById(R.id.phoneNumberText)
        copyButton = findViewById(R.id.copyButton)

        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getPhoneNumber()
        } else {
            // Ask for permission
            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_NUMBERS)
        }
    }

    private fun getPhoneNumber() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            phoneNumberText.text = getString(R.string.permission_not_granted)
            copyButton.isEnabled = false
            return
        }

        val telephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val number = telephonyManager.line1Number

        if (!number.isNullOrBlank()) {
            phoneNumberText.text = number
            copyButton.isEnabled = true

            copyButton.setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.clipboard_label), number)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
            }
        } else {
            phoneNumberText.text = getString(R.string.number_not_available)
            copyButton.isEnabled = false
        }
    }
}
