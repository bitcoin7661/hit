package com.example.hit33

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

// 등 운동기구 기능
class EquipmentBackDetailActivity : AppCompatActivity() {

    private var setCount = 0 // 세트 수 저장
    private var completedReps = 0 // 완료된 운동 횟수
    private lateinit var tvCount: TextView // 현재 횟수 표시
    private lateinit var tvSetCount: TextView // 세트 수 표시
    private lateinit var tvDistance: TextView // 측정된 거리 표시
    private lateinit var tvTitle: TextView // 운동 기구 이름 표시
    private var targetDistance: Int = 100 // 초기 가동 범위
    private var targetReps: Int = 10 // 목표 횟수
    private lateinit var dataCalculator: DataCalculator // DataCalculator 인스턴스
    private lateinit var mediaPlayer: MediaPlayer // 알림음 재생

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_detail)

        // UI 요소 초기화
        tvCount = findViewById(R.id.tvCount)
        tvSetCount = findViewById(R.id.tvSetCount)
        tvDistance = findViewById(R.id.tvDistance)
        tvTitle = findViewById(R.id.tvTitle)

        // Intent로부터 운동 기구 이름 가져오기
        val equipmentName = intent.getStringExtra("EQUIPMENT_NAME")
        Log.d("EquipmentDetailActivity", "Received EQUIPMENT_NAME: $equipmentName")
        tvTitle.text = equipmentName ?: "운동 기구"

        // DataCalculator 인스턴스 초기화
        dataCalculator = DataCalculator(this)
        dataCalculator.setTargetCount(targetReps)

        // 알림음 초기화
        mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startExercise()
        }

        // 전송 버튼 클릭 이벤트 추가
        findViewById<Button>(R.id.btnSend).setOnClickListener {
            saveRecord(equipmentName ?: "운동 기구")
        }
    }

    // 운동 시작 로직
    private fun startExercise() {
        Toast.makeText(this, "운동 준비 중... 5초 후 시작합니다", Toast.LENGTH_SHORT).show()

        // 3초 후 알림음 재생 및 측정 시작
        Handler(Looper.getMainLooper()).postDelayed({
            playBeepSound()
            measureRange()
        }, 5000) // 5초 대기
    }

    // 알림음 재생
    private fun playBeepSound() {
        mediaPlayer.start()
    }

    // 가동 범위 측정
    private fun measureRange() {
        Toast.makeText(this, "가동 범위 측정 중... 7초간 기다려주세요.", Toast.LENGTH_SHORT).show()

        // 5초 동안 측정 대기
        Handler(Looper.getMainLooper()).postDelayed({
            calculateDistance()
            targetDistance = 100 // 하드웨어에서 측정된 예시 값
            updateDistanceText()
            playBeepSound()
            Toast.makeText(this, "가동 범위 측정 완료: ${targetDistance}cm", Toast.LENGTH_SHORT).show()
        }, 7000) // 7초 대기
    }

    private fun calculateDistance() {
        targetDistance = 100 // 하드웨어에서 받은 데이터
        Log.d("EquipmentBackDetailActivity", "Calculated Distance: $targetDistance cm")
    }

    // 운동거리(가동범위) UI 업데이트
    private fun updateDistanceText() {
        tvDistance.text = "$targetDistance cm"
    }

    // Bluetooth 데이터 처리
    fun onBluetoothDataReceived(data: List<String>) {
        val results = dataCalculator.calculate(data)
        val currentRange = results["distance"]?.toIntOrNull() ?: 0

        tvDistance.text = results["distance"] ?: "데이터 없음"
        tvCount.text = "운동 횟수: ${completedReps}회"

        if (currentRange >= targetDistance) {
            completedReps++
            tvCount.text = "운동 횟수: ${completedReps}회"
            checkTargetReached()
        }
    }

    // 목표 횟수 도달 확인
    private fun checkTargetReached() {
        if (completedReps >= targetReps) {
            setCount++
            completedReps = 0
            tvCount.text = "운동 횟수: 0회"
            tvSetCount.text = "운동 세트: ${setCount}세트"
            Toast.makeText(this, "목표 횟수에 도달했습니다! 세트 수가 증가합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 전송 버튼: 현재 날짜에 세트 수와 횟수 저장
    private fun saveRecord(equipmentName: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sharedPreferences = getSharedPreferences("ExerciseRecords", Context.MODE_PRIVATE)

        sharedPreferences.edit().apply {
            putInt("${currentDate}_${equipmentName}_sets", setCount)
            putInt("${currentDate}_${equipmentName}_reps", completedReps)
            apply()
        }

        Toast.makeText(this, "$equipmentName 기록이 저장되었습니다!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // MediaPlayer 자원 해제
    }
}


