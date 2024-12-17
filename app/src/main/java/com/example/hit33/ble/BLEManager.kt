// BLEManager.kt
package com.example.hit33.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("MissingPermission")
class BLEManager(private val context: Context) {
    private var bluetoothGatt: BluetoothGatt? = null
    private var rxCharacteristic: BluetoothGattCharacteristic? = null  // Write characteristic
    private var txCharacteristic: BluetoothGattCharacteristic? = null  // Notify characteristic

    // 연결 상태를 관찰하기 위한 StateFlow
    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()

    // 수신된 데이터를 관찰하기 위한 StateFlow
    private val _receivedData = MutableStateFlow<String>("")
    val receivedData: StateFlow<String> = _receivedData.asStateFlow()

    companion object {
        private const val TAG = "BLEManager"
        // Nordic UART Service (NUS)
        private val SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        // Nordic UART Characteristic
        private val CHARACTERISTIC_UUID_RX = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E") // Write
        private val CHARACTERISTIC_UUID_TX = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E") // Read/Notify

        @Volatile
        private var instance: BLEManager? = null

        fun getInstance(context: Context): BLEManager {
            return instance ?: synchronized(this) {
                instance ?: BLEManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server.")
                    _connectionState.value = true
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server.")
                    _connectionState.value = false
                    closeConnection()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt?.getService(SERVICE_UUID)?.let { service ->
                    // RX characteristic for writing data
                    rxCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID_RX)
                    // TX characteristic for receiving data
                    txCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID_TX)
                    // Enable notifications for TX characteristic
                    enableNotifications()
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            // 데이터 수신 시
            val data = String(value)
            Log.d(TAG, "Received data: $data")
            _receivedData.value = data
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Write successful")
            } else {
                Log.e(TAG, "Write failed")
            }
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun writeData(data: String) {
        rxCharacteristic?.let { characteristic ->
            characteristic.value = data.toByteArray()
            bluetoothGatt?.writeCharacteristic(characteristic)
        }
    }

    private fun enableNotifications() {
        txCharacteristic?.let { characteristic ->
            bluetoothGatt?.setCharacteristicNotification(characteristic, true)
            characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))?.let { descriptor ->
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                bluetoothGatt?.writeDescriptor(descriptor)
            }
        }
    }

    fun closeConnection() {
        bluetoothGatt?.let { gatt ->
            gatt.disconnect()
            gatt.close()
        }
        bluetoothGatt = null
        rxCharacteristic = null
        txCharacteristic = null
    }
}