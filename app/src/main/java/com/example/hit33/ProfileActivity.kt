package com.example.hit33

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // UI 요소 연결
        val etName = findViewById<EditText>(R.id.et_name)
        val etBirthdate = findViewById<EditText>(R.id.et_birthdate)
        val etHeight = findViewById<EditText>(R.id.et_height)
        val etWeight = findViewById<EditText>(R.id.et_weight)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnDetails = findViewById<Button>(R.id.btn_details)

        // 저장 버튼 클릭 이벤트
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val birthdate = etBirthdate.text.toString()
            val height = etHeight.text.toString()
            val weight = etWeight.text.toString()

            if (name.isBlank() || birthdate.isBlank() || height.isBlank() || weight.isBlank()) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "저장 완료:\n이름: $name\n생년월일: $birthdate\n키: $height cm\n몸무게: $weight kg",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // 상세정보 버튼 클릭 이벤트
        btnDetails.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }
    }
}
