// HealthCareActivity.kt
package com.example.hit33

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hit33.ble.BluetoothLEManager
import com.google.android.material.card.MaterialCardView

class HealthCareActivity : AppCompatActivity(), BluetoothLEManager.DataReceiver {

    private lateinit var bluetoothLEManager: BluetoothLEManager
    private lateinit var tvStatus: TextView
    private val scannedDevices = mutableListOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_care)

        tvStatus = findViewById(R.id.tvStatus)
        bluetoothLEManager = BluetoothLEManager(this, this)

        // MaterialCardView 버튼들 설정
        findViewById<MaterialCardView>(R.id.btnShoulder).setOnClickListener { openInfo("어깨") }
        findViewById<MaterialCardView>(R.id.btnBack).setOnClickListener { openInfo("등") }
        findViewById<MaterialCardView>(R.id.btnChest).setOnClickListener { openInfo("가슴") }
        findViewById<MaterialCardView>(R.id.btnlowerbody).setOnClickListener { openInfo("하체") }
        findViewById<MaterialCardView>(R.id.btnArms).setOnClickListener { openInfo("팔") }

        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            startBleScan()
        }
    }

    private fun startBleScan() {
        scannedDevices.clear()
        Toast.makeText(this, "스캔 시작", Toast.LENGTH_SHORT).show()
        tvStatus.text = "BLE 장치 스캔 중..."
        bluetoothLEManager.startScan()
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

    @SuppressLint("MissingPermission")
    private fun showDeviceSelectionDialog() {
        Toast.makeText(this, "다이얼로그 표시 시도", Toast.LENGTH_SHORT).show()
        if (scannedDevices.isEmpty()) {
            Toast.makeText(this, "발견된 BLE 장치가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // Set을 List로 변환하여 다이얼로그에 표시
        val devicesList = scannedDevices.toList()
        val deviceNames = scannedDevices.map { device ->
            device.address
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("BLE 장치 선택")
            .setItems(deviceNames) { _, which ->
                val selectedDevice = devicesList[which]
                tvStatus.text = "연결 중..."
                bluetoothLEManager.connectToDevice(selectedDevice)
            }
            .setNegativeButton("취소") { _, _ ->
                bluetoothLEManager.stopScan()
            }
            .setOnDismissListener {
                bluetoothLEManager.stopScan()
            }
            .show()
    }

    override fun onDataReceived(data: List<String>) {
        runOnUiThread {
            tvStatus.text = "수신된 데이터: ${data.joinToString()}"
        }
    }

    override fun onDeviceFound(device: BluetoothDevice) {
        runOnUiThread {
            Toast.makeText(this, "장치 발견: ${device.address}", Toast.LENGTH_SHORT).show()
            scannedDevices.add(device) // 새로운 장치 추가
            showDeviceSelectionDialog() // 장치 목록 다이얼로그 표시
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startBleScan()
        } else {
            Toast.makeText(this, "BLE 사용을 위해서는 모든 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothLEManager.closeConnection()
    }
}