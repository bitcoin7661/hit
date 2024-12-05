//package com.example.hit9
//
//import DataCalculator
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//
//class EquipmentDetailActivity : AppCompatActivity() {
//
//    private lateinit var tvDistance: TextView
//    private lateinit var tvSpeed: TextView
//    private lateinit var tvCount: TextView
//    private lateinit var tvsetcount: TextView
//    private lateinit var etTargetMinDistance: EditText // 최소 운동 거리
//    private lateinit var etTargetMaxDistance: EditText // 최대 운동 거리
//    private lateinit var etTargetCount: EditText
//    private lateinit var bluetoothManager: BluetoothManager
//    private lateinit var tvTitle: TextView // 타이틀을 표시할 TextView
//    private lateinit var dataCalculator: DataCalculator // DataCalculator 인스턴스
//
//    private var targetMinDistance: Double = 0.0
//    private var targetMaxDistance: Double = 0.0
//    private var targetCount: Int = 0
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_equipment_detail)
//
//        // UI 요소 초기화
//        tvTitle = findViewById(R.id.tvTitle)
//        tvDistance = findViewById(R.id.tvDistance)
//        tvSpeed = findViewById(R.id.tvSpeed)
//        tvCount = findViewById(R.id.tvCount)
//        tvsetcount = findViewById(R.id.tvsetcount)
//
//        // 운동 거리 최소 및 최대 입력 필드 초기화
//        etTargetMinDistance = findViewById(R.id.etTargetMinDistance)
//        etTargetMaxDistance = findViewById(R.id.etTargetMaxDistance)
//        etTargetCount = findViewById(R.id.etTargetCount)
//
//        // DataCalculator 인스턴스 초기화
//        dataCalculator = DataCalculator(this)
//
//        // Intent로부터 운동 기구 이름 가져오기
//        val equipmentName = intent.getStringExtra("EQUIPMENT_NAME")
//        tvTitle.text = equipmentName ?: "운동 기구"
//
//        // BluetoothManager 초기화
//        bluetoothManager = BluetoothManager(this, object : BluetoothManager.DataReceiver {
//            override fun onDataReceived(data: List<String>) {
//                updateUI(data) // 가공된 데이터 UI 업데이트
//            }
//        })
//
//        // 저장 기능 버튼 클릭 리스너
//        findViewById<Button>(R.id.btnSave).setOnClickListener {
//            saveSettings() // 사용자가 설정한 목표 값을 저장
//        }
//    }
//
//    // UI 업데이트 메소드
//    fun updateUI(results: List<String>) {
//        runOnUiThread {
//            if (results.size >= 4) {
//                tvDistance.text = results[0] // 이동 거리 업데이트
//                tvSpeed.text = results[1]    // 이동 속도 업데이트
//                tvCount.text = results[2]    // 운동 횟수 업데이트
//                tvsetcount.text = results[3]   // 운동 세트 업데이트
//            }
//        }
//    }
//
//    // 사용자 설정 저장 메소드
//    private fun saveSettings() {
//        val minDistanceInput = etTargetMinDistance.text.toString()
//        val maxDistanceInput = etTargetMaxDistance.text.toString()
//        val countInput = etTargetCount.text.toString()
//
//        // 입력 값 확인 후 저장
//        if (minDistanceInput.isNotEmpty() && maxDistanceInput.isNotEmpty() && countInput.isNotEmpty()) {
//            targetMinDistance = minDistanceInput.toDoubleOrNull() ?: 0.0
//            targetMaxDistance = maxDistanceInput.toDoubleOrNull() ?: 0.0
//            targetCount = countInput.toIntOrNull() ?: 0
//
//            // DataCalculator에 목표 횟수 설정
//            dataCalculator.setTargetCount(targetCount)
//
//            Toast.makeText(this, "목표가 설정되었습니다.\n목표 이동 거리: $targetMinDistance cm - $targetMaxDistance cm\n목표 운동 횟수: $targetCount 회", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "모든 필드를 입력해야 합니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//}

package com.example.hit33

import DataCalculator
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.R

class EquipmentDetailActivity : AppCompatActivity() {

    private lateinit var tvDistance: TextView
    private lateinit var etMoving: EditText // 가동범위 표시 EditText
    private var targetDistance: Int = 30 // 초기 가동 범위
    private lateinit var dataCalculator: DataCalculator // DataCalculator 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_detail)

        // UI 요소 초기화
        tvDistance = findViewById(R.id.tvDistance)
        etMoving = findViewById(R.id.btnmoving)

        // 가동 범위 초기화
        etMoving.setText("$targetDistance cm") // 초기값 설정

        // DataCalculator 인스턴스 초기화
        dataCalculator = DataCalculator(this)

        // 버튼 설정
        findViewById<Button>(R.id.btnIncrease).setOnClickListener {
            targetDistance += 1 // 1cm 증가
            updateDistanceText() // UI 업데이트
        }

        findViewById<Button>(R.id.btnDecrease).setOnClickListener {
            if (targetDistance > 0) { // 최소 0cm로 제한
                targetDistance -= 1 // 1cm 감소
                updateDistanceText() // UI 업데이트
            } else {
                Toast.makeText(this, "가동 범위는 0보다 작을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 저장 기능 버튼 클릭 리스너
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveSettings() // 사용자가 설정한 목표 값을 저장
        }
    }

    // 현재 거리 텍스트 업데이트 메소드
    private fun updateDistanceText() {
        etMoving.setText("$targetDistance cm") // EditText에 현재 거리 업데이트
    }

    // 사용자 설정 저장 메소드
    private fun saveSettings() {
        Toast.makeText(this, "설정이 저장되었습니다: 가동범위 $targetDistance cm", Toast.LENGTH_SHORT).show()
    }

    // Bluetooth 데이터 처리 메소드 (예시)
    fun onBluetoothDataReceived(data: List<String>) {
        val results = dataCalculator.calculate(data) // 가공된 데이터 계산
        tvDistance.text = results[0] // 이동 거리 업데이트
        // 다른 UI 업데이트를 여기에 추가
    }
}



