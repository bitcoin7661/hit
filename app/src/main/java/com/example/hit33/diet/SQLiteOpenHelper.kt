package com.example.hit33

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MealPlanner.db"
        private const val DATABASE_VERSION = 2 // 버전을 1에서 2로 변경
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Food (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                calories INTEGER NOT NULL,
                carbs REAL NOT NULL,
                protein REAL NOT NULL,
                fat REAL NOT NULL
            )
            """
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS DailyMeal (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                mealType TEXT NOT NULL,
                foodNames TEXT NOT NULL,
                nutrients TEXT NOT NULL
            )
            """
        )

        insertInitialFoods(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 기존 데이터베이스 삭제 및 새로 생성
        db.execSQL("DROP TABLE IF EXISTS Food")
        db.execSQL("DROP TABLE IF EXISTS DailyMeal")
        onCreate(db)
    }

    private fun insertInitialFoods(db: SQLiteDatabase) {
        // 새로운 음식 데이터 추가
        val foods = listOf(
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('닭가슴살', 165, 0.0, 31.0, 3.6)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('현미밥', 110, 23.0, 2.0, 0.5)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('고구마', 130, 31.0, 2.0, 0.1)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('삶은 달걀', 68, 1.0, 6.0, 5.0)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('오트밀', 150, 27.0, 5.0, 2.5)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('연어', 200, 0.0, 22.0, 13.0)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('아몬드', 160, 6.0, 6.0, 14.0)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('스테이크', 250, 0.0, 26.0, 17.0)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('브로콜리', 55, 11.0, 4.0, 0.3)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('아보카도', 240, 12.0, 3.0, 22.0)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('닭가슴살 샐러드', 200, 5.0, 25.0, 7.0)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('단호박', 80, 20.0, 2.0, 0.2)",
            "INSERT INTO Food (name, calories, carbs, protein, fat) VALUES ('청포도', 60, 15.0, 0.6, 0.2)"
        )

        for (food in foods) {
            db.execSQL(food)
        }
    }
}



