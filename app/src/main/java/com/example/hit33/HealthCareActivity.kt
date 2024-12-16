package com.example.hit33

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.ble.BluetoothScanActivity
import com.google.android.material.card.MaterialCardView

class HealthCareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_care)

        // MaterialCardView 버튼들 설정
        findViewById<MaterialCardView>(R.id.btnShoulder).setOnClickListener { openInfo("어깨") }
        findViewById<MaterialCardView>(R.id.btnBack).setOnClickListener { openInfo("등") }
        findViewById<MaterialCardView>(R.id.btnChest).setOnClickListener { openInfo("가슴") }
        findViewById<MaterialCardView>(R.id.btnlowerbody).setOnClickListener { openInfo("하체") }
        findViewById<MaterialCardView>(R.id.btnArms).setOnClickListener { openInfo("팔") }

        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            startActivity(Intent(this, BluetoothScanActivity::class.java))
        }
    }


    private fun openInfo(part: String) {
        val intent = when (part) {
            "가슴" -> Intent(this, EquipmentChest::class.java)
            "등" -> Intent(this, EquipmentBack::class.java)
            "하체" -> Intent(this, EquipmentlowerBody::class.java)
            else -> Intent(this, EquipmentBackDetailActivity::class.java)
        }
        intent.putExtra("EQUIPMENT_NAME", part)
        startActivity(intent)
    }
}