import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.hit33.R

class DataCalculator(private val context: Context) {

    private var movementCount = 0 // 운동 횟수 계산
    private lateinit var soundPool: SoundPool
    private var notificationSound: Int = 0

    init {
        // SoundPool 초기화
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // 사운드 파일 로드
        notificationSound = soundPool.load(context, R.raw.notification_sound, 1) // 'notification_sound'는 실제 파일 이름
    }

    /**
     * 블루투스 데이터로부터 자이로 값과 가속도 값을 계산합니다.
     * @param data 블루투스에서 수신한 데이터 리스트
     * @return 계산 결과를 포함한 문자열 리스트
     */
    fun calculate(data: List<String>): List<String> {
        if (data.size < 6) {
            throw IllegalArgumentException("데이터 항목이 부족합니다.")
        }

        // 데이터 변환
        val gyroX = data[0].toDoubleOrNull() ?: 0.0
        val gyroY = data[1].toDoubleOrNull() ?: 0.0
        val gyroZ = data[2].toDoubleOrNull() ?: 0.0
        val accelX = data[3].toDoubleOrNull() ?: 0.0
        val accelY = data[4].toDoubleOrNull() ?: 0.0
        val accelZ = data[5].toDoubleOrNull() ?: 0.0

        // 이동 거리 및 속도 계산
        val distance = calculateDistance(accelX, accelY, accelZ)
        val speed = calculateSpeed(accelX, accelY, accelZ)

        // 운동 횟수 판별
        updateMovementCount(distance)

        // 결과 반환
        return listOf(
            "이동 거리: $distance cm",
            "이동 속도: $speed m/s",
            "운동 횟수: $movementCount"
        )
    }

    // 이동 거리 계산
    private fun calculateDistance(accelX: Double, accelY: Double, accelZ: Double): Double {
        // 가속도를 거리로 변환 (여기서는 간단히 예를 들기 위해 적당한 계수를 사용)
        return Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ) // 예를 사용한 거리 단위
    }

    // 이동 속도 계산
    private fun calculateSpeed(accelX: Double, accelY: Double, accelZ: Double): Double {
        // 가속도를 속도로 변환 (적절한 시간 간격을 가정해야 함)
        return Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ) // 예를 사용한 속도 단위
    }

    // 운동 횟수 업데이트
    private fun updateMovementCount(currentDistance: Double) {
        if (currentDistance in 50.0..100.0) {
            movementCount++ // 운동 횟수 증가
            playNotification(1) // 알림 1회 발생
        }
    }

    // 알림 재생 메소드
    private fun playNotification(count: Int) {
        for (i in 0 until count) {
            soundPool.play(notificationSound, 1f, 1f, 0, 0, 1f) // 알림음 재생
        }
    }

    fun setTargetCount(targetCount: Int) {

    }
}
