package com.example.carpooling.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.carpooling.R
import com.example.carpooling.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageView: ImageView
    private lateinit var btnChangePhoto: Button
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageView = findViewById(R.id.profileImage)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        val nameView = findViewById<TextView>(R.id.profileName)
        val emailView = findViewById<TextView>(R.id.profileEmail)
        val nicknameView = findViewById<TextView>(R.id.profileNickname)
        val ageView = findViewById<TextView>(R.id.profileAge)
        val roleView = findViewById<TextView>(R.id.profileRole)

        // --- Дані користувача ---
        val sharedPrefs = getSharedPreferences("user", MODE_PRIVATE)
        userId = sharedPrefs.getInt("userId", -1)

        // --- Відновлюємо фото профілю з файлу, якщо є в User (photoUri) ---
        CoroutineScope(Dispatchers.IO).launch {
            if (userId != -1) {
                val user = AppDatabase.getDatabase(this@ProfileActivity).userDao().getUserById(userId)
                runOnUiThread {
                    if (user?.photoUri != null && user.photoUri.isNotBlank()) {
                        val file = File(user.photoUri)
                        if (file.exists()) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            imageView.setImageBitmap(bitmap)
                        } else {
                            imageView.setImageResource(R.drawable.ic_person)
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_person)
                    }
                    user?.let {
                        nameView.text = "Ім'я: ${it.name}"
                        emailView.text = "Email: ${it.email}"
                        nicknameView.text = "Нікнейм: ${it.nickname}"
                        ageView.text = "Вік: ${it.age}"
                        roleView.text = "Роль: ${if (it.role == "driver") "Водій" else "Пасажир"}"
                    }
                }
            }
        }

        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    // --- Обробка вибору фото ---
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            imageUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val fileName = "profile_photo_${userId}.jpg"
                val file = File(filesDir, fileName)
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)

                // --- Зберігаємо шлях до фото в SharedPreferences ---
                getSharedPreferences("user", MODE_PRIVATE).edit()
                    .putString("profilePhotoPath", file.absolutePath)
                    .apply()

                // --- ОНОВЛЮЄМО User у БД (photoUri) ---
                CoroutineScope(Dispatchers.IO).launch {
                    if (userId != -1) {
                        val db = AppDatabase.getDatabase(this@ProfileActivity)
                        db.userDao().updateUserPhoto(userId, file.absolutePath)
                    }
                }
            }
        }
    }
}