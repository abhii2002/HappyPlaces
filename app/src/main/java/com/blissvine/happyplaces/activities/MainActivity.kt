package com.blissvine.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blissvine.happyplaces.R
import com.blissvine.happyplaces.adapters.HappyPlacesAdapter
import com.blissvine.happyplaces.database.DatabaseHandler
import com.blissvine.happyplaces.models.HappyPlaceModel
import com.blissvine.happyplaces.utils.SwipeToDeleteCallback
import com.blissvine.happyplaces.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main )

        fabAddHappyPlace.setOnClickListener {
            // Caller
            val intent =   Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            getResult.launch(intent)

        }

        getHappyPlacesFromDb()
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
         rv_happy_places.layoutManager = LinearLayoutManager(this@MainActivity)
         rv_happy_places.setHasFixedSize(true)

         val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
         rv_happy_places.adapter = placesAdapter

        placesAdapter.setOnclickListener(object : HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }

        })

        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)

            }
        }



        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_places)


        val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getHappyPlacesFromDb()
            }

        }


        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_places)


    }

    private fun getHappyPlacesFromDb(){
            val dbHandler = DatabaseHandler(this@MainActivity)
            val getHappyPlaceList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

            if (getHappyPlaceList.size > 0){
                  rv_happy_places.visibility = View.VISIBLE
                  tv_no_data.visibility = View.GONE
                  setupHappyPlacesRecyclerView(getHappyPlaceList)
            }else {
                rv_happy_places.visibility = View.GONE
                tv_no_data.visibility = View.VISIBLE
            }


    }

    /**
     * Updating the recyclerview
     */

     // Receiver
    // If the result code is Result ok then update the recyclerview.
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
           if (it.resultCode == Activity.RESULT_OK ){
                getHappyPlacesFromDb()
           }else{
               Log.e("Activity", "Cancelled or Back pressed")
           }
    }

    companion object {
           var ADD_PLACE_ACTIVITY_REQUEST_CODE  = 1
           var EXTRA_PLACE_DETAILS = "extra_place_details"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 'ADD_PLACE_ACTIVITY_REQUEST_CODE'
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlacesFromDb()
            }else{
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }


}