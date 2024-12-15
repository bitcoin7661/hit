//package com.example.hit33
//
//import android.content.Context
//import android.media.AudioAttributes
//import android.media.SoundPool
//import com.example.hit33.R
//import kotlin.math.sqrt
//import com.example.hit33.DataCalculator
//
//
//class DataCalculator(private val context: Context) {
//
//    private var movementCount = 0 // 운동 횟수 계산
//    private var targetCount = 0 // 목표 횟수
//    private var totalDistance = 0.0
//    private var previousSpeed = 0.0
//    private var samplingTime = 0.5 // 샘플링 주기 (초 단위)
//    private var movementThreshold: Double = 50.0 // 기본 가동 범위 기준
//
//    private lateinit var soundPool: SoundPool
//    private var notificationSound: Int = 0
//    var onTargetReachedListener: OnTargetReachedListener? = null // 콜백 리스너
//
//
//    /**
//     * 블루투스 데이터로부터 자이로 값과 가속도 값을 계산합니다.
//     * @param data 블루투스에서 수신한 데이터 리스트
//     * @return 계산 결과를 포함한 문자열 리스트
//     */
//    fun calculate(data: List<String>): Map<String, String>  {
//        if (data.size < 6) {
//            throw IllegalArgumentException("데이터 항목이 부족합니다. 현재 수신된 항목 수: ${data.size}")
//        }
//
//        // 데이터 변환
//        val gyroX = data[0].toDoubleOrNull() ?: 0.0
//        val gyroY = data[1].toDoubleOrNull() ?: 0.0
//        val gyroZ = data[2].toDoubleOrNull() ?: 0.0
//        val accelX = data[3].toDoubleOrNull() ?: 0.0
//        val accelY = data[4].toDoubleOrNull() ?: 0.0
//        val accelZ = data[5].toDoubleOrNull() ?: 0.0
//
//        // 이동 거리 및 속도 계산
//        val distance = calculateDistance(accelX, accelY, accelZ)
//
//        // 포맷팅
//        val formattedDistance = String.format("%.2f", distance)
//
////        // 운동 횟수 판별
////        updateMovementCount(distance)
//
//        // 결과 반환
//        return mapOf(
//            "distance" to "$formattedDistance cm",
//            "reps" to "$movementCount"
//        )
//    }
//
//    // Bluetooth 데이터에서 샘플링 간격 추출 동적으로 추출하는 함수입니다
//    fun updateSamplingTime(newSamplingTime: Double) {
//        samplingTime = newSamplingTime
//    }
//
//    // 가동 범위 동적 설정 메서드
//    fun setMovementThreshold(threshold: Double) {
//        movementThreshold = threshold
//    }
//
//
//
//    // 거리 계산
//    private fun calculateDistance(accelX: Double, accelY: Double, accelZ: Double): Double {
//        val acceleration = sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)
//        val distance = 0.5 * acceleration * samplingTime * samplingTime // s = 0.5 * a * t^2
//        totalDistance += distance // 총 이동 거리 누적
//        return totalDistance
//    }
//
//
//    // 운동 횟수 업데이트
//     fun updateMovementCount(currentDistance: Double) {
//        // 측정된 거리 기준으로 횟수 판별
//        if (currentDistance >= movementThreshold) { // minRange, maxRange는 가동 범위
//            movementCount++ // 운동 횟수 증가
//        }
//
//        if (movementCount >= targetCount) {
//            movementCount = 0 // 운동 횟수 초기화
//            playNotification(2) // 목표 달성을 알리는 알림
//            // 세트 수 증가 콜백 호출
//            onTargetReachedListener?.onTargetReached()
//        }
//    }
//
//
//
//    // 알림 재생 메소드
//    private fun playNotification(count: Int) {
//        for (i in 0 until count) {
//            soundPool.play(notificationSound, 1f, 1f, 0, 0, 1f) // 알림음 재생
//        }
//    }
//
//    // 목표 횟수 설정 메소드
//    fun setTargetCount(targetCount: Int) {
//        // 필요한 경우 목표 횟수 설정 로직 추가
//    }
//}
//
//interface OnTargetReachedListener {
//    fun onTargetReached() // 목표 횟수 도달 시 호출
//}
//
