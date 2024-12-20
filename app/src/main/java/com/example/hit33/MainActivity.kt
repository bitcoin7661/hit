package com.example.hit33

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 스플래시 테마에서 메인 테마로 전환
        setTheme(R.style.Theme_Hit33)

        setContentView(R.layout.activity_main)

        // 헬스케어 카드 클릭 리스너
        findViewById<MaterialCardView>(R.id.cardHealthCare)?.setOnClickListener { openHealthCare() }

        // 플래너 카드 클릭 리스너
        findViewById<MaterialCardView>(R.id.cardPlanner)?.setOnClickListener { openPlanner() }

        // 프로필 카드 클릭 리스너
        findViewById<MaterialCardView>(R.id.cardProfile)?.setOnClickListener { openProfile() }

        // 이벤트 카드 클릭 리스너 추가
        findViewById<MaterialCardView>(R.id.cardEvent)?.setOnClickListener {
            try {
                val intent = Intent(this, EventActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "오류 발생: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun openHealthCare() {
        val intent = Intent(this, HealthCareActivity::class.java)
        startActivity(intent)
    }

    private fun openPlanner() {
        val intent = Intent(this, PlannerActivity::class.java)
        startActivity(intent)
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}


