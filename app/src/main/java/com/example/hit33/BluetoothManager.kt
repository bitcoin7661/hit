package com.example.hit33

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.util.UUID

class BluetoothManager(private val context: Context, private val dataReceiver: DataReceiver) {

    interface DataReceiver {
        fun onDataReceived(data: List<String>)
    }

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null

    private val deviceAddress = "2c:cf:67:97:c8:4b" // 블루투스 MAC 주소

    companion object {
        private val MY_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb") // GATT UUID 예시
        private const val REQUEST_CODE = 1 // 권한 요청 코드
    }

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    // Bluetooth 장치에 연결하는 함수
    @SuppressLint("MissingPermission")
    fun connectToDevice() {
        requestBluetoothPermission() // 권한 요청
    }

    // 권한 요청 메소드
    private fun requestBluetoothPermission() {
        if (!hasBluetoothPermission()) {
            // 사용자에게 권한 설명
            Toast.makeText(context, "이 앱은 Bluetooth 기능을 사용하기 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show()

            // 권한 요청
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_CODE
            )
        } else {
            // 권한이 승인된 경우 연결 시도
            initiateBluetoothConnection()
        }
    }

    // 실제 Bluetooth 연결 시도
    @SuppressLint("MissingPermission")
    private fun initiateBluetoothConnection() {
        if (!isBluetoothEnabled()) {
            Toast.makeText(context, "Bluetooth가 활성화되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 연결 전에 권한이 있는지 다시 한 번 확인합니다.
        if (!hasBluetoothPermission()) {
            Toast.makeText(context, "Bluetooth 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (device == null) {
            Toast.makeText(context, "장치를 찾을 수 없습니다: $deviceAddress", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(context, "Bluetooth 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Bluetooth 연결 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    // GATT Callback
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Toast.makeText(context, "연결 성공", Toast.LENGTH_SHORT).show()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            // 서비스 발견 시 로직 (필요 시 추가 구현)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            // 특성 읽기 시 로직 (필요 시 추가 구현)
        }
    }

    // Bluetooth 권한 확인
    private fun hasBluetoothPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    // Bluetooth 활성화 확인
    private fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    // Bluetooth 연결 종료 메소드
    @SuppressLint("MissingPermission")
    fun closeConnection() {
        try {
            bluetoothGatt?.close()
            bluetoothGatt = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
