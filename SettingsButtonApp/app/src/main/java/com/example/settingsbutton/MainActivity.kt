package com.example.settingsbutton

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {
    private val cameraRequestCode = 100
    private val storageRequestCode = 101
    private var pendingBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.settingsButton).setOnClickListener { openFrontCamera() }
    }

    private fun openFrontCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra("android.intent.extras.CAMERA_FACING", 1)
            putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        }
        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, "На устройстве нет приложения камеры", Toast.LENGTH_LONG).show()
            return
        }
        try {
            startActivityForResult(intent, cameraRequestCode)
        } catch (_: Exception) {
            Toast.makeText(this, "Не удалось открыть камеру", Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Uses the classic camera intent result for compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != cameraRequestCode || resultCode != RESULT_OK) return
        val bitmap = data?.extras?.get("data") as? Bitmap
        if (bitmap == null) {
            Toast.makeText(this, "Камера не вернула фото. Попробуйте ещё раз.", Toast.LENGTH_LONG).show()
            return
        }
        pendingBitmap = bitmap
        savePhoto()
    }

    private fun savePhoto() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), storageRequestCode)
            return
        }
        val bitmap = pendingBitmap ?: return
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "ButtonApp_" + System.currentTimeMillis() + ".jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ButtonApp")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        try {
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri == null) {
                Toast.makeText(this, "Не удалось сохранить фото", Toast.LENGTH_LONG).show()
                return
            }
            contentResolver.openOutputStream(uri)?.use { output ->
                check(bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output))
            } ?: error("Cannot open image output stream")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver.update(uri, ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }, null, null)
            }
            Toast.makeText(this, "Фото сохранено в галерею", Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
            Toast.makeText(this, "Не удалось сохранить фото в галерею", Toast.LENGTH_LONG).show()
        } finally {
            pendingBitmap = null
        }
    }

    @Deprecated("Uses the classic permission callback for compatibility")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storageRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) savePhoto()
            else {
                pendingBitmap = null
                Toast.makeText(this, "Разрешите доступ к хранилищу, чтобы сохранить фото", Toast.LENGTH_LONG).show()
            }
        }
    }
}
