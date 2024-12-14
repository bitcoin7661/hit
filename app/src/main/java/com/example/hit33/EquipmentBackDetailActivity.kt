package com.example.hit33

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//등 운동기구 기능
class EquipmentBackDetailActivity : AppCompatActivity(), OnTargetReachedListener  {

    private var setCount = 0 // 세트 수를 저장하는 변수
    private lateinit var tvCount: TextView
    private lateinit var tvsetcount: TextView // 세트 수를 표시할 TextView
    private lateinit var tvSpeed: TextView
    private lateinit var tvDistance: TextView
    private lateinit var etMoving: EditText // 가동범위 표시 EditText
    private lateinit var tvTitle: TextView // 운동 기구 이름을 표시할 TextView
    private var targetDistance: Int = 30 // 초기 가동 범위
    private lateinit var dataCalculator: DataCalculator // DataCalculator 인스턴스



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_detail)

        // UI 요소 초기화
        tvsetcount = findViewById(R.id.tvsetcount)
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
        dataCalculator.onTargetReachedListener = this // 콜백 연결
        dataCalculator.setTargetCount(10) // 목표 횟수 설정



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

        // 시작 기능 버튼 클릭 리스너
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            saveSettings() // 사용자가 설정한 목표 값을 저장
        }
    }

    // 콜백: 목표 횟수에 도달 시 호출
    override fun onTargetReached() {
        setCount++ // 세트 수 증가
        tvsetcount.text = "운동 세트: ${setCount}세트" // UI 업데이트
        Toast.makeText(this, "목표 횟수에 도달했습니다! 세트 수가 증가합니다.", Toast.LENGTH_SHORT).show()
    }

    // 현재 거리 텍스트 업데이트 메소드
    private fun updateDistanceText() {
        etMoving.setText("$targetDistance cm") // EditText에 현재 거리 업데이트
    }

    // 사용자 설정 시작 메소드
    private fun saveSettings() {
        // 목표 가동 범위 전달
        dataCalculator.setTargetDistance(targetDistance)

        // 목표 횟수 설정 (예: 10회, 사용자 입력 추가 가능)
        val targetCount = 10
        dataCalculator.setTargetCount(targetCount)

        Toast.makeText(this, "운동이 시작됩니다: 가동 범위 $targetDistance cm, 목표 횟수 ${targetCount}회", Toast.LENGTH_SHORT).show()

    }

    // Bluetooth 데이터 처리 메소드
    fun onBluetoothDataReceived(data: List<String>) {
        val results = dataCalculator.calculate(data) // 가공된 데이터 계산
        // UI 업데이트
        tvDistance.text = results["distance"] ?: "데이터 없음"
        tvSpeed.text = results["speed"] ?: "데이터 없음"
        tvCount.text = "운동 횟수: ${results["reps"] ?: "0회"}"
    }
    // 다른 UI 업데이트를 여기에 추가
}



