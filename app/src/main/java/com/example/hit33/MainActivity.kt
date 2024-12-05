package com.example.hit33

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnHealthCare).setOnClickListener { openHealthCare() }
//        findViewById<Button>(R.id.btnProfile).setOnClickListener { openProfile() }
//        findViewById<Button>(R.id.btnDiet).setOnClickListener { openDiet() }
//        findViewById<Button>(R.id.btnPlanner).setOnClickListener { openPlanner() }
//        findViewById<Button>(R.id.btnEvent).setOnClickListener { openEvent() }
//        findViewById<Button>(R.id.btnRecord).setOnClickListener { openRecord() }
    }

    private fun openHealthCare() {
        val intent = Intent(this, HealthCareActivity::class.java)
        startActivity(intent)
    }

//    private fun openProfile() {
//        val intent = Intent(this, ProfileActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun openDiet() {
//        val intent = Intent(this, DietActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun openPlanner() {
//        val intent = Intent(this, PlannerActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun openEvent() {
//        val intent = Intent(this, EventActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun openRecord() {
//        val intent = Intent(this, RecordActivity::class.java)
//        startActivity(intent)
//    }
}
