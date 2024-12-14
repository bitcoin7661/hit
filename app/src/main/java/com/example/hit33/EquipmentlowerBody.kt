package com.example.hit33

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.R

class EquipmentlowerBody : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lowerbody)

        // 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btnsquat).setOnClickListener { openInfo("스쿼트") }
        findViewById<Button>(R.id.btnlegcull).setOnClickListener { openInfo("레그컬") }
        findViewById<Button>(R.id.btnlegExtention).setOnClickListener { openInfo("레그익스텐션") }
        findViewById<Button>(R.id.btninlegpress).setOnClickListener { openInfo("레그프레스") }
    }


    private fun openInfo(equipment: String) {
        // 선택된 운동 기구에 대한 정보를 보여줄 Activity를 실행
        val intent = Intent(this, EquipmentBackDetailActivity::class.java)
        intent.putExtra("EQUIPMENT_NAME", equipment) // 선택한 운동 기구 이름을 전달
        startActivity(intent) // Activity 실행
    }
}
