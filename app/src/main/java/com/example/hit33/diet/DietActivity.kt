package com.example.hit33

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DietActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var mealDAO: MealDAO
    private var currentDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_diet)

        dbHelper = DatabaseHelper(this)
        mealDAO = MealDAO(dbHelper.writableDatabase)

        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val btnPreviousDay = findViewById<ImageButton>(R.id.btnPreviousDay)
        val btnNextDay = findViewById<ImageButton>(R.id.btnNextDay)

        // 날짜 업데이트
        updateDateTextView(dateTextView)
        updateOrGenerateMeals()
        updateDailyMeals()

        // 이전 날짜 버튼 클릭
        btnPreviousDay.setOnClickListener {
            changeDate(-1)
            updateDateTextView(dateTextView)
            updateOrGenerateMeals()
            updateDailyMeals()
        }

        // 다음 날짜 버튼 클릭
        btnNextDay.setOnClickListener {
            changeDate(1)
            updateDateTextView(dateTextView)
            updateOrGenerateMeals()
            updateDailyMeals()
        }
    }

    private fun changeDate(dayOffset: Int) {
        currentDate.add(Calendar.DAY_OF_MONTH, dayOffset)
        limitToCurrentMonth()
    }

    private fun limitToCurrentMonth() {
        val calendar = Calendar.getInstance()

        // 해당 월의 시작일로 이동
        calendar.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1)
        val startOfMonth = calendar.time

        // 해당 월의 마지막 날로 이동
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endOfMonth = calendar.time

        // 현재 날짜가 범위를 벗어나면 조정
        if (currentDate.time.before(startOfMonth)) currentDate.time = startOfMonth
        if (currentDate.time.after(endOfMonth)) currentDate.time = endOfMonth
    }

    private fun updateDateTextView(dateTextView: TextView) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateTextView.text = sdf.format(currentDate.time)
    }

    private fun updateOrGenerateMeals() {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate.time)

        // 1. Food 데이터 가져오기
        val foodCursor = dbHelper.readableDatabase.rawQuery("SELECT * FROM Food", null)
        val foodList = mutableListOf<Food>()

        while (foodCursor.moveToNext()) {
            foodList.add(
                Food(
                    foodCursor.getString(foodCursor.getColumnIndexOrThrow("name")),
                    foodCursor.getInt(foodCursor.getColumnIndexOrThrow("calories")),
                    foodCursor.getDouble(foodCursor.getColumnIndexOrThrow("carbs")),
                    foodCursor.getDouble(foodCursor.getColumnIndexOrThrow("protein")),
                    foodCursor.getDouble(foodCursor.getColumnIndexOrThrow("fat"))
                )
            )
        }
        foodCursor.close()

        // 조식, 중식, 석식 생성 및 저장
        listOf("아침", "점심", "저녁").forEach { mealType ->
            if (mealDAO.getDailyMeal(formattedDate, mealType) == null) {
                val meal = mealDAO.generateRandomMeal(foodList)
                mealDAO.saveDailyMeal(formattedDate, mealType, meal.first, meal.second)
            }
        }
    }

    private fun updateDailyMeals() {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate.time)

        val breakfast = mealDAO.getDailyMeal(formattedDate, "아침")
        findViewById<TextView>(R.id.breakfastTextView).text = breakfast?.first ?: "데이터 없음"
        findViewById<TextView>(R.id.breakfastNutrientsTextView).text = breakfast?.second ?: ""

        val lunch = mealDAO.getDailyMeal(formattedDate, "점심")
        findViewById<TextView>(R.id.lunchTextView).text = lunch?.first ?: "데이터 없음"
        findViewById<TextView>(R.id.lunchNutrientsTextView).text = lunch?.second ?: ""

        val dinner = mealDAO.getDailyMeal(formattedDate, "저녁")
        findViewById<TextView>(R.id.dinnerTextView).text = dinner?.first ?: "데이터 없음"
        findViewById<TextView>(R.id.dinnerNutrientsTextView).text = dinner?.second ?: ""
    }
}
