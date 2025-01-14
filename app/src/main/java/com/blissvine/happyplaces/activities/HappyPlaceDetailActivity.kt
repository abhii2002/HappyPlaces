package com.blissvine.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blissvine.happyplaces.R
import com.blissvine.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_happy_place_detail.iv_place_image
import kotlinx.android.synthetic.main.activity_happy_place_detail.toolbar_happy_place_detail
import kotlinx.android.synthetic.main.activity_happy_place_detail.tv_description
import kotlinx.android.synthetic.main.activity_happy_place_detail.tv_location
import kotlinx.android.synthetic.main.item_happy_place.tv_title

class HappyPlaceDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)


        var happyPlaceDetailModel: HappyPlaceModel? =  null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){

             happyPlaceDetailModel =
                 intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS ) as HappyPlaceModel?
        }

        if(happyPlaceDetailModel != null){
              setSupportActionBar(toolbar_happy_place_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailModel.title

            toolbar_happy_place_detail.setNavigationOnClickListener {
                onBackPressed()
            }


            tv_description.text = happyPlaceDetailModel.description
            tv_location.text = happyPlaceDetailModel.location


            iv_place_image.setImageURI(Uri.parse(happyPlaceDetailModel.image))
        }
    }
}