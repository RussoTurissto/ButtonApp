package com.ncorti.kotlin.template.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.settingsButton)

        // Если Нажать -> Показать сообщение и закрыть через 2 секунды
        button.setOnClickListener {
            Toast.makeText(this, "Вы успешно нажали", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ finish() }, 2000)
        }

        // Если Удержать -> Показать сообщение и закрыть через 2 секунды
        button.setOnLongClickListener {
            Toast.makeText(this, "Вы успешно задали кнопку", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ finish() }, 2000)
            true
        }
    }
}
