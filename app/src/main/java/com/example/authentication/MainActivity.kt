package com.example.authentication

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlanningAdapter
    private val planningList = mutableListOf<String>()
    private var selectedDate: String? = null // Untuk menyimpan tanggal yang dipilih

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup RecyclerView
        setupRecyclerView()

        // Handle DatePicker
        val calendarIcon = findViewById<ImageView>(R.id.btnCalendar)
        val selectedDateText = findViewById<TextView>(R.id.selectedDateText)


        val todayDate = getFormattedDate(Calendar.getInstance())
        selectedDate = todayDate
        selectedDateText.text = todayDate
        loadDataForDate(todayDate)


        calendarIcon.setOnClickListener {
            showDatePicker { selectedDate ->
                this.selectedDate = selectedDate
                selectedDateText.text = selectedDate
                loadDataForDate(selectedDate)
            }
        }


        createUserDocumentIfNotExists()


        val addButton = findViewById<Button>(R.id.btnAdd)
        addButton.setOnClickListener {
            showAddPlanningDialog()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycleView)
        adapter = PlanningAdapter(planningList) { itemToDelete ->
            deleteItemFromFirestore(itemToDelete)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun createUserDocumentIfNotExists() {
        val userId = auth.currentUser?.uid ?: return
        val userDoc = db.collection("users").document(userId)

        userDoc.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    userDoc.set(mapOf<String, Any>())
                        .addOnSuccessListener {
                            Log.d("Firestore", "User document created successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error creating user document: $e")
                        }
                } else {
                    Log.d("Firestore", "User document already exists.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking user document: $e")
            }
    }

    private fun loadDataForDate(date: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val plansForDate = (document.get(date) as? List<String>) ?: emptyList()
                planningList.clear()
                planningList.addAll(plansForDate)
                adapter.notifyDataSetChanged()
                Log.d("Firestore", "Data for $date loaded successfully: $plansForDate")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error loading data for $date: $e")
            }
    }

    private fun saveListToFirestore() {
        val userId = auth.currentUser?.uid ?: return
        val date = selectedDate ?: return

        db.collection("users").document(userId)
            .update(date, planningList)
            .addOnSuccessListener {
                Log.d("Firestore", "List for $date saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving list for $date: $e")
            }
    }

    private fun deleteItemFromFirestore(item: String) {
        val position = planningList.indexOf(item)
        if (position != -1) {
            planningList.removeAt(position)
            adapter.notifyItemRemoved(position)
            saveListToFirestore()
        }
    }

    private fun showAddPlanningDialog() {
        val editText = EditText(this).apply {
            hint = "Enter new planning"
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("Add New Planning")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val newItem = editText.text.toString().trim()
                if (newItem.isNotBlank()) {
                    val timeAdded = getCurrentTime()
                    selectedDate?.let { date ->
                        val planningWithTime = "$newItem - $timeAdded"
                        planningList.add(planningWithTime)

                        // Update RecyclerView
                        adapter.notifyItemInserted(planningList.size - 1)

                        saveListToFirestore()
                    } ?: run {
                        Log.e("AddPlanning", "Selected date is null")
                    }
                } else {
                    Log.e("Validation", "Empty input is not allowed")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = getFormattedDate(Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                })
                onDateSelected(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun getFormattedDate(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("EEEE, yyyy -MM - dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
