package com.example.hit33

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var pointStatus: TextView
    private lateinit var attendanceCount: TextView
    private val sharedPreferences by lazy { getSharedPreferences("AttendancePrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_button)

        // View 초기화
        pointStatus = findViewById(R.id.tvPointStatus)
        attendanceCount = findViewById(R.id.tvAttendanceCount)
        val btnCheckAttendance = findViewById<Button>(R.id.btnCheckAttendance)

        // UI 업데이트
        updateUI()

        // 출석체크 버튼 클릭 리스너
        btnCheckAttendance.setOnClickListener {
            handleAttendanceCheck()
        }
    }

    private fun handleAttendanceCheck() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastCheckedDate = sharedPreferences.getString("lastCheckedDate", null)
        var attendanceStreak = sharedPreferences.getInt("attendanceStreak", 0)
        var points = sharedPreferences.getInt("points", 0)

        if (today == lastCheckedDate) {
            showToast("오늘 이미 출석체크를 완료했습니다!")
        } else {
            // 출석 체크 처리
            attendanceStreak = if (attendanceStreak < 7) attendanceStreak + 1 else 1
            points += 10
            sharedPreferences.edit()
                .putString("lastCheckedDate", today)
                .putInt("attendanceStreak", attendanceStreak)
                .putInt("points", points)
                .apply()

            // 7일 체크 후 초기화
            if (attendanceStreak >= 7) {
                attendanceStreak = 0
                points = 0
                sharedPreferences.edit()
                    .putInt("attendanceStreak", attendanceStreak)
                    .putInt("points", points)
                    .apply()
                showToast("7일 출석체크 완료! 초기화됩니다.")
            } else {
                showToast("출석체크 완료! +10 포인트가 적립되었습니다.")
            }

            updateUI()

            // 포인트 상점 화면으로 이동
            val intent = Intent(this, PointShopActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI() {
        val points = sharedPreferences.getInt("points", 0)
        val attendanceStreak = sharedPreferences.getInt("attendanceStreak", 0)

        pointStatus.text = "현재 포인트: $points"
        attendanceCount.text = "출석체크 횟수: $attendanceStreak/7"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

