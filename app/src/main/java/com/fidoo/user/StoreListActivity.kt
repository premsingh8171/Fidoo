package com.fidoo.user


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.fidoo.user.adapter.StoreAdapter
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.*
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase
import com.fidoo.user.grocery.roomdatabase.database.ProductsEntitiy
import com.fidoo.user.search.activity.SearchItemActivity
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.viewmodels.StoreListingViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_store_list.*


class StoreListActivity : com.fidoo.user.utils.BaseActivity() {

    var storeListingViewModel: StoreListingViewModel? = null
    var storeList: MutableList<StoreListingModel.StoreList>? = null
    var filterVisibility:Int=0
    var selectionDistance:Int=0
    var selectionRating:Int=0
    var selectedValue:String?=""
    var distanceStr:String?=""
    var ratingStr:String?=""
    private lateinit var productsDatabase: ProductsDatabase
    var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_store_list)
        val mainText = "<span style=\"color:#339347\">Choose </span> <span>Your <br/>Favorite</span> <span style=\"color:#339347\">Store</span></string>"
        tv_choose_your_fav_store.text = HtmlCompat.fromHtml(mainText, HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        storeListingViewModel = ViewModelProviders.of(this).get(StoreListingViewModel::class.java)


        backIcon.setOnClickListener { finish() }

        val serive_id=intent.getStringExtra("serviceId")

        Thread{
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            productsDatabase!!.productsDaoAccess()!!.deleteAll()

        }.start()
        apicall(serive_id)


        filter_icon.setOnClickListener {
            if (filterVisibility==0) {
                filtervalueLL.visibility = View.VISIBLE
                filterVisibility=1;
            }else{
                filtervalueLL.visibility = View.GONE
                filterVisibility=0;
            }
        }

        distance_txt.setOnClickListener {

            if (selectionDistance==0) {
                distanceStr="distance"
                distance_txt.setBackgroundResource(R.drawable.black_rounded_solid)
                selectionDistance=1;

            }else{
                distanceStr=""
                distance_txt.setBackgroundResource(R.drawable.round_outline)
                selectionDistance=0;
            }
            selectedValue =distanceStr+","+ratingStr
            if (ratingStr!!.isEmpty()){
                selectedValue=distanceStr
            }
            apicall(serive_id)
        }

        rating_txt.setOnClickListener {
            if (selectionRating==0) {
                ratingStr="rating"
                rating_txt.setBackgroundResource(R.drawable.black_rounded_solid)
                selectionRating=1;
            }else{
                ratingStr=""
                rating_txt.setBackgroundResource(R.drawable.round_outline)
                selectionRating=0;
            }
            selectedValue =distanceStr+","+ratingStr
            if (distanceStr!!.isEmpty()){
                selectedValue=ratingStr
            }
            apicall(serive_id)
        }

        search_stores_icon?.setOnClickListener {
            startActivity(Intent(this,SearchItemActivity::class.java))
        }
    }

    private fun apicall(serive_id:String?) {

        if (isNetworkConnected) {
            if (SessionTwiclo(this).isLoggedIn){
                if (serive_id != null) {
                    storeListingViewModel!!.getStores(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        serive_id,
                        SessionTwiclo(this).userLat,
                        SessionTwiclo(this).userLng,
                        "",
                        "",
                        selectedValue
                    )
                }
            }else{
                if (serive_id != null) {
                    storeListingViewModel!!.getStores(
                        "",
                        "",
                        serive_id,
                        SessionTwiclo(this).userLat,
                        SessionTwiclo(this).userLng,
                        "",
                        "",
                        selectedValue
                    )
                }
            }
        } else {
            showInternetToast()

        }


        storeListingViewModel?.getStoresApi?.observe(this, Observer { user ->
            linear_progress_indicator.visibility = View.GONE
            if (!user.error) {
                //val storeLat = user.storeList[0].storeLat
                //val storeLong = user.storeList[0].storeLong
                //calculateEstimatedDeliveryTime(storeLat, storeLong)

                Log.e("stores_response", Gson().toJson(user))
                val mModelData: StoreListingModel = user
                //mDistanceStartTemp = mModelData.distance_start
                //mDistanceEndTemp = mModelData.distance_end
                storeList = mModelData.storeList

                if (storeList!!.size!=0) {
                    val adapter = StoreAdapter(this, storeList!!)
                    storesRecyclerView.layoutManager = LinearLayoutManager(this)
                    storesRecyclerView.setHasFixedSize(true)
                    storesRecyclerView.adapter = adapter
                }else{
                    val toast = Toast.makeText(applicationContext, "No Store found nearby your area", Toast.LENGTH_SHORT)
                    toast.show()
                }

            } else {
                if (user.errorCode == 101) {
                    showAlertDialog(this)
                }
            }

        })

    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        // Log.d("sfdhjfdds","aaya")
        Thread{
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            productsDatabase!!.productsDaoAccess()!!.deleteAll()

        }.start()
    }
}