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
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "CoffeeRecipesDatabase" // Database name
        private const val TABLE_RECIPE = "Recipes" // Table Name

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_RECIPE_TABLE = ("CREATE TABLE " + TABLE_RECIPE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,")
        db?.execSQL(CREATE_RECIPE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE")
        onCreate(db)
    }

    /**
     * Function to insert a Happy Place details to SQLite Database.
     */
    fun addRecipe(recipe: Recipe): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, recipe.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, recipe.image) // HappyPlaceModelClass IMAGE
        contentValues.put(
            KEY_DESCRIPTION,
            recipe.description
        )
        contentValues.put(KEY_DATE, recipe.date) // HappyPlaceModelClass DATE

        // Inserting Row
        val result = db.insert(TABLE_RECIPE, null, contentValues)


        db.close() // Closing database connection
        return result
    }

}