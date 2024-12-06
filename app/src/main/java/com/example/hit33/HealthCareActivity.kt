package com.example.hit33

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.R

class HealthCareActivity : AppCompatActivity(), BluetoothManager.DataReceiver {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var tvStatus: TextView // Bluetooth 상태 표시를 위한 TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_care)

        tvStatus = findViewById(R.id.btnConnect) // Bluetooth 상태 TextView 초기화
        bluetoothManager = BluetoothManager(this, this) // BluetoothManager 초기화

        findViewById<Button>(R.id.btnShoulder).setOnClickListener { openInfo("어깨") }
        findViewById<Button>(R.id.btnBack).setOnClickListener { openInfo("등") }
        findViewById<Button>(R.id.btnChest).setOnClickListener { openInfo("가슴") }
        findViewById<Button>(R.id.btnlowerbody).setOnClickListener { openInfo("하체") }
        findViewById<Button>(R.id.btnArms).setOnClickListener { openInfo("팔") }
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            bluetoothManager.connectToDevice() // Bluetooth 장치 연결
        }
    }

    private fun openInfo(part: String) {
        val intent = when (part) {
            "가슴" -> Intent(this, EquipmentChest::class.java) // 가슴 운동 액티비티
            "등" -> Intent(this, EquipmentBack::class.java) // 등 운동 액티비티
            "하체" -> Intent(this, EquipmentlowerBody::class.java) // 하체 운동 액티비티
//            "팔" -> Intent(this, EquipmentArm::class.java) // 팔 운동 액티비티
//            "어깨" -> Intent(this, EquipmentShoulder::class.java) // 등 운동 액티비티
            // 다른 운동 부위 클릭 시에 대해 여기에 액티비티 추가
            else -> Intent(this, EquipmentDetailActivity::class.java)
        }
        intent.putExtra("EQUIPMENT_NAME", part) // 운동 부위를 넘김
        startActivity(intent) // 적절한 Activity 실행
    }

    override fun onDataReceived(data: List<String>) {
        // 수신된 데이터를 UI 업데이트
        runOnUiThread {
            tvStatus.text = "수신 데이터: $data"
        }
    }

    // 기타 필요할 때 추가적인 메서드를 여기서 구현하세요.

    override fun onDestroy() {
        super.onDestroy()
        bluetoothManager.closeConnection() // Bluetooth 연결 종료
    }
}
