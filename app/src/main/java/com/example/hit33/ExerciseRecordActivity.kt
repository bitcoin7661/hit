package com.example.hit33

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ExerciseRecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise_record)

        // Window Insets 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 전달받은 날짜와 메모 데이터를 가져오기
        val selectedDate = intent.getStringExtra("selectedDate") ?: "날짜 정보 없음"
        val memo = intent.getStringExtra("memo") ?: "메모 없음"

        // 전달받은 세트 수와 횟수 데이터를 가져오기
        val sets = intent.getIntExtra("sets", 0) // 세트 수
        val reps = intent.getIntExtra("reps", 0) // 횟수

        // 날짜와 메모를 화면에 표시
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val memoTextView = findViewById<TextView>(R.id.memoTextView)
        dateTextView.text = selectedDate
        memoTextView.text = memo

        // 세트 수와 횟수를 화면에 표시
        val setEditText = findViewById<EditText>(R.id.setEditText)
        val countEditText = findViewById<EditText>(R.id.countEditText)

        setEditText.setText(sets.toString()) // 세트 수 표시
        countEditText.setText(reps.toString()) // 횟수 표시
    }
}

