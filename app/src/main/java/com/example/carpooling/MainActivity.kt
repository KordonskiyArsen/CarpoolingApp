package com.example.carpooling

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpooling.adapters.RideAdapter
import com.example.carpooling.data.Ride
import com.example.carpooling.ui.ProfileActivity
import com.example.carpooling.ui.RideDetailsActivity
import com.example.carpooling.ui.RidesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.carpooling.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var rideAdapter: RideAdapter
    private lateinit var editSearch: EditText
    private val ridesViewModel: RidesViewModel by viewModels()
    private var userRole: String? = null
    private var userId: Int = -1
    private var allRides: List<Ride> = listOf() // Зберігаємо всі поїздки для фільтрації

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewRides)
        recyclerView.layoutManager = LinearLayoutManager(this)
        editSearch = findViewById(R.id.editSearch)

        // Отримуємо userId з SharedPreferences
        userId = getSharedPreferences("user", MODE_PRIVATE).getInt("userId", -1)

        lifecycleScope.launch {
            ridesViewModel.rides.collectLatest { rides ->
                allRides = rides
                showFilteredRides(editSearch.text.toString())
            }
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                showFilteredRides(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val fab: FloatingActionButton = findViewById(R.id.fabAddRide)
        fab.setOnClickListener {
            showAddRideDialog()
        }

        val btnProfile: Button = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Перевірка ролі користувача
        if (userId != -1) {
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(this@MainActivity).userDao().getUserById(userId)
                }
                userRole = user?.role
                // Ховаємо FAB якщо це пасажир
                if (userRole != "driver") {
                    fab.hide()
                }
            }
        }
    }

    // Фільтрація поїздок за напрямом
    private fun showFilteredRides(query: String) {
        val normalizedQuery = query.trim().replace("–", "-").replace("→", "-").replace(">", "-").replace("  ", " ").lowercase()
        val filtered = if (normalizedQuery.isBlank()) {
            allRides
        } else {
            allRides.filter { ride ->
                val route1 = "${ride.fromLocation} ${ride.toLocation}".lowercase()
                val route2 = "${ride.fromLocation}-${ride.toLocation}".lowercase()
                val route3 = "${ride.fromLocation}→${ride.toLocation}".lowercase()
                route1.contains(normalizedQuery) ||
                        route2.contains(normalizedQuery) ||
                        route3.contains(normalizedQuery)
            }
        }
        rideAdapter = RideAdapter(filtered) { ride ->
            val intent = Intent(this, RideDetailsActivity::class.java)
            intent.putExtra("rideId", ride.id)
            startActivity(intent)
        }
        recyclerView.adapter = rideAdapter
    }

    private fun showAddRideDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_ride, null)
        val fromEdit = dialogView.findViewById<EditText>(R.id.editFrom)
        val toEdit = dialogView.findViewById<EditText>(R.id.editTo)
        val dateEdit = dialogView.findViewById<EditText>(R.id.editDate)
        val seatsEdit = dialogView.findViewById<EditText>(R.id.editSeats)
        val priceEdit = dialogView.findViewById<EditText>(R.id.editPrice)
        val commentEdit = dialogView.findViewById<EditText>(R.id.editComment)

        AlertDialog.Builder(this)
            .setTitle("Додати поїздку")
            .setView(dialogView)
            .setPositiveButton("Додати") { _, _ ->
                val ride = Ride(
                    fromLocation = fromEdit.text.toString(),
                    toLocation = toEdit.text.toString(),
                    dateTime = dateEdit.text.toString(),
                    availableSeats = seatsEdit.text.toString().toIntOrNull() ?: 1,
                    price = priceEdit.text.toString().toDoubleOrNull() ?: 0.0,
                    comment = commentEdit.text.toString(),
                    creatorId = userId
                )
                ridesViewModel.addRide(ride)
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }
}