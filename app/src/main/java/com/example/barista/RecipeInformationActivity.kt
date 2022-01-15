package com.example.barista

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recipes_info.*
import kotlinx.android.synthetic.main.activity_recipes_info.iv_place_image

class RecipeInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recipes_info)

        var recipeDetailModel: Recipe? = null

        if (intent.hasExtra(HomeActivity.EXTRA_RECIPE_DETAILS)) {
            recipeDetailModel =
                intent.getSerializableExtra(HomeActivity.EXTRA_RECIPE_DETAILS) as Recipe
        }

        if (recipeDetailModel != null) {

            setSupportActionBar(toolbar_happy_place_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = recipeDetailModel.title

            toolbar_happy_place_detail.setNavigationOnClickListener {
                onBackPressed()
            }

            iv_place_image.setImageURI(Uri.parse(recipeDetailModel.image))
            tv_description.text = recipeDetailModel.description
        }

    }
}