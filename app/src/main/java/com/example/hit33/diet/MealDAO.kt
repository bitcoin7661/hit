package com.example.hit33

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class MealDAO(private val db: SQLiteDatabase) {

    fun saveDailyMeal(date: String, mealType: String, foodNames: String, nutrients: String) {
        Log.d("saveDailyMeal", "Attempting to save meal: $mealType for $date with $foodNames and nutrients: $nutrients")

        val cursor = db.rawQuery(
            "SELECT id FROM DailyMeal WHERE date = ? AND mealType = ?",
            arrayOf(date, mealType)
        )

        if (cursor.moveToFirst()) {
            Log.d("saveDailyMeal", "Meal already exists for $date and $mealType. Skipping save.")
        } else {
            val values = ContentValues().apply {
                put("date", date)
                put("mealType", mealType)
                put("foodNames", foodNames)
                put("nutrients", nutrients)
            }
            db.insert("DailyMeal", null, values)
            Log.d("saveDailyMeal", "Saved meal: $mealType for $date.")
        }
        cursor.close()
    }

    fun getDailyMeal(date: String, mealType: String): Pair<String, String>? {
        Log.d("getDailyMeal", "Fetching meal: $mealType for $date")

        val cursor = db.rawQuery(
            "SELECT foodNames, nutrients FROM DailyMeal WHERE date = ? AND mealType = ?",
            arrayOf(date, mealType)
        )
        return if (cursor.moveToFirst()) {
            val foodNames = cursor.getString(cursor.getColumnIndexOrThrow("foodNames"))
            val nutrients = cursor.getString(cursor.getColumnIndexOrThrow("nutrients"))
            Log.d("getDailyMeal", "Fetched meal: $mealType for $date -> $foodNames, $nutrients")
            cursor.close()
            Pair(foodNames, nutrients)
        } else {
            Log.d("getDailyMeal", "No meal found: $mealType for $date")
            cursor.close()
            null
        }
    }

    fun getWeeklyMeals(startDate: String, endDate: String): List<Pair<String, List<Pair<String, String>>>> {
        Log.d("getWeeklyMeals", "Fetching meals between $startDate and $endDate")

        val cursor = db.rawQuery(
            """
            SELECT date, mealType, foodNames, nutrients 
            FROM DailyMeal 
            WHERE date BETWEEN ? AND ?
            ORDER BY date
            """,
            arrayOf(startDate, endDate)
        )

        val weeklyMeals = mutableListOf<Pair<String, List<Pair<String, String>>>>()

        var currentDay: String? = null
        val dayMeals = mutableListOf<Pair<String, String>>()

        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val mealType = cursor.getString(cursor.getColumnIndexOrThrow("mealType"))
            val foodNames = cursor.getString(cursor.getColumnIndexOrThrow("foodNames"))
            val nutrients = cursor.getString(cursor.getColumnIndexOrThrow("nutrients"))

            Log.d("getWeeklyMeals", "Found meal: $mealType on $date -> $foodNames, $nutrients")

            if (currentDay == null || currentDay != date) {
                if (currentDay != null) {
                    weeklyMeals.add(Pair(currentDay, dayMeals.toList()))
                    dayMeals.clear()
                }
                currentDay = date
            }
            dayMeals.add(Pair("$mealType: $foodNames", nutrients))
        }

        if (currentDay != null) {
            weeklyMeals.add(Pair(currentDay, dayMeals.toList()))
        }

        cursor.close()
        Log.d("getWeeklyMeals", "Weekly meals fetched: $weeklyMeals")
        return weeklyMeals
    }

    fun generateRandomMeal(foodList: List<Food>): Pair<String, String> {
        Log.d("generateRandomMeal", "Generating random meal from food list")

        val selectedFoods = foodList.shuffled().take((2..3).random())
        val foodNames = selectedFoods.joinToString { it.name }
        val totalCalories = selectedFoods.sumOf { it.calories }
        val totalCarbs = selectedFoods.sumOf { it.carbs }
        val totalProtein = selectedFoods.sumOf { it.protein }
        val totalFat = selectedFoods.sumOf { it.fat }

        val nutrients = "칼로리: $totalCalories kcal, 탄수화물: $totalCarbs g, 단백질: $totalProtein g, 지방: $totalFat g"
        Log.d("generateRandomMeal", "Generated meal -> Names: $foodNames, Nutrients: $nutrients")

        return Pair(foodNames, nutrients)
    }

    fun deleteDailyMeal(date: String, mealType: String) {
        Log.d("deleteDailyMeal", "Deleting meal: $mealType for $date")
        db.delete(
            "DailyMeal",
            "date = ? AND mealType = ?",
            arrayOf(date, mealType)
        )
    }
}
