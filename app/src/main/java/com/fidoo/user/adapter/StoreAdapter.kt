package com.fidoo.user.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.StoreItemsActivity
import com.fidoo.user.grocery.activity.GroceryItemsActivity
import kotlinx.android.synthetic.main.fav_store_item.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StoreAdapter(val context: Context, private val storeList: MutableList<com.fidoo.user.data.model.StoreListingModel.StoreList>):
    RecyclerView.Adapter<StoreAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var storeName: TextView? = view.storeName
        var storeImg = view.productImg
        var location = view.tv_location
        //var ratingTxt = view.ratingTxt
        var deliveryTimeTxt: TextView? = view.tv_deliveryTime
        var mainLay: LinearLayout? = view.store_lay
        var onOffText: TextView? = view.open_close_status
        //var comingSoon = view.coming_soon_text
        var closingTimeText = view.closing_time
        var cuisine: TextView? = view.cuisine_types
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder (
         LayoutInflater
        .from(parent.context)
        .inflate(R.layout.fav_store_item, parent, false))


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        if (!storeList[position].cuisines.isNullOrEmpty()){
            holder.cuisine?.text = storeList[position].cuisines.joinToString (separator = ", ")

        }else{

        }



        //Log.d("kb","adapter time is"+time)
        var date: Date? = null
        try {
            date = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).parse(storeList[position].store_closing_time)
            //holder.closingTimeText.text = "Closing Time: "+ SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        // format the java.util.Date object to the desired format

        holder.closingTimeText.text = "Closing Time: "+SimpleDateFormat("hh:mm aa").format(date)
        holder.storeName?.text  = storeList[position].name
        //holder.locText.text = storeList[position].address
        if (storeList[position].rating == ""){
            //holder.ratingTxt.text = "--"
        }else{
            //holder.ratingTxt.text = storeList[position].rating
        }

        if(storeList[position].status.equals("1")) {


            if (storeList[position].open_close_status.equals("1")){
                holder.onOffText?.visibility=View.VISIBLE
                holder.onOffText?.text = "Open"
                holder.onOffText?.setTextColor(Color.rgb(51, 147, 71))

            }else if (storeList[position].open_close_status.equals("0")){
                holder.onOffText?.text = "Offline"
                holder.onOffText?.setTextColor(Color.rgb(240, 0, 0))
                holder.onOffText?.visibility = View.VISIBLE
            }

        }else if (storeList[position].status.equals("2")){
            //holder.onOffText.setTextColor(Color.rgb(245,195,48))
            holder.onOffText?.setTextColor(Color.rgb(245,195,48))
            holder.onOffText?.text = "Coming Soon"
        }

        holder.deliveryTimeTxt?.text = storeList[position].delivery_time + " minutes"
        holder.location?.text = storeList[position].distance + "kms"


        Glide.with(context)
            .load(storeList[position].image)
            .fitCenter()
            .into(holder.storeImg)

        holder.mainLay?.setOnClickListener {

            if (storeList[position].status.equals("1")) {
                if (storeList[position].open_close_status.equals("1")) {
                    if (storeList[position].has_product_categories.equals("1")) {
                        context.startActivity(
                            Intent(context, GroceryItemsActivity::class.java)
                                .putExtra("storeId", storeList[position].id)
                                .putExtra("store_name", storeList.get(position).name)
                                .putExtra("store_location", storeList[position].address)
                                .putExtra("delivery_time", storeList[position].delivery_time)
                                .putExtra("cuisine_types", storeList[position].cuisines.joinToString (separator = ", "))
                                .putExtra("distance", storeList[position].distance)
                        )
                    } else {
                        context.startActivity(
                            Intent(context, StoreItemsActivity::class.java)
                                .putExtra("storeId", storeList[position].id)
                                .putExtra("storeName", storeList[position].name)
                                .putExtra("store_location", storeList[position].address)
                                .putExtra("delivery_time", storeList[position].delivery_time)
                                .putExtra("cuisine_types", storeList[position].cuisines.joinToString (separator = ", "))
                                .putExtra("distance", storeList[position].distance)
                        )
                    }
                } else if (storeList[position].open_close_status.equals("0")) {
                    Toast.makeText(context, "Currently store is offline", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(context, "This store is  coming soon", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount() = storeList.size


}