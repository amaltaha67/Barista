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
    // Call Back method  to get the Message form other Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // check if the request code is same as what is passed  here it is 'ADD_PLACE_ACTIVITY_REQUEST_CODE'
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getRecipesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }

    /**
     * A function to get the list of happy place from local database.
     */
    private fun getRecipesListFromLocalDB() {

        val dbHandler = DatabaseHelper(this)

        val getHappyPlacesList = dbHandler.getRecipesList()

        if (getHappyPlacesList.size > 0) {
            recipes_list.visibility = View.VISIBLE
            empty_list.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        } else {
            recipes_list.visibility = View.GONE
            empty_list.visibility = View.VISIBLE
        }
    }

    /**
     * A function to populate the recyclerview to the UI.
     */
    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<Recipe>) {

        recipes_list.layoutManager = LinearLayoutManager(this)
        recipes_list.setHasFixedSize(true)

        val placesAdapter = RecipeAdapter(this, happyPlacesList)
        recipes_list.adapter = placesAdapter

        placesAdapter.setOnClickListener(object :
            RecipeAdapter.OnClickListener {
            override fun onClick(position: Int, model: Recipe) {
                val intent = Intent(this@HomeActivity, RecipeInformationActivity::class.java)
                intent.putExtra(EXTRA_RECIPE_DETAILS, model) // Passing the complete serializable data class to the detail activity using intent.
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recipes_list.adapter as RecipeAdapter
                adapter.notifyEditItem(
                    this@HomeActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(recipes_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recipes_list.adapter as RecipeAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getRecipesListFromLocalDB() // Gets the latest list from the local database after item being delete from it.
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(recipes_list)
    }

    companion object {
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        internal const val EXTRA_RECIPE_DETAILS = "extra_place_details"
    }
}