package com.example.barista

import android.content.Intent
import android.os.Bundle
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
    }
}