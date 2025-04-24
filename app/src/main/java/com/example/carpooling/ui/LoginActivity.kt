package com.example.carpooling.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carpooling.MainActivity
import com.example.carpooling.R
import com.example.carpooling.data.AppDatabase
import com.example.carpooling.data.User
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEdit = findViewById<EditText>(R.id.editEmail)
        val passwordEdit = findViewById<EditText>(R.id.editPassword)
        val nameEdit = findViewById<EditText>(R.id.editName)
        val nicknameEdit = findViewById<EditText>(R.id.editNickname)
        val ageEdit = findViewById<EditText>(R.id.editAge)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupRole)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        btnRegister.setOnClickListener {
            val email = emailEdit.text.toString()
            val password = passwordEdit.text.toString()
            val name = nameEdit.text.toString()
            val nickname = nicknameEdit.text.toString()
            val age = ageEdit.text.toString().toIntOrNull() ?: 18
            val selectedRoleId = radioGroup.checkedRadioButtonId
            val role = if (selectedRoleId == R.id.radioDriver) "driver" else "passenger"

            if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank() && nickname.isNotBlank()) {
                lifecycleScope.launch {
                    val exists = userDao.getUserByEmail(email)
                    if (exists == null) {
                        val user = User(
                            email = email,
                            password = password,
                            name = name,
                            nickname = nickname,
                            age = age,
                            role = role
                        )
                        userDao.insertUser(user)
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Успішно зареєстровано! Можете увійти.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Такий email вже існує!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Заповніть усі поля!", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogin.setOnClickListener {
            val email = emailEdit.text.toString()
            val password = passwordEdit.text.toString()
            lifecycleScope.launch {
                val user = userDao.authenticate(email, password)
                if (user != null) {
                    // Збережи user.id або email в SharedPreferences для доступу в додатку!
                    getSharedPreferences("user", MODE_PRIVATE).edit()
                        .putInt("userId", user.id)
                        .apply()
                    // Переходимо на головний екран
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Невірний email або пароль!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}