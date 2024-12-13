package com.example.hit33

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ExerciseChangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_change)

        val sharedPreferences = getSharedPreferences("PlannerPrefs", MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val groupedData = mutableMapOf<String, MutableList<String>>()

        // 데이터를 그룹화
        for ((date, memo) in allEntries) {
            if (memo is String && memo.isNotEmpty()) {
                groupedData.getOrPut(memo) { mutableListOf() }.add(date)
            }
        }

        val dataContainer = findViewById<LinearLayout>(R.id.dataContainer)

        // 데이터를 화면에 표시
        for ((memo, dates) in groupedData) {
            // 날짜를 빠른 순으로 정렬
            val sortedDates = dates.sortedBy { parseDate(it) }

            // 메모 표시
            val memoTextView = TextView(this).apply {
                text = memo
                textSize = 18f
                setPadding(0, 16, 0, 8)
            }
            dataContainer.addView(memoTextView)

            // 정렬된 날짜 표시
            for (date in sortedDates) {
                val dateTextView = TextView(this).apply {
                    text = " - $date (${getDayOfWeek(date)})"
                    textSize = 16f
                    setPadding(16, 0, 0, 8)
                }
                dataContainer.addView(dateTextView)
            }
        }

        // 추이 변화 버튼 클릭 이벤트 추가
        val trackChangeButton = findViewById<Button>(R.id.trackChangeButton)
        trackChangeButton.setOnClickListener {
            val intent = Intent(this, ExerciseTrendActivity::class.java)
            startActivity(intent)
        }
    }

    private fun parseDate(date: String): Long {
        val parts = date.split("-").map { it.toInt() }
        val calendar = java.util.Calendar.getInstance()
        calendar.set(parts[0], parts[1] - 1, parts[2])
        return calendar.timeInMillis
    }

    private fun getDayOfWeek(date: String): String {
        val parts = date.split("-").map { it.toInt() }
        val calendar = java.util.Calendar.getInstance()
        calendar.set(parts[0], parts[1] - 1, parts[2])
        return java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(calendar.time)
    }
}



