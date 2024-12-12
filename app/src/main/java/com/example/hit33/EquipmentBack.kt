package com.example.hit33

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.R

class EquipmentBack : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back)

        // 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btnpullup).setOnClickListener { openInfo("풀업") }
        findViewById<Button>(R.id.btnletpulldown).setOnClickListener { openInfo("렛풀다운") }
        findViewById<Button>(R.id.btnseatedrow).setOnClickListener { openInfo("시티드로우") }
        findViewById<Button>(R.id.btnbabelow).setOnClickListener { openInfo("바벨로우") }
    }


    private fun openInfo(equipment: String) {
        val intent = Intent(this, EquipmentBackDetailActivity::class.java)
        intent.putExtra("EQUIPMENT_NAME", equipment) // 선택한 운동 기구 이름을 전달
        startActivity(intent) // Activity 실행
    }
}

