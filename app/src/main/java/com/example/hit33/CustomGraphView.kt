package com.example.hit33

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomGraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val dataPoints = mutableListOf<Float>() // 데이터 점들을 저장
    private val paintLine = Paint().apply {
        color = Color.BLUE // 선 색상
        strokeWidth = 8f   // 선 두께
        isAntiAlias = true // 안티앨리어싱 활성화
    }
    private val paintCircle = Paint().apply {
        color = Color.RED  // 원 색상
        style = Paint.Style.FILL // 원을 채우는 스타일
    }

    // 데이터 설정
    fun setData(points: List<Float>) {
        dataPoints.clear()
        dataPoints.addAll(points)
        invalidate() // 뷰를 다시 그림
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return // 데이터가 없으면 그리지 않음

        // 폭을 나누어 데이터 간 간격 설정
        val widthStep = width / (dataPoints.size + 1).toFloat()
        var prevX = widthStep
        var prevY = height - dataPoints[0] / 100 * height

        for (i in 1 until dataPoints.size) {
            val currentX = widthStep * (i + 1)
            val currentY = height - dataPoints[i] / 100 * height

            // 선 그리기
            canvas.drawLine(prevX, prevY, currentX, currentY, paintLine)

            // 원 그리기
            canvas.drawCircle(currentX, currentY, 10f, paintCircle)

            // 이전 좌표 갱신
            prevX = currentX
            prevY = currentY
        }
    }
}
