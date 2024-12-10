package com.example.hit33

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LowerBodyDetailActivity : AppCompatActivity(), BluetoothLEManager.DataReceiver {

    private lateinit var tvDistance: TextView
    private lateinit var tvSpeed: TextView
    private lateinit var tvCount: TextView
    private lateinit var etTargetMinDistance: EditText
    private lateinit var etTargetMaxDistance: EditText
    private lateinit var etTargetCount: EditText
    private lateinit var bluetoothLEManager: BluetoothLEManager
    private lateinit var tvTitle: TextView

    private var targetMinDistance: Double = 0.0
    private var targetMaxDistance: Double = 0.0
    private var targetCount: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_detail)

        // UI 요소 초기화
        initializeViews()

        // Intent로부터 운동 기구 이름 가져오기
        val equipmentName = intent.getStringExtra("EQUIPMENT_NAME")
        tvTitle.text = equipmentName ?: "운동 기구"

        // BluetoothLEManager 초기화
        bluetoothLEManager = BluetoothLEManager(this, this)

        // 저장 기능 버튼 클릭 리스너
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveSettings()
        }
    }

    private fun initializeViews() {
        tvTitle = findViewById(R.id.tvTitle)
        tvDistance = findViewById(R.id.tvDistance)
        tvSpeed = findViewById(R.id.tvSpeed)
        tvCount = findViewById(R.id.tvCount)
        etTargetMinDistance = findViewById(R.id.etTargetMinDistance)
        etTargetMaxDistance = findViewById(R.id.etTargetMaxDistance)
        etTargetCount = findViewById(R.id.etTargetCount)
    }

    // BLE 데이터 수신 구현
    override fun onDataReceived(data: List<String>) {
        runOnUiThread {
            if (data.size >= 3) {
                updateUI(data)
                checkTargets(data)
            }
        }
    }

    // BLE 장치 발견 콜백 구현
    override fun onDeviceFound(device: BluetoothDevice) {
        // 이 액티비티에서는 새로운 장치 검색을 하지 않으므로 구현하지 않음
    }

    private fun updateUI(results: List<String>) {
        try {
            tvDistance.text = "${results[0]} cm" // 이동 거리
            tvSpeed.text = "${results[1]} cm/s"  // 이동 속도
            tvCount.text = "${results[2]} 회"    // 운동 횟수
        } catch (e: Exception) {
            Toast.makeText(this, "데이터 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkTargets(results: List<String>) {
        try {
            val currentDistance = results[0].toDoubleOrNull() ?: 0.0
            val currentCount = results[2].toIntOrNull() ?: 0

            // 목표 거리 체크
            if (currentDistance < targetMinDistance) {
                Toast.makeText(this, "운동 거리가 너무 짧습니다!", Toast.LENGTH_SHORT).show()
            } else if (currentDistance > targetMaxDistance) {
                Toast.makeText(this, "운동 거리가 너무 깁니다!", Toast.LENGTH_SHORT).show()
            }

            // 목표 횟수 달성 체크
            if (currentCount >= targetCount && targetCount > 0) {
                Toast.makeText(this, "목표 운동 횟수를 달성했습니다!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            // 에러 처리
        }
    }

    private fun saveSettings() {
        val minDistanceInput = etTargetMinDistance.text.toString()
        val maxDistanceInput = etTargetMaxDistance.text.toString()
        val countInput = etTargetCount.text.toString()

        if (minDistanceInput.isNotEmpty() && maxDistanceInput.isNotEmpty() && countInput.isNotEmpty()) {
            try {
                targetMinDistance = minDistanceInput.toDouble()
                targetMaxDistance = maxDistanceInput.toDouble()
                targetCount = countInput.toInt()

                // 설정값 검증
                if (targetMinDistance >= targetMaxDistance) {
                    Toast.makeText(this, "최소 거리는 최대 거리보다 작아야 합니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (targetCount <= 0) {
                    Toast.makeText(this, "목표 횟수는 0보다 커야 합니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                // 설정값을 BLE 장치로 전송
                val settings = listOf(
                    targetMinDistance.toString(),
                    targetMaxDistance.toString(),
                    targetCount.toString()
                )
                bluetoothLEManager.sendData(settings)

                Toast.makeText(
                    this,
                    "목표가 설정되었습니다.\n" +
                            "목표 이동 거리: $targetMinDistance cm - $targetMaxDistance cm\n" +
                            "목표 운동 횟수: $targetCount 회",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "올바른 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "모든 필드를 입력해야 합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothLEManager.closeConnection()
    }
}