package com.fidoo.user.restaurants.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.restaurants.listener.AdapterCartAddRemoveClick
import com.fidoo.user.restaurants.model.StoreDetailsModel
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import kotlinx.android.synthetic.main.grocery_cat_header_layout.view.*

@Suppress("DEPRECATION")
class CategoryHeaderAdapter(
    var context: Context,
    var list:ArrayList<StoreDetailsModel.Category>,
    private val adapterClick: AdapterClick,
    var adapterAddRemoveClick: AdapterAddRemoveClick,
    private val adapterCartAddRemoveClick: AdapterCartAddRemoveClick,
    private val storeID: String,
    var categoryItemClick:CategoryItemClick): RecyclerView.Adapter<CategoryHeaderAdapter.ViewHolder>(){



    private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_cat_header_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.cat_tv_header.text = list.get(position).catName
        holder.itemView.category_LL_header.setOnClickListener {
            //active_or_not=position
            categoryItemClick.onItemClick(position,list.get(position))
            notifyDataSetChanged()
        }
        //getRoomData(list.get(position).catId)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface CategoryItemClick {
        fun onItemClick(pos: Int,category:StoreDetailsModel.Category)
    }

//    private fun rvStoreItemlisting(productList_: ArrayList<StoreItemProductsEntity>) {
//        if(countRes==0) {
//            storeItemsRecyclerview.layoutManager = LinearLayoutManager(this)
//            storeItemsRecyclerview.setHasFixedSize(true)
//            storeItemsAdapter= StoreItemsAdapter(
//                this,
//                this,
//                productList_,
//                "",
//                "",
//                "3.5",
//                "5",
//                this,
//                this,
//                0,
//                storeID
//            )
//            //   storeItemsRecyclerview.adapter = storeItemsAdapter
//            storeItemsRecyclerview.layoutManager = manager
//            storeItemsRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                        isScrolling = true
//                        StoreItemsActivity.handleresponce =0
//                    }
//
//                }
//
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    currentItems = manager!!.childCount
//                    totalItems = manager!!.itemCount
//                    scrollOutItems = manager!!.findFirstVisibleItemPosition()
//                    Log.d("value_gg_", "$dy-$currentItems---$totalItems---$scrollOutItems");
//
//                    if (dy > 1) {
//                        if (isScrolling && currentItems + scrollOutItems == totalItems) {
//                            Log.d("totalItem___", table_count.toString()+"---"+ StoreItemsActivity.productsListing_Count)
//                            if (table_count!! > StoreItemsActivity.productsListing_Count!!) {
//                                if (isScrolling == true) {
//                                    totalItem = totalItem?.plus(20)
//                                    StoreItemsActivity.handleresponce = 1
//                                    showIOSProgress()
//                                    getRoomData()
//                                    isScrolling = false
//                                }
//                            }
//                        }
//                    }
//                }
//            })
//
//        }else{
//            storeItemsAdapter.updateData(productList_)
//        }
//
//    }

    //get data from room
//    private fun getRoomData(cat_id:String) {
//        Thread{
//            restaurantProductsDatabase = Room.databaseBuilder(
//                context,
//                RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME)
//                .fallbackToDestructiveMigration()
//                .build()
//
//        }.start()
//        Handler().postDelayed(
//            {
//                restaurantProductsDatabase!!.resProductsDaoAccess()!!.getSpecificValue(cat_id)
//
//            }, 100
//        )
//    }


}