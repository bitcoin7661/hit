package com.example.hit33

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.util.Log

class PlannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_planner)

//        // PlannerActivity.kt
//        findViewById<Button>(R.id.weekMonthPlannerButton).setOnClickListener {
//            val intent = Intent(this, WeekMonthPlannerActivity::class.java)
//            startActivity(intent)
//        }
        // PlannerActivity에서 버튼 클릭 시 로그 추가
        findViewById<Button>(R.id.weekMonthPlannerButton).setOnClickListener {
            Log.d("PlannerActivity", "주간/월간 플래너 버튼 클릭됨")
            val intent = Intent(this, WeekMonthPlannerActivity::class.java)
            startActivity(intent)
        }
    }
}

