package com.example.hit33

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PointShopActivity : AppCompatActivity() {

    private var currentPoints = 0 // 포인트 초기화 값
    private lateinit var tvCurrentPoints: TextView
    private val sharedPreferences by lazy { getSharedPreferences("AttendancePrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_store_button)

        // SharedPreferences에서 포인트 값 가져오기
        currentPoints = sharedPreferences.getInt("points", 0)

        tvCurrentPoints = findViewById(R.id.tvCurrentPoints)
        updatePointsDisplay()

        // 구매 버튼 설정
        setupBuyButton(R.id.btnBuyEggs, 1000)
        setupBuyButton(R.id.btnBuySweetPotatoes, 1000)
        setupBuyButton(R.id.btnBuyChicken, 1000)
        setupBuyButton(R.id.btnBuyProteinShake, 2000)
        setupBuyButton(R.id.btnBuyStrap, 3000)
    }

    private fun setupBuyButton(buttonId: Int, price: Int) {
        findViewById<Button>(buttonId).setOnClickListener {
            if (currentPoints >= price) {
                currentPoints -= price

                // SharedPreferences 업데이트
                sharedPreferences.edit()
                    .putInt("points", currentPoints)
                    .apply()

                updatePointsDisplay()
                showToast("구매되었습니다!")
            } else {
                showToast("포인트가 부족합니다!")
            }
        }
    }

    private fun updatePointsDisplay() {
        tvCurrentPoints.text = "보유 포인트: $currentPoints"
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()

        // 3초 후 토스트 메시지 사라지게 하기
        Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 3000)
    }
}


