package com.example.hit33.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

val CTF_SERVICE_UUID: UUID = UUID.fromString("8c380000-10bd-4fdb-ba21-1922d6cf860d")
val PASSWORD_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380001-10bd-4fdb-ba21-1922d6cf860d")
val NAME_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380002-10bd-4fdb-ba21-1922d6cf860d")

// Nordic UART Service (NUS)
val NORDIC_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
val NORDIC_RX_CHAR_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")  // Write
val NORDIC_TX_CHAR_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")  // Read/Notify

@Suppress("DEPRECATION")
class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice
) {
    val isConnected = MutableStateFlow(false)
    val passwordRead = MutableStateFlow<String?>(null)
    val successfulNameWrites = MutableStateFlow(0)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())
    // 수신된 데이터를 저장할 StateFlow 추가
    val receivedData = MutableStateFlow<String?>(null)

    private val callback = object: BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val connected = newState == BluetoothGatt.STATE_CONNECTED
            if (connected) {
                //read the list of services
                services.value = gatt.services
            }
            isConnected.value = connected
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            services.value = gatt.services
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (characteristic.uuid == NORDIC_TX_CHAR_UUID) {  // original: PASSWORD_CHARACTERISTIC_UUID
                passwordRead.value = String(characteristic.value)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (characteristic.uuid == NORDIC_RX_CHAR_UUID) {  // original: NAME_CHARACTERISTIC_UUID
                successfulNameWrites.update { it + 1 }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == NORDIC_TX_CHAR_UUID) {
                // 데이터 수신 처리
                val received = String(value)
                // StateFlow를 통해 수신된 데이터 업데이트
                receivedData.value = received
            }
        }
    }

    private var gatt: BluetoothGatt? = null

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connect() {
        gatt = bluetoothDevice.connectGatt(context, false, callback)
        enableNotifications()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverServices() {
        gatt?.discoverServices()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun readPassword() {
        val service = gatt?.getService(NORDIC_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(NORDIC_TX_CHAR_UUID)  // original: pw char
        if (characteristic != null) {
            val success = gatt?.readCharacteristic(characteristic)
            Log.d("bluetooth", "Read status: $success")
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeName() {
        val service = gatt?.getService(NORDIC_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(NORDIC_RX_CHAR_UUID)
        if (characteristic != null) {
            characteristic.value = "fox".toByteArray()
            val success = gatt?.writeCharacteristic(characteristic)
            Log.d("bluetooth", "Write status: $success")
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeData(data: ByteArray) {
        val service = gatt?.getService(NORDIC_SERVICE_UUID)
        val rxCharacteristic = service?.getCharacteristic(NORDIC_RX_CHAR_UUID)

        if (rxCharacteristic != null) {
            // Nordic nRF는 Write without response를 사용
            rxCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            rxCharacteristic.value = data
            gatt?.writeCharacteristic(rxCharacteristic)
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun enableNotifications() {
        val service = gatt?.getService(NORDIC_SERVICE_UUID)
        val txCharacteristic = service?.getCharacteristic(NORDIC_TX_CHAR_UUID)

        if (txCharacteristic != null) {
            // Notify 활성화
            gatt?.setCharacteristicNotification(txCharacteristic, true)

            // Descriptor 설정
            val descriptor = txCharacteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")  // Client Characteristic Configuration
            )
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt?.writeDescriptor(descriptor)
        }
    }
}