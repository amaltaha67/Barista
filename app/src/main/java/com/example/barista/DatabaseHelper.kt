package com.example.barista

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "CoffeeRecipesDatabase"
        private const val TABLE_RECIPE = "Recipe"

        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_RECIPE_TABLE = ("CREATE TABLE " + TABLE_RECIPE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT)")
        db?.execSQL(CREATE_RECIPE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE")
        onCreate(db)
    }

    // Function to insert a Recipe to SQLite Database.

    fun addRecipe(recipe: Recipe): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, recipe.title)
        contentValues.put(KEY_IMAGE, recipe.image)
        contentValues.put(
            KEY_DESCRIPTION,
            recipe.description
        )
        contentValues.put(KEY_DATE, recipe.date)
        val result = db.insert(TABLE_RECIPE, null, contentValues)

        db.close()
        return result
    }
    @SuppressLint("Range")
    fun getRecipesList(): ArrayList<Recipe> {

        val coffeeRecipesList: ArrayList<Recipe> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_RECIPE" // Database select query
        val db = this.readableDatabase
        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = Recipe(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE))
                    )
                    coffeeRecipesList.add(place)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return coffeeRecipesList
    }

     //Function to update a recipe

    fun updateRecipe(happyPlace: Recipe): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(
            KEY_DESCRIPTION,
            happyPlace.description
        )
        contentValues.put(KEY_DATE, happyPlace.date)
        val success =
            db.update(TABLE_RECIPE, contentValues, KEY_ID + "=" + happyPlace.id, null)
        db.close()
        return success
    }

     // Function to delete a recipe.

    fun deleteRecipe(happyPlace: Recipe): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_RECIPE, KEY_ID + "=" + happyPlace.id, null)
        db.close()
        return success
    }
}