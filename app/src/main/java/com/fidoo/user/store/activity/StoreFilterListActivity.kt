package com.fidoo.user.store.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity.Companion.service_idStr
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.data.model.Product
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase
import com.fidoo.user.grocerynewui.activity.GroceryNewUiActivity
import com.fidoo.user.newsearch.ui.NewSearchActivity
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.store.activity.StoreListActivity.Companion.check_
import com.fidoo.user.store.activity.StoreListActivity.Companion.onBackPressHandle
import com.fidoo.user.store.adapter.StoreAdapter
import com.fidoo.user.store.model.StoreListingModel
import com.fidoo.user.store.viewmodel.StoreListingViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.showAlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_store_filter_list.*
import kotlinx.android.synthetic.main.activity_store_filter_list.Store_type_text
import kotlinx.android.synthetic.main.activity_store_filter_list.backIcon
import kotlinx.android.synthetic.main.activity_store_filter_list.linear_progress_indicator
import kotlinx.android.synthetic.main.activity_store_filter_list.no_internet_store
import kotlinx.android.synthetic.main.activity_store_filter_list.no_shop_ll
import kotlinx.android.synthetic.main.activity_store_filter_list.search_stores_icon
import kotlinx.android.synthetic.main.activity_store_filter_list.sorListLlheader
import kotlinx.android.synthetic.main.activity_store_filter_list.sortRlheader
import kotlinx.android.synthetic.main.activity_store_filter_list.storesRecyclerView
import kotlinx.android.synthetic.main.activity_store_list.*
import kotlinx.android.synthetic.main.no_internet_connection.*

class StoreFilterListActivity : BaseActivity() {

    var storeListingViewModel: StoreListingViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var storeList: ArrayList<StoreListingModel.StoreList>? = null
    var storeListUpdated: ArrayList<StoreListingModel.StoreList>? = null
    var curationList: ArrayList<StoreListingModel.Curation>? = null
    var selectedValue: String? = ""
    var distanceStr: String? = ""
    private lateinit var productsDatabase: ProductsDatabase
    private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase
    var product: Product? = null
    lateinit var analytics: FirebaseAnalytics
    private var layoutManger: GridLayoutManager? = null
    var relevancePopUp: Dialog? = null
    var adapterStore: StoreAdapter? = null
    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var serive_id_: String? = ""
        var cuisine_to_search: String? = ""
    }

    var width = 0

    //for pagination
    private var barOffset = 0
    private var fabVisible = true
    private var manager: GridLayoutManager? = null
    private var currentItems = 0
    private var totalItems: Int = 0
    private var scrollOutItems: Int = 0
    private var isScrolling = false
    private var isMore = false
    private var hit = 0
    private var pagecount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_store_filter_list)
        storeList = ArrayList()
        storeListUpdated = ArrayList()
        storeListingViewModel = ViewModelProvider(this).get(StoreListingViewModel::class.java)
        viewmodelusertrack = ViewModelProvider(this).get(UserTrackerViewModel::class.java)
        analytics = FirebaseAnalytics.getInstance(this)
        manager = GridLayoutManager(this, 1)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        backIcon.setOnClickListener {
            finish()
            onBackPressHandle=1
            overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
        }

        try {
            selectedValue = intent.getStringExtra("selectedValue")
            cuisine_to_search = intent.getStringExtra("cuisine_to_search")
            Log.e("serivcuisine_to_searche_id__", cuisine_to_search!!)

            val serive_id = intent.getStringExtra("serviceId")
            val serviceName = intent.getStringExtra("serviceName")
          //  val list_val = intent.getStringExtra("list_val")
            Store_type_text.text = serviceName
            service_idStr=serive_id

            serive_id_ = serive_id

            if (serive_id.equals("5")) {
                restaurant_curationll_.visibility = View.VISIBLE
                sortRlheader.visibility = View.VISIBLE
            } else {
                restaurant_curationll_.visibility = View.GONE
                sortRlheader.visibility = View.GONE

            }
            Log.e("serive_id__", serive_id!!)

            if (cuisine_to_search.equals("")){
                restaurant_curationll_.visibility=View.GONE
            }else{
                var cusineName = intent.getStringExtra("cusineName")
                restaurant_curationll_.visibility=View.VISIBLE
                curationNameTxt.text=cusineName

            }

//            val gson = Gson()
//            val type: Type = object : TypeToken<ArrayList<StoreListingModel.StoreList?>?>() {}.getType()
//            val storeList: ArrayList<StoreListingModel.StoreList> = gson.fromJson(list_val, type)
//
//            if (storeList!!.size != 0) {
//                adapterStore = StoreAdapter(this, storeList!!)
//                storesRecyclerView.layoutManager = LinearLayoutManager(this)
//                storesRecyclerView.setHasFixedSize(true)
//                storesRecyclerView.adapter = adapterStore
//                no_shop_ll.visibility = View.GONE
//            } else {
//                no_shop_ll.visibility = View.VISIBLE
//            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        deleteRoomDataBase()
        apicall(serive_id_)
        onObserveData()


        sorListLlheader.setOnClickListener {
            relevancePopUp()
        }

        search_stores_icon?.setOnClickListener {
            AppUtils.startActivityForResultRightToLeft(
                this, Intent(this, NewSearchActivity::class.java)
                    .putExtra("storeId", "").putExtra("service_id", serive_id_),
                GroceryNewUiActivity.SEARCH_RESULT_CODE
            )
        }

        retry_onRefresh.setOnClickListener {
            apicall(serive_id_)
        }

    }

    private fun onObserveData() {
        storeListingViewModel?.getStoresApi?.observe(this) { user ->
            dismissIOSProgress()
            linear_progress_indicator.visibility = View.GONE
            sortRlheader.visibility = View.VISIBLE
            Log.e("stores_response_h_", Gson().toJson(user))
            if (!user.error) {
                dismissIOSProgress()
                storeList!!.clear()
                val mModelData: StoreListingModel = user
                storeList = mModelData.storeList as ArrayList
                hit = 0
                isMore = user.more_value

                if (pagecount > 0) {
                    storeListUpdated = mModelData.storeList as ArrayList
                    storeList!!.addAll(storeListUpdated!!)
                    Log.d("ordersList__", storeList!!.size.toString())
                    adapterStore!!.updateData(storeList!!, isMore)
                    adapterStore!!.notifyDataSetChanged()
                } else {
                    storeList = mModelData.storeList as ArrayList
                    storeListRv(storeList!!)

                }
                if (storeList!!.size != 0) {
                    no_shop_ll.visibility = View.GONE
                } else {
                    no_shop_ll.visibility = View.VISIBLE
                }

            } else {
                if (user.errorCode == 101) {
                    showAlertDialog(this)
                }
            }
        }

        storeListingViewModel?.failureResponse?.observe(this) {
            dismissIOSProgress()
            linear_progress_indicator.visibility = View.GONE
            Log.d("failureResponse___", it.toString())
        }


    }

    private fun storeListRv(storeList: ArrayList<StoreListingModel.StoreList>) {
        adapterStore = StoreAdapter(this, storeList!!)
        storesRecyclerView.layoutManager = LinearLayoutManager(this)
        storesRecyclerView.setHasFixedSize(true)
        storesRecyclerView.adapter = adapterStore

        storesRecyclerView.layoutManager = manager
        storesRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager!!.childCount
                totalItems = manager!!.itemCount
                scrollOutItems = manager!!.findFirstVisibleItemPosition()
                var firstvisibleItem = manager!!.findFirstCompletelyVisibleItemPosition()

                Log.d(
                    "value_g_",
                    "$dy-$currentItems---$totalItems---$scrollOutItems---$firstvisibleItem"
                )

                if (dy > 1) {
                    if (isScrolling && currentItems + scrollOutItems == totalItems) {
                        if (isScrolling == true) {
                            if (isMore) {
                                if (hit == 0) {
                                    pagecount++
                                    //call api here
                                    Log.d("totalItem___", "aaya")

                                    if (SessionTwiclo(this@StoreFilterListActivity).isLoggedIn) {
                                        if (serive_id_ != null) {
                                            storeListingViewModel!!.getStores(
                                                SessionTwiclo(this@StoreFilterListActivity).loggedInUserDetail.accountId,
                                                SessionTwiclo(this@StoreFilterListActivity).loggedInUserDetail.accessToken,
                                                serive_id_!!,
                                                SessionTwiclo(this@StoreFilterListActivity).userLat,
                                                SessionTwiclo(this@StoreFilterListActivity).userLng,
                                                "",
                                                "",
                                                selectedValue,
                                                cuisine_to_search,
                                                pagecount.toString()
                                            )
                                        }
                                    }

                                    hit = 1
                                }
                                isScrolling = false
                                isMore = false
                            }

                        }
                    }
                }
            }
        })


    }

    private fun apicall(serive_id: String?) {

        if (isNetworkConnected) {
              showIOSProgress()
            if (SessionTwiclo(this).isLoggedIn) {

                if (serive_id != null) {
                    storeListingViewModel!!.getStores(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        serive_id,
                        SessionTwiclo(this).userLat,
                        SessionTwiclo(this).userLng,
                        "",
                        "",
                        selectedValue,
                        cuisine_to_search, pagecount.toString()
                    )
                }

                viewmodelusertrack?.customerActivityLog(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).mobileno, "StoreListing Screen",
                    SplashActivity.appversion, serive_id_, SessionTwiclo(this).deviceToken
                )

            } else {
                if (serive_id != null) {
                    storeListingViewModel!!.getStores(
                        "",
                        "",
                        serive_id,
                        SessionTwiclo(this).userLat,
                        SessionTwiclo(this).userLng,
                        "",
                        "",
                        selectedValue, cuisine_to_search, pagecount.toString()
                    )
                }
            }

            no_internet_store.visibility = View.GONE
            no_internet_Ll.visibility = View.GONE
            //  store_list_ll.visibility = View.VISIBLE
            linear_progress_indicator.visibility = View.GONE

        } else {
            no_internet_store.visibility = View.VISIBLE
            no_internet_Ll.visibility = View.VISIBLE
            linear_progress_indicator.visibility = View.GONE
        }



    }

    //delete room data
    private fun deleteRoomDataBase() {
        Thread {
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
            productsDatabase!!.productsDaoAccess()!!.deleteAll()

        }.start()

        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(
                applicationContext,
                RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
            restaurantProductsDatabase!!.resProductsDaoAccess()!!.deleteAll()

        }.start()

    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        deleteRoomDataBase()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
        onBackPressHandle=1
    }

    fun relevancePopUp() {
        relevancePopUp = Dialog(this)
        relevancePopUp?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        relevancePopUp?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        relevancePopUp?.setContentView(R.layout.relevance_popup)

        relevancePopUp?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        // relevancePopUp?.window?.attributes?.windowAnimations = R.style.diologIntertnet

        relevancePopUp?.setCanceledOnTouchOutside(true)
        relevancePopUp?.show()
        val dismisspopUp_ = relevancePopUp?.findViewById<ConstraintLayout>(R.id.dismisspopUp_)
        val relevance = relevancePopUp?.findViewById<TextView>(R.id.relevance)
        val RatingTxt_ = relevancePopUp?.findViewById<TextView>(R.id.RatingTxt_)
        val delivery_timeTxt = relevancePopUp?.findViewById<TextView>(R.id.delivery_timeTxt)
        dismisspopUp_!!.setOnClickListener {
            relevancePopUp!!.dismiss()
        }

        if (check_.equals("0")) {
            relevance!!.setTextColor(getResources().getColor(R.color.primary_color))
            RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
            delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))

        } else if (check_.equals("1")) {
            relevance!!.setTextColor(getResources().getColor(R.color.black))
            RatingTxt_!!.setTextColor(getResources().getColor(R.color.primary_color))
            delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))

        } else if (check_.equals("2")) {

            relevance!!.setTextColor(getResources().getColor(R.color.black))
            RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
            delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.primary_color))

        }

        relevance!!.setOnClickListener {
            showIOSProgress()
            relevancePopUp!!.dismiss()
           // adapterStore!!.setFilter(storeList as ArrayList)
            selectedValue=""
            pagecount=0
            apicall(serive_id_)
            relevance.setTextColor(getResources().getColor(R.color.primary_color))
            RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
            delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))
            check_ = "0"
        }

        RatingTxt_!!.setOnClickListener {
            showIOSProgress()
            relevancePopUp!!.dismiss()
          //  var sortedList = storeList!!.sortedWith(compareBy({ it.rating })).reversed()
           // Log.d("sortedList",Gson().toJson(sortedList))
          //  adapterStore!!.setFilter(sortedList!!)
            pagecount=0
            selectedValue="rating"
            relevance.setTextColor(getResources().getColor(R.color.black))
            RatingTxt_!!.setTextColor(getResources().getColor(R.color.primary_color))
            delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))
            apicall(serive_id_)
            check_ = "1"
        }

        delivery_timeTxt!!.setOnClickListener {
            showIOSProgress()
            relevancePopUp!!.dismiss()
            selectedValue="distance"
            pagecount=0
            apicall(serive_id_)
         //   var sortedList = storeList!!.sortedWith(compareBy({ it.delivery_time }))
          //  adapterStore!!.setFilter(sortedList!!)

            relevance.setTextColor(getResources().getColor(R.color.black))
            RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
            delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.primary_color))
            check_ = "2"
        }

    }
}