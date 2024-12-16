package com.example.hit33

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class ExerciseTrendActivity : AppCompatActivity() {

    private lateinit var spinnerExerciseNames: Spinner
    private lateinit var btnViewTrend: Button
    private lateinit var customGraphView: CustomGraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_trend)

        // UI 요소 초기화
        spinnerExerciseNames = findViewById(R.id.spinnerExerciseNames)
        btnViewTrend = findViewById(R.id.btnViewTrend)
        customGraphView = findViewById(R.id.customGraphView)

        // 운동명 리스트 가져오기
        loadExerciseNames()

        // 추이 변화 버튼 클릭 이벤트
        btnViewTrend.setOnClickListener {
            val selectedExercise = spinnerExerciseNames.selectedItem.toString()
            val dataPoints = filterChangedRecords(selectedExercise)
            customGraphView.setData(dataPoints)
        }
    }

    private fun loadExerciseNames() {
        val sharedPreferences = getSharedPreferences("PlannerPrefs", Context.MODE_PRIVATE)
        val exerciseNames = sharedPreferences.all.values.filterIsInstance<String>().toSet()

        // Spinner에 운동명 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseNames.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExerciseNames.adapter = adapter
    }

    private fun filterChangedRecords(exerciseName: String): List<Float> {
        val sharedPreferences = getSharedPreferences("ExerciseRecords", Context.MODE_PRIVATE)
        val dataPoints = mutableListOf<Float>()

        var prevReps = -1
        var prevSets = -1

        sharedPreferences.all.forEach { (key, value) ->
            if (key.contains(exerciseName)) {
                val reps = sharedPreferences.getInt("${key}_reps", 0)
                val sets = sharedPreferences.getInt("${key}_sets", 0)

                if (reps != prevReps || sets != prevSets) {
                    dataPoints.add(reps.toFloat())
                    prevReps = reps
                    prevSets = sets
                }
            }
        }
        return dataPoints
    }
}
