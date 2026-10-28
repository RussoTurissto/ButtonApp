package com.example.settingsbutton

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var exitScheduled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            showMessageAndExit("Вы успешно нажали")
        }
        settingsButton.setOnLongClickListener {
            showMessageAndExit("Вы успешно задали кнопку")
            true
        }
    }

    private fun showMessageAndExit(message: String) {
        if (exitScheduled) return
        exitScheduled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        mainHandler.postDelayed({
            finishAffinity()
            finishAndRemoveTask()
        }, 2_000L)
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
