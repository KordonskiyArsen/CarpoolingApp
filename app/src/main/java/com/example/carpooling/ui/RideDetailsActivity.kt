package com.example.carpooling.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpooling.R
import com.example.carpooling.adapters.CommentAdapter
import com.example.carpooling.data.AppDatabase
import com.example.carpooling.data.Comment
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File

class RideDetailsActivity : AppCompatActivity() {
    private var rideId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_details)

        rideId = intent.getIntExtra("rideId", -1)
        if (rideId == -1) {
            finish()
            return
        }

        val routeText = findViewById<TextView>(R.id.textRoute)
        val dateTimeText = findViewById<TextView>(R.id.textDateTime)
        val priceText = findViewById<TextView>(R.id.textPrice)
        val seatsText = findViewById<TextView>(R.id.textSeats)
        val commentText = findViewById<TextView>(R.id.textComment)

        // ==== Водій ====
        val driverPhoto = findViewById<ImageView>(R.id.driverPhoto)
        val driverName = findViewById<TextView>(R.id.driverName)
        val driverEmail = findViewById<TextView>(R.id.driverEmail)
        val driverNick = findViewById<TextView>(R.id.driverNickname)

        // ==== КНОПКА ЗАБРОНЮВАТИ ====
        val btnBookRide = findViewById<Button>(R.id.btnBookRide)
        btnBookRide.setOnClickListener {
            Toast.makeText(this, "Стосовно бронювання з вами звʼяжеться водій", Toast.LENGTH_SHORT).show()
        }

        // ==== Коментарі ====
        val recyclerComments = findViewById<RecyclerView>(R.id.recyclerComments)
        recyclerComments.layoutManager = LinearLayoutManager(this)
        val editComment = findViewById<EditText>(R.id.editComment)
        val btnSendComment = findViewById<Button>(R.id.btnSendComment)

        // Завантаження даних про поїздку і водія
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@RideDetailsActivity)
            val ride = db.rideDao().getRideById(rideId)
            ride?.let {
                routeText.text = "${it.fromLocation} → ${it.toLocation}"
                dateTimeText.text = it.dateTime
                priceText.text = "Ціна: ${it.price}₴"
                seatsText.text = "Вільних місць: ${it.availableSeats}"
                commentText.text = it.comment

                // Інформація про водія (з фото!)
                val driver = db.userDao().getUserById(it.creatorId)
                driver?.let { d ->
                    // Фото
                    if (!d.photoUri.isNullOrBlank()) {
                        val file = File(d.photoUri)
                        if (file.exists()) {
                            Glide.with(this@RideDetailsActivity)
                                .load(file)
                                .apply(RequestOptions.circleCropTransform())
                                .placeholder(R.drawable.ic_person)
                                .into(driverPhoto)
                        } else {
                            driverPhoto.setImageResource(R.drawable.ic_person)
                        }
                    } else {
                        driverPhoto.setImageResource(R.drawable.ic_person)
                    }
                    driverName.text = d.name
                    driverEmail.text = d.email
                    driverNick.text = d.nickname
                }
            }
            loadComments()
        }

        // Додаємо коментар
        btnSendComment.setOnClickListener {
            val commentValue = editComment.text.toString().trim()
            if (commentValue.isNotEmpty()) {
                val userId = getSharedPreferences("user", MODE_PRIVATE).getInt("userId", -1)
                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(this@RideDetailsActivity)
                    db.commentDao().insertComment(
                        Comment(
                            rideId = rideId,
                            userId = userId,
                            text = commentValue
                        )
                    )
                    runOnUiThread {
                        editComment.text.clear()
                        loadComments()
                    }
                }
            }
        }
    }

    // Функція для завантаження і показу коментарів
    private fun loadComments() {
        val recyclerComments = findViewById<RecyclerView>(R.id.recyclerComments)
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@RideDetailsActivity)
            val comments = db.commentDao().getCommentsForRide(rideId)
            val commentsWithUsers = comments.mapNotNull { comment ->
                val user = db.userDao().getUserById(comment.userId)
                if (user != null) comment to user else null
            }
            runOnUiThread {
                recyclerComments.adapter = CommentAdapter(commentsWithUsers)
            }
        }
    }
}