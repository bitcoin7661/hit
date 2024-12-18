package com.example.hit33

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.R

class EquipmentChest : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chest)

        // 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btnbenchpress).setOnClickListener { openInfo("벤치프레스") }
        findViewById<Button>(R.id.btninclinpress).setOnClickListener { openInfo("인클라인프레스") }
        findViewById<Button>(R.id.btnchestpress).setOnClickListener { openInfo("체스트프레스") }
        findViewById<Button>(R.id.btnfly).setOnClickListener { openInfo("펙 덱 플라이") }
    }


    private fun openInfo(equipment: String) {
        val intent = Intent(this, EquipmentBackDetailActivity::class.java)
        intent.putExtra("EQUIPMENT_NAME", equipment) // 선택한 운동 기구 이름을 전달
        startActivity(intent) // Activity 실행
    }
}

