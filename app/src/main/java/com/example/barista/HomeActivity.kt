package com.example.barista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val fab : FloatingActionButton = findViewById(R.id.addCoffeeRecipe)
        fab.setOnClickListener{
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }
        getRecipesListFromLocalDB()
    }
    private fun getRecipesListFromLocalDB() {

        val dbHandler = DatabaseHelper(this)

        val getRecipeList : ArrayList<Recipe> = dbHandler.getRecipesList()

        if (getRecipeList.size > 0) {
            for (i in getRecipeList){
                Log.e("Title", i.title)
                Log.e("Description", i.description)
            }
        }else {

        }
    }
}