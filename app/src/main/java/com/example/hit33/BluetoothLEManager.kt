package com.example.hit33

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.util.*
import android.content.Context

class BluetoothLEManager(
    private val activity: Activity,
    private val dataReceiver: DataReceiver
) {
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var isScanning = false
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val SCAN_PERIOD: Long = 10000 // 스캔 시간 10초
        private const val PERMISSIONS_REQUEST_CODE = 100

        // BLE 서비스 UUID 예시 (실제 사용할 UUID로 변경 필요)
        private val SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val CHARACTERISTIC_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    }

    // 권한 체크 메소드
    private fun checkPermissions(): Boolean {
        return getRequiredPermissions().all {
            ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 권한 요청 메소드
    private fun requestPermissions() {
        val permissionsToRequest = getRequiredPermissions().filter {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest,
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // Android 버전에 따른 필요 권한 목록 반환
    private fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    // GATT 콜백
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    showToast("BLE 장치에 연결되었습니다")
                    if (ActivityCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED) {
                        gatt.discoverServices()
                    } else {
                        showToast("Bluetooth 권한이 필요합니다")
                        requestPermissions()
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    showToast("BLE 장치와 연결이 끊어졌습니다")
                    bluetoothGatt?.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED) {
                    showToast("Bluetooth 권한이 필요합니다")
                    requestPermissions()
                    return
                }

                val service = gatt.getService(SERVICE_UUID)
                val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)

                if (characteristic != null) {
                    try {
                        // Notification 설정 시도
                        if (ActivityCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) == PackageManager.PERMISSION_GRANTED) {
                            gatt.setCharacteristicNotification(characteristic, true)

                            // Descriptor 설정
                            characteristic.descriptors?.forEach { descriptor ->
                                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                gatt.writeDescriptor(descriptor)
                            }
                        } else {
                            showToast("Bluetooth 권한이 필요합니다")
                            requestPermissions()
                        }
                    } catch (e: SecurityException) {
                        showToast("Bluetooth 권한이 없습니다")
                        requestPermissions()
                    }
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val data = characteristic.value
            val receivedData = String(data).trim()
            val dataParts = receivedData.split(",")

            handler.post {
                dataReceiver.onDataReceived(dataParts)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                showToast("데이터 전송 성공")
            } else {
                showToast("데이터 전송 실패")
            }
        }
    }

    // 스캔 콜백
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED) {
                val device = result.device
                if (device.address.startsWith("2C:CF:67")) {
                    dataReceiver.onDeviceFound(device)
                }
            } else {
                showToast("Bluetooth 권한이 필요합니다")
                requestPermissions()
            }
        }

        override fun onScanFailed(errorCode: Int) {
            showToast("BLE 스캔 실패: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        if (!isScanning && bluetoothAdapter.isEnabled) {
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

            handler.postDelayed({
                stopScan()
            }, SCAN_PERIOD)

            isScanning = true
            bluetoothLeScanner?.startScan(scanCallback)
            showToast("BLE 스캔을 시작합니다")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        if (isScanning) {
            isScanning = false
            bluetoothLeScanner?.stopScan(scanCallback)
            showToast("BLE 스캔을 중지합니다")
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        bluetoothGatt = device.connectGatt(activity, false, gattCallback)
    }

    fun closeConnection() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        bluetoothGatt?.let { gatt ->
            try {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED) {
                    gatt.disconnect()
                    gatt.close()
                } else {
                    showToast("Bluetooth 권한이 필요합니다")
                    requestPermissions()
                }
            } catch (e: SecurityException) {
                showToast("Bluetooth 권한이 없습니다")
                requestPermissions()
            }
        }
        bluetoothGatt = null
    }

    @SuppressLint("MissingPermission")
    fun sendData(data: List<String>) {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        val service = bluetoothGatt?.getService(SERVICE_UUID)
        val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)

        if (characteristic != null) {
            val message = data.joinToString(",")
            characteristic.value = message.toByteArray()
            bluetoothGatt?.writeCharacteristic(characteristic)
        } else {
            showToast("특성을 찾을 수 없습니다")
        }
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    // 데이터 수신 인터페이스
    interface DataReceiver {
        fun onDataReceived(data: List<String>)
        fun onDeviceFound(device: BluetoothDevice)
    }
}