package com.example.hit33

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null
    private var dailySteps = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)

        // 날짜 확인 및 걸음 수 초기화
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = sharedPreferences.getString("lastDate", "")
        if (currentDate != lastDate) {
            resetDailySteps(currentDate)
        }

        // 걸음 수 데이터 UI
        val tvStepCount = findViewById<TextView>(R.id.tv_step_count)
        updateStepCount(tvStepCount)

        // BMI 계산
        val etHeight = findViewById<EditText>(R.id.et_height)
        val etWeight = findViewById<EditText>(R.id.et_weight)
        val btnCalculateBMI = findViewById<Button>(R.id.btn_calculate_bmi)
        val tvBMIResult = findViewById<TextView>(R.id.tv_bmi_result)

        btnCalculateBMI.setOnClickListener {
            val heightCm = etHeight.text.toString().toDoubleOrNull()
            val weightKg = etWeight.text.toString().toDoubleOrNull()

            if (heightCm != null && weightKg != null && heightCm > 0) {
                val heightM = heightCm / 100
                val bmi = weightKg / (heightM * heightM)
                val bmiCategory = when {
                    bmi < 18.5 -> "저체중"
                    bmi < 25.0 -> "정상"
                    bmi < 30.0 -> "과체중"
                    else -> "비만"
                }
                tvBMIResult.text = "BMI: %.2f\n상태: %s".format(bmi, bmiCategory)
                tvBMIResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
            } else {
                tvBMIResult.text = "올바른 값을 입력하세요."
                tvBMIResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
        }

        // 센서 매니저 초기화
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private fun resetDailySteps(currentDate: String) {
        dailySteps = 0
        sharedPreferences.edit().apply {
            putString("lastDate", currentDate)
            putInt("dailySteps", dailySteps)
            apply()
        }
    }

    private fun updateStepCount(tvStepCount: TextView) {
        dailySteps = sharedPreferences.getInt("dailySteps", 0)
        tvStepCount.text = "오늘 걸음 수: $dailySteps 걸음"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Null 안전 처리
        val sensor = event?.sensor ?: return
        if (sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values.firstOrNull()?.toInt() ?: return
            val savedSteps = sharedPreferences.getInt("dailySteps", 0)
            val stepsToday = totalSteps - savedSteps

            // 업데이트된 값 저장
            dailySteps = stepsToday
            sharedPreferences.edit().putInt("dailySteps", totalSteps).apply()

            // UI 업데이트
            findViewById<TextView>(R.id.tv_step_count).text = "오늘 걸음 수: $dailySteps 걸음"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        stepCounter?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
