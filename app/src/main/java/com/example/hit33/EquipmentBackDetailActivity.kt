package com.example.hit33

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class EquipmentBackDetailActivity : AppCompatActivity() {

    private var setCount = 0 // 세트 수 저장
    private var completedReps = 0 // 완료된 운동 횟수
    private lateinit var tvCount: TextView // 현재 횟수 표시
    private lateinit var tvSetCount: TextView // 세트 수 표시
    private lateinit var tvDistance: TextView // 측정된 거리 표시
    private lateinit var tvTitle: TextView // 운동 기구 이름 표시
    private var targetDistance: Double = 0.0 // 변동 가동 범위
    private var targetReps: Int = 10 // 목표 횟수
    private var totalDistance: Double = 0.0 // 누적 이동 거리

    private var samplingTime = 0.5 // 샘플링 시간 (초 단위)
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

        // 알림음 초기화
        mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startExercise()
        }
    }

    // 운동 시작 로직
    private fun startExercise() {
        Toast.makeText(this, "운동 준비 중... 5초 후 시작합니다", Toast.LENGTH_SHORT).show()

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

        Handler(Looper.getMainLooper()).postDelayed({
            val measuredRange = calculateDistanceWithGyro() // 누적 거리 계산
            targetDistance = measuredRange
            updateDistanceText()
            playBeepSound()
            Toast.makeText(this, "가동 범위 측정 완료: ${targetDistance}cm", Toast.LENGTH_SHORT).show()
            updateMovementCount(measuredRange) // 운동 횟수 업데이트
        }, 7000) // 7초 대기
    }

    // UI 업데이트
    private fun updateDistanceText() {
        tvDistance.text = "$targetDistance cm"
    }


    // 가속도와 자이로 데이터를 기반으로 포물선 이동 거리 계산
    private fun calculateDistanceWithGyro(
        accelX: Double = 0.0,
        accelY: Double = 0.0,
        accelZ: Double = 0.0,
        gyroX: Double = 0.0,
        gyroY: Double = 0.0,
        gyroZ: Double = 0.0
    ): Double {
        // 가속도 크기 계산
        val acceleration = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)

        // 자이로 데이터를 이용한 각속도 계산 (deg/sec -> rad/sec 변환)
        val angularVelocityX = Math.toRadians(gyroX)
        val angularVelocityY = Math.toRadians(gyroY)
        val angularVelocityZ = Math.toRadians(gyroZ)

        // 회전량 추적 (시간 경과에 따른 적분 필요)
        val deltaThetaX = angularVelocityX * samplingTime
        val deltaThetaY = angularVelocityY * samplingTime
        val deltaThetaZ = angularVelocityZ * samplingTime

        // 포물선 운동을 계산하기 위한 방향성 조정
        val adjustedAccelX = accelX - (accelY * deltaThetaZ) + (accelZ * deltaThetaY)
        val adjustedAccelY = accelY - (accelZ * deltaThetaX) + (accelX * deltaThetaZ)
        val adjustedAccelZ = accelZ - (accelX * deltaThetaY) + (accelY * deltaThetaX)

        // 보정된 가속도를 통해 이동 거리 계산
        val correctedAcceleration = Math.sqrt(
            adjustedAccelX * adjustedAccelX +
                    adjustedAccelY * adjustedAccelY +
                    adjustedAccelZ * adjustedAccelZ
        )

        // 이동 거리 계산 (s = 0.5 * a * t²)
        val distance = 0.5 * correctedAcceleration * samplingTime * samplingTime

        // 누적 이동 거리 갱신
        totalDistance += distance

        Log.d("EquipmentBackDetailActivity", """
        Acceleration: $acceleration cm
        Corrected Acceleration: $correctedAcceleration cm
        Angular Velocity (rad/sec): [$angularVelocityX, $angularVelocityY, $angularVelocityZ]
        Delta Theta: [$deltaThetaX, $deltaThetaY, $deltaThetaZ]
        Calculated Distance: $distance cm
        Total Distance: $totalDistance cm
    """.trimIndent())

        return totalDistance
    }



    // Bluetooth 데이터 수신 및 처리 로직 업데이트
    fun onBluetoothDataReceived(data: List<String>) {
        if (data.size >= 6) { // 최소 6개의 데이터(가속도 X, Y, Z + 자이로 X, Y, Z)
            val accelX = data[0].toDoubleOrNull() ?: 0.0
            val accelY = data[1].toDoubleOrNull() ?: 0.0
            val accelZ = data[2].toDoubleOrNull() ?: 0.0
            val gyroX = data[3].toDoubleOrNull() ?: 0.0
            val gyroY = data[4].toDoubleOrNull() ?: 0.0
            val gyroZ = data[5].toDoubleOrNull() ?: 0.0

            val measuredDistance = calculateDistanceWithGyro(accelX, accelY, accelZ, gyroX, gyroY, gyroZ)
            Log.d("EquipmentBackDetailActivity", "Measured Distance with Gyro: $measuredDistance cm")
        } else {
            Log.e("EquipmentBackDetailActivity", "Invalid data received from Bluetooth")
        }
    }

    // 운동 횟수 업데이트
    private fun updateMovementCount(currentDistance: Double) {
        if (currentDistance >= targetDistance) { // 기준 거리 초과 시
            completedReps++ // 완료된 운동 횟수
            tvCount.text = "운동 횟수: ${completedReps}회"
        }

        if (completedReps >= targetReps) { // 목표횟수 : targetReps
            completedReps = 0
            setCount++
            tvSetCount.text = "운동 세트: ${setCount}세트"
            Toast.makeText(this, "목표 횟수에 도달했습니다! 세트 수가 증가합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // MediaPlayer 자원 해제
    }
}
