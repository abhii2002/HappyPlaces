package com.blissvine.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.blissvine.happyplaces.R
import com.blissvine.happyplaces.activities.AddHappyPlaceActivity
import com.blissvine.happyplaces.activities.MainActivity
import com.blissvine.happyplaces.database.DatabaseHandler
import com.blissvine.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.item_happy_place.view.iv_place_image
import kotlinx.android.synthetic.main.item_happy_place.view.tv_description
import kotlinx.android.synthetic.main.item_happy_place.view.tv_title

class HappyPlacesAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    /**
     * Inflates the item views which is designed in xml Layout file
     *
     * create a new
     *
     * {@link ViewHolder} and initializes some private fields to be used by Re
     */


    /**
     * A ViewHolder describes an item view and metadata about its place within
     */


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_happy_place, parent, false)
        )
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      val model = list[position] // list at a position

        if (holder is MyViewHolder){
             holder.itemView.iv_place_image.setImageURI(Uri.parse(model.image))
            holder.itemView.tv_title.text = model.title
            holder.itemView.tv_description.text = model.description
            //pass the onclick for every single item in the recyclerview
            holder.itemView.setOnClickListener{
                 if (onClickListener != null){
                     onClickListener!!.onClick(position, model)
                 }
            }
        }

    }

    interface OnClickListener{
        fun onClick(position: Int,  model: HappyPlaceModel)

    }

    //functions which binds the onclickListener

    fun setOnclickListener(onClickListener: OnClickListener){
         this.onClickListener = onClickListener
    }


    private class MyViewHolder(view : View): RecyclerView.ViewHolder(view)

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
          val intent  = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun removeAt(position: Int){
         val dbHandler = DatabaseHandler(context)
         val isDelete = dbHandler.deleteHappyPlace(list[position])
          if (isDelete > 0){
               list.removeAt(position)
              notifyItemRemoved(position)
          }
    }



}