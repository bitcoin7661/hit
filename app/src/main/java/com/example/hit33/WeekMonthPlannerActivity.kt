package com.example.hit33

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WeekMonthPlannerActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var lastSelectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_month_planner)

        sharedPreferences = getSharedPreferences("PlannerPrefs", Context.MODE_PRIVATE)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val memoEditText = findViewById<EditText>(R.id.memoEditText)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            saveCurrentMemo(memoEditText) // 기존 메모 저장
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            lastSelectedDate = selectedDate

            val memo = sharedPreferences.getString(selectedDate, "") ?: ""
            memoEditText.setText(memo)

            Toast.makeText(this, "$selectedDate 선택됨", Toast.LENGTH_SHORT).show()
        }

        // "운동 기록 바로가기" 버튼 클릭 이벤트
        val navigateRecordButton = findViewById<Button>(R.id.navigateButton)
        navigateRecordButton.setOnClickListener {
            saveCurrentMemo(memoEditText) // 현재 메모 저장
            val intent = Intent(this, ExerciseRecordActivity::class.java)

            // 선택된 날짜와 메모를 Intent로 전달
            lastSelectedDate?.let { date ->
                intent.putExtra("selectedDate", date)
            }
            val memo = memoEditText.text.toString()
            intent.putExtra("memo", memo)

            startActivity(intent)
        }

        // "운동 변화 바로가기" 버튼 클릭 이벤트
        val navigateChangeButton = findViewById<Button>(R.id.changeButton)
        navigateChangeButton.setOnClickListener {
            saveCurrentMemo(memoEditText) // 현재 메모 저장
            val intent = Intent(this, ExerciseChangeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveCurrentMemo(memoEditText: EditText) {
        lastSelectedDate?.let { date ->
            val memo = memoEditText.text.toString()
            sharedPreferences.edit().putString(date, memo).apply()
            Toast.makeText(this, "$date 메모 저장됨", Toast.LENGTH_SHORT).show()
        }
    }
}







