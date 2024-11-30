package com.blissvine.happyplaces.activities


import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.blissvine.happyplaces.R
import com.blissvine.happyplaces.database.DatabaseHandler
import com.blissvine.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener{
    @Suppress("DEPRECATION")
    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages" // folder on the phone where we are going to store image
    }

    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mHappyPlaceDetails: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        setSupportActionBar(toolbar_add_place)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar_add_place.setNavigationOnClickListener{
               onBackPressed()
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
             mHappyPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel?
        }

        //here we are opening the date picker dialog and we waiting for the user to select a date
        // thats why we are using the date-picker dialog with onDateSetListener
        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
              calendar.set(Calendar.YEAR, year)
              calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView() //automatically populate the date with the current date

        if(mHappyPlaceDetails != null){
               supportActionBar?.title = "Edit Happy Place"

            et_title.setText(mHappyPlaceDetails!!.title)
            et_description.setText(mHappyPlaceDetails!!.description)
            et_date.setText(mHappyPlaceDetails!!.date)
            et_location.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude

            saveImageToInternalStorage = Uri.parse(
                mHappyPlaceDetails!!.image
            )

            iv_place_image.setImageURI(saveImageToInternalStorage)
            btn_save.text  = "UPDATE"
        }

        et_date.setOnClickListener(this)
        tv_add_image.setOnClickListener(this)
        btn_save.setOnClickListener(this)
    }


    /*
    In this method we will take care of different types of onclick events
    The onClickListener is going to be the whole class, but this on click(member)
     is going to be where we implement the whole code for it.
     */
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from Gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()

            }

            R.id.btn_save -> {
                when {
                    et_title.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }

                    et_description.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
                    }

                    et_location.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
                    }

                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if (mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id,
                            et_title.text.toString(),
                            saveImageToInternalStorage.toString(),
                            et_description.text.toString(),
                            et_date.text.toString(),
                            et_location.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler = DatabaseHandler(this@AddHappyPlaceActivity)
                        if (mHappyPlaceDetails == null) {

                            val addHappyPlaceResult = dbHandler.addHappyPlace(happyPlaceModel)
                            if (addHappyPlaceResult > 0) { // if the result value is greater than 0 means there was no error and task is completed successfully
                                Toast.makeText(
                                    this,
                                    "The happy places details are inserted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }

                        } else {
                            val updateHappyPlace = dbHandler.updateHappyPLace(happyPlaceModel)
                            if (updateHappyPlace > 0) { // if the result value is greater than 0 means there was no error and task is completed successfully
                                Toast.makeText(
                                    this,
                                    "The happy places details are inserted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }

            }

        }
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
             if(requestCode == GALLERY){
                  if(data != null){ // means if we got the image (data)
                      val contentURI = data.data // storing the data we got
                      try {

                          // passing the data to the getBitmap() , basically here we are taking this data
                          //as an image in the form of a bitmap
                           val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                           saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                           Log.e("Saved Image: ", "Path:: $saveImageToInternalStorage")

                            iv_place_image.setImageBitmap(selectedImageBitmap)
                      }catch (e: IOException){
                           e.printStackTrace()
                           Toast.makeText(this@AddHappyPlaceActivity, "Failed to load the image from gallery", Toast.LENGTH_SHORT).show()
                      }

                  }
             }else if(requestCode == CAMERA){
                 /***
                  * here we are getting the data (image in this case) using the get method which requires the
                  * name of the data and in this case the name is data so we passed "data" and then converted it
                  * into a bitmap by casting the expression to Bitmap ***/

                 val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap

                 saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                 Log.e("Saved Image: ", "Path:: $saveImageToInternalStorage")

                 iv_place_image!!.setImageBitmap(thumbnail)
             }
        }
    }

    // function or method to handle camera permissions to capture photo
    private fun takePhotoFromCamera(){
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun  onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent, CAMERA)

                }
            }
            override fun  onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()

    }


 /*
   handling permissions for gallery
  */
    private fun choosePhotoFromGallery(){

       Dexter.withActivity(this).withPermissions(
           android.Manifest.permission.READ_EXTERNAL_STORAGE,
       android.Manifest.permission.WRITE_EXTERNAL_STORAGE
       ).withListener(object: MultiplePermissionsListener {
           override fun  onPermissionsChecked(report: MultiplePermissionsReport?) {
               if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                   startActivityForResult(galleryIntent, GALLERY)

               }
           }
           override fun  onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
               showRationalDialogForPermissions()
           }
       }).onSameThread().check()

    }

    private fun showRationalDialogForPermissions(){
         AlertDialog.Builder(this).setMessage("It looks like you have turned off permission for this feature. It can be" +
                 "enabled under Application Settings").setPositiveButton("GO TO SETTINGS"){
                     _, _, ->
             try {
                  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                  val uri  = Uri.fromParts("package", packageName, null)
                 intent.data = uri
                 startActivity(intent)
             }catch (e: ActivityNotFoundException){
                  e.printStackTrace()
             }
         }.setNegativeButton("Cancel"){
             dialog, _ ->
             dialog.dismiss()
         }.show()
    }

    private fun updateDateInView(){
        val myFormat =  "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        et_date.setText(sdf.format(calendar.time).toString())
    }

    // it should store bitmap and should return an URI (uri -> location of the image that we are storing)
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var fileDirectory = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)   //context wrapper is necessary to get the directory, and mode private allows us to make this file only accessible  from the calling the application
        fileDirectory = File(fileDirectory, "${UUID.randomUUID()}.jpg")  // above we got the directory and we can use this directory to create a file
      // randomUUID generates a random unique user id and it will store the image as jpg , this makes sure we make each image unique

        // Once we got the file we can store it using file output stream

        try {
            // we need to create a stream which will be our output stream, because we are trying to output an image to our phone
            val stream: OutputStream = FileOutputStream(fileDirectory)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            // flush the screen once we are done compressing
            stream.flush()
            stream.close()

        }catch (e: IOException){
            e.printStackTrace()
        }

        // we use the whole directory, the path name and we parse that to Uri format that exactly what our
        // methods returns
        return Uri.parse(fileDirectory.absolutePath)

    }


}