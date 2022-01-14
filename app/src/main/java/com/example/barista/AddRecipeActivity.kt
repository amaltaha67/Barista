package com.example.barista

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_recipe.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddRecipeActivity : AppCompatActivity(), View.OnClickListener {
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "CoffeeRecipeImages"
    }
    private var coffeeRecipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)
        val toolbar : Toolbar = findViewById(R.id.toolbar_add_coffee_recipe)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()

        }
        if (intent.hasExtra(HomeActivity.EXTRA_RECIPE_DETAILS)) {
            coffeeRecipe =
                intent.getSerializableExtra(HomeActivity.EXTRA_RECIPE_DETAILS) as Recipe
        }
        if (coffeeRecipe != null) {
            supportActionBar?.title = "Edit Happy Place"

            et_title.setText(coffeeRecipe!!.title)
            et_description.setText(coffeeRecipe!!.description)
            et_date.setText(coffeeRecipe!!.date)

            saveImageToInternalStorage = Uri.parse(coffeeRecipe!!.image)

            iv_place_image.setImageURI(saveImageToInternalStorage)

            btn_save.text = "UPDATE"
        }

        val etDate : EditText = findViewById(R.id.et_date)
        etDate.setOnClickListener(this)

        val addImage : TextView = findViewById(R.id.tv_add_image)
        addImage.setOnClickListener(this)

        val saveInput : Button = findViewById(R.id.btn_save)
        saveInput.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        val titleED : EditText = findViewById(R.id.et_title)
        val descriptionED : EditText = findViewById(R.id.et_description)
        val dateEd : EditText = findViewById(R.id.et_date)
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(
                    pictureDialogItems
                ) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btn_save -> {

                when {
                    titleED.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    descriptionED.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT)
                            .show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                        val happyPlaceModel = Recipe(
                            if (coffeeRecipe == null) 0 else coffeeRecipe!!.id,
                            titleED.text.toString(),
                            saveImageToInternalStorage.toString(),
                            descriptionED.text.toString(),
                            dateEd.text.toString()
                        )

                        val dbHandler = DatabaseHelper(this)

                        if (coffeeRecipe == null) {
                            val addHappyPlace = dbHandler.addRecipe(happyPlaceModel)

                            if (addHappyPlace > 0) {
                                setResult(Activity.RESULT_OK);
                                finish()//finishing activity
                            }
                        } else {
                            val updateHappyPlace = dbHandler.updateRecipe(happyPlaceModel)

                            if (updateHappyPlace > 0) {
                                setResult(Activity.RESULT_OK);
                                finish()//finishing activity
                            }
                        }

                    }
                }
            }
        }

    }
    private fun updateDateInView(){
        val myFormat = "dd.mm.yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        val etDate : EditText = findViewById(R.id.et_date)
        etDate.setText(simpleDateFormat.format(cal.time).toString())
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityIfNeeded(galleryIntent, GALLERY)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    /**
     * A method is used  asking the permission for camera and storage and image capturing and selection from Camera.
     */
    private fun takePhotoFromCamera() {

        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityIfNeeded(intent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {

            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()

            stream.close()

        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val placeImage : ImageView = findViewById(R.id.iv_place_image)
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                         saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                        // Set the selected image from GALLERY to imageView.
                        placeImage.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddRecipeActivity, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap

                 saveImageToInternalStorage =
                    saveImageToInternalStorage(thumbnail)
                Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                placeImage.setImageBitmap(thumbnail)
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }


}