package com.example.hit33

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.R

class ChestDetailActivity : AppCompatActivity() {

    private lateinit var tvDistance: TextView
    private lateinit var tvSpeed: TextView
    private lateinit var tvCount: TextView
    private lateinit var etTargetMinDistance: EditText // 최소 운동 거리
    private lateinit var etTargetMaxDistance: EditText // 최대 운동 거리
    private lateinit var etTargetCount: EditText
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var tvTitle: TextView // 타이틀을 표시할 TextView

    private var targetMinDistance: Double = 0.0
    private var targetMaxDistance: Double = 0.0
    private var targetCount: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_detail)

        // UI 요소 초기화
        tvTitle = findViewById(R.id.tvTitle)
        tvDistance = findViewById(R.id.tvDistance)
        tvSpeed = findViewById(R.id.tvSpeed)
        tvCount = findViewById(R.id.tvCount)

        // 운동 거리 최소 및 최대 입력 필드 초기화
        etTargetMinDistance = findViewById(R.id.etTargetMinDistance)
        etTargetMaxDistance = findViewById(R.id.etTargetMaxDistance)
        etTargetCount = findViewById(R.id.etTargetCount)

        // Intent로부터 운동 기구 이름 가져오기
        val equipmentName = intent.getStringExtra("EQUIPMENT_NAME")
        tvTitle.text = equipmentName ?: "운동 기구"

        // BluetoothManager 초기화
        bluetoothManager = BluetoothManager(this, object : BluetoothManager.DataReceiver {
            override fun onDataReceived(data: List<String>) {
                updateUI(data) // 가공된 데이터 UI 업데이트
            }
        })

        // 저장 기능 버튼 클릭 리스너
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveSettings() // 사용자가 설정한 목표 값을 저장
        }
    }

    // UI 업데이트 메소드
    fun updateUI(results: List<String>) {
        runOnUiThread {
            if (results.size >= 3) {
                tvDistance.text = results[0] // 이동 거리 업데이트
                tvSpeed.text = results[1]    // 이동 속도 업데이트
                tvCount.text = results[2]    // 운동 횟수 업데이트
            }
        }
    }

    // 사용자 설정 저장 메소드
    private fun saveSettings() {
        val minDistanceInput = etTargetMinDistance.text.toString()
        val maxDistanceInput = etTargetMaxDistance.text.toString()
        val countInput = etTargetCount.text.toString()

        // 입력 값 확인 후 저장
        if (minDistanceInput.isNotEmpty() && maxDistanceInput.isNotEmpty() && countInput.isNotEmpty()) {
            targetMinDistance = minDistanceInput.toDoubleOrNull() ?: 0.0
            targetMaxDistance = maxDistanceInput.toDoubleOrNull() ?: 0.0
            targetCount = countInput.toIntOrNull() ?: 0

            Toast.makeText(this, "목표가 설정되었습니다.\n목표 이동 거리: $targetMinDistance cm - $targetMaxDistance cm\n목표 운동 횟수: $targetCount 회", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "모든 필드를 입력해야 합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}






