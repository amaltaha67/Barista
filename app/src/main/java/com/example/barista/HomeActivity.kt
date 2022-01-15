package com.example.barista

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_home.*

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_RECIPE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getRecipesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }

    private fun getRecipesListFromLocalDB() {

        val dbHandler = DatabaseHelper(this)

        val getRecipesList = dbHandler.getRecipesList()

        if (getRecipesList.size > 0) {
            setupRecipeRecyclerView(getRecipesList)
            recipes_list.visibility = View.VISIBLE
            empty_list.visibility = View.GONE
        } else {
            recipes_list.visibility = View.GONE
            empty_list.visibility = View.VISIBLE
        }
    }

    private fun setupRecipeRecyclerView(recipesList: ArrayList<Recipe>) {

        recipes_list.layoutManager = LinearLayoutManager(this)
        recipes_list.setHasFixedSize(true)

        val placesAdapter = RecipeAdapter(this, recipesList)
        recipes_list.adapter = placesAdapter

        placesAdapter.setOnClickListener(object :
            RecipeAdapter.OnClickListener {
            override fun onClick(position: Int, model: Recipe) {
                val intent = Intent(this@HomeActivity, RecipeInformationActivity::class.java)
                intent.putExtra(EXTRA_RECIPE_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recipes_list.adapter as RecipeAdapter
                adapter.notifyEditItem(
                    this@HomeActivity,
                    viewHolder.adapterPosition,
                    ADD_RECIPE_ACTIVITY_REQUEST_CODE
                )
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(recipes_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recipes_list.adapter as RecipeAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getRecipesListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(recipes_list)
    }

    companion object {
        private const val ADD_RECIPE_ACTIVITY_REQUEST_CODE = 1
        internal const val EXTRA_RECIPE_DETAILS = "extra_place_details"
    }
}