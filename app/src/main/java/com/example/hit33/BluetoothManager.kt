package com.example.hit33

import DataCalculator
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothManager(private val context: Context, private val dataReceiver: DataReceiver) {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    private val deviceAddress = "2c:cf:67:97:c8:4b" // 블루투스 MAC 주소

    companion object {
        private const val MY_UUID = "00001101-0000-1000-8000-00805F9B34FB" // SPP UUID
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

        // MAC 주소가 올바르게 포맷되어 있는지 확인하는 정규 표현식
        val macAddressRegex = Regex("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})")
        if (!macAddressRegex.matches(deviceAddress)) {
            Toast.makeText(context, "잘못된 MAC 주소 형식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(deviceAddress)

        if (device == null) {
            // 장치가 존재하지 않을 때 처리
            Toast.makeText(context, "장치를 찾을 수 없습니다: $deviceAddress", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID))
            bluetoothSocket.connect()
            initializeStreams()
            receiveData()

            Toast.makeText(context, "Bluetooth 연결 성공!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Bluetooth 연결 실패: ${e.message}", Toast.LENGTH_SHORT).show()
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

    // 입력 및 출력 스트림 초기화
    private fun initializeStreams() {
        inputStream = bluetoothSocket.inputStream
        outputStream = bluetoothSocket.outputStream
    }

    // 데이터 수신 메소드
    private fun receiveData() {
        Thread {
            while (true) {
                try {
                    val buffer = ByteArray(1024)
                    val bytes = inputStream.read(buffer)

                    val receivedData = String(buffer, 0, bytes).trim()
                    val dataParts = receivedData.split(",") // 자이로와 가속도 데이터가 구분되어 있다고 가정.

                    // 데이터 가공 부분
                    if (dataParts.size >= 6) { // 자이로 및 가속도 데이터가 적어도 6개 있어야 한다고 가정.
                        val calculator = DataCalculator(context)
                        val results = calculator.calculate(dataParts) // 계산 후 결과 얻기

                        // 가공된 데이터 전달
                        dataReceiver.onDataReceived(results) // EquipmentDetailActivity에 결과 전달
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // 예외 발생 시 로그 출력
                    break
                }
            }
        }.start()
    }



    // Bluetooth 연결 종료 메소드
    fun closeConnection() {
        try {
            inputStream.close()
            outputStream.close()
            bluetoothSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendData(data: List<String>) {
        try {
            val message = data.joinToString(",") // 데이터를 콤마로 구분하여 문자열로 변환
            outputStream.write(message.toByteArray()) // Bluetooth로 데이터 전송
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "데이터 전송 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 데이터 수신 인터페이스
    interface DataReceiver {
        fun onDataReceived(data: List<String>)
    }
}
