package com.blissvine.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.blissvine.happyplaces.models.HappyPlaceModel


class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
         private const val DATABASE_VERSION = 1
         private const val DATABASE_NAME = "HappyPlacesDatabase"
         private const val TABLE_HAPPY_PLACE = "HappyPlacesTable"

        // All the columns names
        /*
        Column names are the basically the ones that we have from our happy place model. So each time
        we create an object, we want to store that object inside of our database. and because
        we can not store objects as such, we need to make sure that we have the variables of those objects in the
        right format.  Which we do here all of those column names

         */
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

   // TODO (Step 4 : After Creating a database handler class. Let us create an function to insert a happy place detail to respective table.)
    //    // START
    //    /**
    //     * Function to insert a Happy Place details to SQLite Database.
    //     */
    fun addHappyPlace(happyPlace: HappyPlaceModel):Long{
         val db = this.writableDatabase // this allows us to write something in database

        val contentValues = ContentValues()
       contentValues.put(KEY_TITLE, happyPlace.title)
       contentValues.put(KEY_IMAGE, happyPlace.image)
       contentValues.put(KEY_DESCRIPTION, happyPlace.description)
       contentValues.put(KEY_DATE, happyPlace.date)
       contentValues.put(KEY_LOCATION, happyPlace.location)
       contentValues.put(KEY_LATITUDE, happyPlace.latitude)
       contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

       // Inserting Row

       val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)
       //2nd argument is String containing nullColumnHack

       db.close() // Closing database connection

       // our method returns a long and here insert plays the long role as it is a long so our
       // result variable is going to be  a long
       return result

    }

    fun updateHappyPLace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase // this allows us to write something in database

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        // update the table or the entries where the id is the same as the id that we pass when we call updateHappyPlace
        val success = db.update(TABLE_HAPPY_PLACE, contentValues, KEY_ID + "=" +happyPlace.id, null)

        db.close() // Closing database connection
        return success


    }


    fun deleteHappyPlace(happyPlace: HappyPlaceModel): Int{
          val db = this.writableDatabase
          val success = db.delete(TABLE_HAPPY_PLACE, KEY_ID + "=" + happyPlace.id, null)

          db.close()
        return success
    }


    // Retrieveing all the databse entries
    fun getHappyPlacesList(): ArrayList<HappyPlaceModel>{
          val happyPlaceList =  ArrayList<HappyPlaceModel>()
          val selectQuery = "SELECT * FROM $TABLE_HAPPY_PLACE"
          val db = this.readableDatabase   // database that we can read from

        // filling the happyPlaceList with all the entries in the happy place model
        try{
               val cursor : Cursor =  db.rawQuery(selectQuery, null)
        // we are using cursor to go through every single entry in that we have selected from our table

            // from this we say run through all the entries that we have in the database that came from the select table
            if(cursor.moveToFirst()){
                //moveToFirst is used when you need to start iterating from start after you have already reached some position.
                 do{
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                    )
                     //add this place model to our arrayList
                     happyPlaceList.add(place)

                 }while (cursor.moveToNext())
            }
            cursor.close()
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return  ArrayList() // if nothing worked fine simply return an empty array list
        }
        return happyPlaceList
    }

}