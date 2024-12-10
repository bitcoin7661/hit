package com.example.hit33

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView


class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 권한 체크 및 요청
        if (!hasPermissions()) {
            requestPermissions()
        }

        findViewById<MaterialCardView>(R.id.cardHealthCare).setOnClickListener{ openHealthCare() }
//        findViewById<MaterialCardView>(R.id.cardProfile).setOnClickListener
//        findViewById<MaterialCardView>(R.id.cardDiet).setOnClickListener
//        findViewById<MaterialCardView>(R.id.cardPlanner).setOnClickListener
//        findViewById<MaterialCardView>(R.id.cardEvent).setOnClickListener

    }

    private fun hasPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
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
