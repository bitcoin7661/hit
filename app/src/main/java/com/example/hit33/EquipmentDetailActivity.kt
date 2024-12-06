package com.example.hit33

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//등
class EquipmentDetailActivity : AppCompatActivity() {

    private lateinit var tvDistance: TextView
    private lateinit var etMoving: EditText // 가동범위 표시 EditText
    private lateinit var tvTitle: TextView // 운동 기구 이름을 표시할 TextView
    private var targetDistance: Int = 30 // 초기 가동 범위
    private lateinit var dataCalculator: DataCalculator // DataCalculator 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_detail)

        // UI 요소 초기화
        tvDistance = findViewById(R.id.tvDistance)
        etMoving = findViewById(R.id.btnmoving)
        tvTitle = findViewById(R.id.tvTitle) // 운동 기구 이름을 표시할 TextView 초기화

        // Intent로부터 운동 기구 이름 가져오기
        val equipmentName = intent.getStringExtra("EQUIPMENT_NAME")
        Log.d("EquipmentDetailActivity", "Received EQUIPMENT_NAME: $equipmentName") // 로그 추가

        // 제목 설정
        tvTitle.text = equipmentName ?: "운동 기구" // 제목을 운동 기구 이름으로 설정, null이면 기본값 설정
        etMoving.setText("$targetDistance cm") // 가동 범위 초기화

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

    // Bluetooth 데이터 처리 메소드
    fun onBluetoothDataReceived(data: List<String>) {
        val results = dataCalculator.calculate(data) // 가공된 데이터 계산
        tvDistance.text = results[0] // 이동 거리 업데이트
        // 다른 UI 업데이트를 여기에 추가
    }
}
