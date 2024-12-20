package com.example.hit33

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // 출석체크 버튼 클릭 리스너 추가
        findViewById<Button>(R.id.attendanceButton)?.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
        }

        // 포인트 상점 버튼 클릭 리스너 수정
        findViewById<Button>(R.id.pointStoreButton)?.setOnClickListener {
            val intent = Intent(this, PointShopActivity::class.java)
            startActivity(intent)
        }
    }
}

