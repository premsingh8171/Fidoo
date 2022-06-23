package com.fidoo.user.newsearch.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.cartview.model.CartModel
import com.fidoo.user.cartview.viewmodel.CartViewModel
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.ActivityNewSearchStorelistingBinding
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.newsearch.adapter.SearchCategoryStoreAdapter
import com.fidoo.user.newsearch.model.Product
import com.fidoo.user.newsearch.model.Store
import com.fidoo.user.newsearch.viewmodel.SearchNewViewModel
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.restaurants.activity.NewDBStoreItemsActivity
import com.fidoo.user.restaurants.adapter.StoreCustomItemsAdapter
import com.fidoo.user.restaurants.listener.CustomCartPlusMinusClick
import com.fidoo.user.restaurants.model.CustomCheckBoxModel
import com.fidoo.user.restaurants.model.CustomListModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity
import com.fidoo.user.restaurants.viewmodel.StoreDetailsViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.showAlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_new_search_storelisting.*
import kotlinx.android.synthetic.main.item_search_parent.*
import java.math.RoundingMode
import kotlin.collections.ArrayList

class NewSearchStoreListingActivity : BaseActivity() , CustomCartPlusMinusClick, AdapterCustomRadioClick ,
    AdapterClick {
    var count: Int = 1
    var tempCount: String? = ""
    var cart_count: Int = 0
    var total_cart_count: Int = 0
    var customIdsList: ArrayList<String>? = null
    var tempPrice: Double? = 0.0
    private var slide_: Animation? = null
    var cus_itemProductId: String = ""
    var cartViewModel: CartViewModel? = null
    var viewmodel: StoreDetailsViewModel? = null
    var customNamesList: java.util.ArrayList<String>? = null
    var tempOfferPrice: String? = ""
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var tempProductId: String? = ""
    var cartId: String = ""
    private var mainlist: ArrayList<StoreItemProductsEntity>? = null
    private var productListFilter: ArrayList<StoreItemProductsEntity>? = null
    private var mModelDataTemp: CustomizeProductResponseModel? = null
    lateinit var storeID: String
    var searchStoreModel: Store? = null
    var localSearchList: List<CartModel.Cart>? = null

    var searchStoreId: String? = ""

    private var mMixpanel: MixpanelAPI? = null
    var distanceViewModel: TrackViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    private var manager1: GridLayoutManager? = null
    var countRes: Int = 0
    private var categoryy: java.util.ArrayList<CustomListModel>? = null
    private lateinit var binding: ActivityNewSearchStorelistingBinding
    var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
    var search_value: String? = ""
    var tempType: String? = ""
    var key_value: String? = ""
    var storeId: String? = ""
    var storeName: String? = ""
    var store_location: String? = ""
    var delivery_time: String? = ""
    var mCustomizeCount: Int? = 0
    private var cuisine_types: String? = ""
    var coupon_desc: String? = ""
    private var custom_itemCount: Int = 0
    var distance: String? = ""
    var sessionTwiclo: SessionTwiclo? = null
    var viewModel: SearchNewViewModel? = null
    var searchCategoryStoreAdapter: SearchCategoryStoreAdapter? = null
    companion object{
        // var addDishFromSearch : Int = 0
        // var addDishFromCart : Int = 0
        var searchType : String = "addDishFromSearch"
        var checkProductInRes : Int = 0
        var store_Name : String = ""
        var cusins_type : String = ""
        var checkSearchDish : Int = 0
        var store_location : String = ""
        var price : Int = 0
        var searchedDish : String = ""
        var handleresponce: Int = 0
        var product_customize_id: String = ""
        var lastCustomized_str: String = ""
        var storeIDCheckOnCart: String = ""
    }
    //for pagination
    var table_count: Int? = 0
    private var manager: GridLayoutManager? = null
    private var currentItems = 0
    private var totalItems: Int = 0
    private var scrollOutItems: Int = 0
    private var isScrolling = false
    private var isMore = false
    private var hit = 0
    private var pagecount = 0
    var mainList: ArrayList<Store>? = null
    var latestList: ArrayList<Store>? = null
    private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSearchStorelistingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionTwiclo = SessionTwiclo(this)
        viewModel = ViewModelProvider(this).get(SearchNewViewModel::class.java)
        manager = GridLayoutManager(this, 1)
        mainList = ArrayList()
        latestList = ArrayList()
        localSearchList = ArrayList()

        try {
            search_value = intent.getStringExtra("search_value").toString()
            key_value = intent.getStringExtra("key_value").toString()
            Log.d("key_value__", intent.getStringExtra("delivery_time").toString())
            storeId = intent.getStringExtra("storeId").toString()
            storeName = intent.getStringExtra("storeName").toString()
            store_location = intent.getStringExtra("store_location").toString()
            delivery_time = intent.getStringExtra("delivery_time").toString()
            cuisine_types = intent.getStringExtra("cuisine_types").toString()
            coupon_desc = intent.getStringExtra("coupon_desc").toString()
            distance = intent.getStringExtra("distance").toString()
        }

        catch (e: Exception) {
            e.printStackTrace()
        }
        binding.TypeTxt.text = storeName
        searchedDish = storeName.toString()
        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
        distanceViewModel = ViewModelProvider(this).get(TrackViewModel::class.java)
        behavior = BottomSheetBehavior.from(bottom_sheet)
        MainActivity.tempProductList = ArrayList()
        MainActivity.addCartTempList = ArrayList()
        customIdsList = ArrayList()
        customIdsListTemp = ArrayList()
        customNamesList = ArrayList()
        productListFilter = ArrayList()
        storeID = intent.getStringExtra("storeId")!!
        manager = GridLayoutManager(this, 1)
        manager1 = GridLayoutManager(this, 1)
        sessionTwiclo = SessionTwiclo(this)

        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(applicationContext, RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }.start()

        viewmodel = ViewModelProvider(this).get(StoreDetailsViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        viewmodelusertrack = ViewModelProvider(this).get(UserTrackerViewModel::class.java)

        cartitemView_LLstore.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(Intent(this, CartActivity::class.java).putExtra("store_id", SessionTwiclo(this).storeId))
            }
            else {
                showLoginDialog("Please login to proceed")
            }
        }

        viewmodel?.addRemoveCartResponse?.observe(this) { user ->
            Log.e("addRemoveCartRes____", Gson().toJson(user))
            if (user.errorCode == 200) {
                handleresponce = 1
                try {
                    product_customize_id = user.product_customize_id
                    Thread {
                        updateByCartIdProductCustomized(
                            user.cart_quantity!!.toInt(),
                            user.product_id!!,
                            user.is_customize_quantity!!.toInt(),
                            lastCustomized_str,
                            user.cart_id!!,
                            product_customize_id
                        )
                    }.start()
                }
                catch (e: Exception) {
                }
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getCartCountApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                }
            }
            else if (user.errorCode == 101) {
                showAlertDialog(this)
            }
        }

        customAddBtn.setOnClickListener {
            //Log.e("addCartTempList", Gson().toJson(addCartTempList))
            for (i in 0 until categoryy!!.size) {
                customIdsList!!.add(categoryy!![i].id.toString())
                customNamesList!!.add(categoryy!![i].subCatName.toString())
            }
            var lastCustomized: String = ""
            lastCustomized = customNamesList.toString()
            val regex = "\\[|\\]"
            lastCustomized_str = lastCustomized.replace(regex.toRegex(), "")
            product_customize_id = "1"
            //Log.e("customIdsList", customIdsList.toString() + "\n" + lastCustomized_str)

            MainActivity.addCartTempList!!.clear()
            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = tempProductId
            addCartInputModel.quantity = countValue.text.toString()
            addCartInputModel.message = "addviasearch"
            addCartInputModel.customizeSubCatId = customIdsList!!
            addCartInputModel.isCustomize = "1"
            MainActivity.addCartTempList!!.add(0, addCartInputModel)

            Log.d("fdgdgd", intent.getStringExtra("storeId").toString() + "\n" + SessionTwiclo(this).storeId)

            if (SessionTwiclo(this).storeId.equals(searchStoreId) || SessionTwiclo(this).storeId.equals("")) {
                //  showIOSProgress()
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
                SessionTwiclo(this).serviceId = MainActivity.service_idStr
                handleresponce = 1
                // product_customize_id
                updateProductCustomized(
                    custom_itemCount,
                    cus_itemProductId,
                    0,
                    lastCustomized_str
                )

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    cartId
                )
                Log.d("cust_sec_test", "${SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accountId}"+
                        " ${SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accessToken} "+
                        "${Gson().toJson(MainActivity.addCartTempList!!)} $cartId")

                AppUtils.startActivityRightToLeft(
                    this@NewSearchStoreListingActivity,
//                         Intent(this@NewSearchStoreListingActivity, StoreItemsActivity::class.java)
//                          Intent(this@NewSearchStoreListingActivity, NewStoreItemsActivity::class.java)
                    Intent(this@NewSearchStoreListingActivity, NewDBStoreItemsActivity::class.java)
                        .putExtra("storeId", searchStoreId)
                        .putExtra("search_value", search_value)
                        .putExtra("storeName", searchStoreModel!!.store_id)
                        .putExtra("store_location", store_location)
                        .putExtra("delivery_time", delivery_time)
                        .putExtra("cuisine_types",cusins_type)
                        .putExtra("search_type", searchType)
                        .putExtra("coupon_desc", "")
                        .putExtra("distance",distance)
                        .putExtra("product_id", tempProductId)
                        .putExtra("from_search", "1")
                )

                cartitemView_LLstore.visibility = View.VISIBLE
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else {
                clearCartPopup()
            }
        }

        viewmodel?.customizeProductResponse?.observe(this, Observer { user ->

            cartitemView_LLstore.visibility = View.GONE
            Log.e("stores___response", Gson().toJson(user))

            if (user.errorCode == 200) {
                mModelDataTemp = user

                categoryy = ArrayList()

                for (i in 0 until mModelDataTemp?.category?.size!!) {
                    if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
                        try {
                            if (mModelDataTemp?.category?.get(i)!!.isMandatory.equals("0")) {

                            } else {
                                var customListModel: CustomListModel? = CustomListModel()
                                customListModel!!.category = mModelDataTemp?.category?.get(i)!!.catId
                                try {
                                    customListModel.id = mModelDataTemp?.category?.get(i)!!.subCat[0].id.toInt()
                                    customListModel.price = mModelDataTemp?.category?.get(i)!!.subCat[0].price
                                    customListModel.subCatName = mModelDataTemp?.category?.get(i)!!.subCat[0].subCatName
                                } catch (e: Exception) {
                                }
                                categoryy!!.add(customListModel)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                tempPrice = 0.0

                //var tempPrice: Double? = 0.0
                for (i in 0 until customIdsListTemp!!.size) {
                    tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
                }

                for (i in 0 until categoryy!!.size) {
                    if (categoryy!!.get(i).price != null) {
                        tempPrice = tempPrice!! + categoryy!![i].price.toDouble()
                    }
                }
                Log.e("tempPrice", tempPrice.toString())

                try {
                    tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
                } catch (e: NumberFormatException) {
                    Log.e("Exception", e.toString())
                }
                //tempPrice = tempPrice!! + plusMinusPrice
                //showToast(tempPrice.toString())
                Log.e("tempPriceTotal", tempPrice.toString())

                // customAddBtn.text = "Item total "+resources.getString(R.string.ruppee) + tempPrice.toString()
                customAddBtn.text =
                    "Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()

                //	Log.e("tempPriceTotal_", mModelDataTemp?.category!![0].subCat.size.toString())
                try {
                    if (mModelDataTemp?.category!![0].subCat.size == 0) {
                        customAddBtn.visibility = View.GONE
                    } else {
                        customAddBtn.visibility = View.VISIBLE

                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                val adapter = StoreCustomItemsAdapter(
                    this,
                    mModelDataTemp?.category!!,
                    this,
                    categoryy,
                    this
                )
                customItemsRecyclerview.layoutManager = LinearLayoutManager(this)
                customItemsRecyclerview.setHasFixedSize(true)
                customItemsRecyclerview.adapter = adapter
                adapter.notifyDataSetChanged()
                // Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
            }
            else if (user.errorCode == 101) {
                showAlertDialog(this)
            }
        })


        binding.backIconStoreListing.setOnClickListener {
            AppUtils.finishActivityLeftToRight(this)
        }

        viewmodel?.addToCartResponse?.observe(this, Observer { user ->
            if (user.errorCode == 200) {
                handleresponce = 1
                Log.e("stores_addResponse____", Gson().toJson(user))
                if (tempType.equals("custom")) {
                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        //searchLay.visibility = View.GONE
                    }
                    else {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }

                } else {
                    MainActivity.tempProductList!!.clear()
                    MainActivity.addCartTempList!!.clear()
                }
                try {
                    product_customize_id = user.product_customize_id
                    Thread {
                        if (user.cart_quantity.equals("0")) {
                        } else {
                            updateByCartIdProductCustomized(
                                user.cart_quantity!!.toInt(),
                                user.product_id!!,
                                user.is_customize_quantity!!.toInt(),
                                lastCustomized_str,
                                user.cart_id!!,
                                user.product_customize_id
                            )
                        }
                    }.start()
                }
                catch (e: Exception) {
                }
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            } else if (user.errorCode == 101) {
                showAlertDialog(this)
            }
        })

        viewmodel?.clearCartResponse?.observe(this, Observer { user ->
            if (user.errorCode == 200) {
                Log.e("stores_response", Gson().toJson(user))
                if (tempType.equals("custom")) {

                    viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                    )
                }
                else {
                    MainActivity.addCartTempList!!.clear()
                    val addCartInputModel = AddCartInputModel()
                    addCartInputModel.productId = cus_itemProductId
                    addCartInputModel.quantity = "1"
                    addCartInputModel.message = "add product"
                    addCartInputModel.customizeSubCatId = customIdsList!!
                    addCartInputModel.isCustomize = "1"
                    MainActivity.addCartTempList!!.add(0, addCartInputModel)

                    viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                    )
                }
            }
            else if (user.errorCode == 101) {
                showAlertDialog(this)
            }
        })

        viewmodel?.cartCountResponse?.observe(this) { cartcount ->
            MainActivity.addCartTempList!!.clear()
            MainActivity.tempProductList!!.clear()

            val count = cartcount.count
            val price = cartcount.price
            SessionTwiclo(this).storeId = cartcount.store_id
            if (!cartcount.error) {
                if (!count.equals("0")) {
                    cart_count = 1
                    itemQuantity_textstore.text = count
                    val rounded = price.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

                    totalprice_txtstore.text = "₹ $rounded"

                    cartitemView_LLstore.visibility = View.VISIBLE
                    if (total_cart_count == 0) {
                        total_cart_count = 1
                        slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
                        cartitemView_LLstore?.startAnimation(slide_)
                    }
                }
                else {
                    cart_count = 0
                    itemQuantity_textstore.text = "0"
                    totalprice_txtstore.text = ""
                    cartitemView_LLstore.visibility = View.GONE
                    total_cart_count = 0
                    slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                    cartitemView_LLstore?.startAnimation(slide_)
                    SessionTwiclo(this).serviceId = ""
                }
            }
        }

        swipeRefreshLaySearch.setOnRefreshListener {
            if (sessionInstance.isLoggedIn) {
                viewModel!!.keywordBasedSearchResultsApi(
                    sessionTwiclo!!.loggedInUserDetail.accountId,
                    sessionTwiclo!!.loggedInUserDetail.accessToken,
                    key_value!!,
                    sessionTwiclo!!.userLat,
                    sessionTwiclo!!.userLng,
                    pagecount.toString())
            }
            else {
                viewModel!!.keywordBasedSearchResultsApi("", "",
                    key_value!!,
                    sessionTwiclo!!.userLat,
                    sessionTwiclo!!.userLng,
                    pagecount.toString()
                )
            }
        }

        showIOSProgress()

        if (sessionInstance.isLoggedIn) {
            viewModel!!.keywordBasedSearchResultsApi(
                sessionTwiclo!!.loggedInUserDetail.accountId,
                sessionTwiclo!!.loggedInUserDetail.accessToken,
                key_value!!,
                sessionTwiclo!!.userLat,
                sessionTwiclo!!.userLng,
                pagecount.toString()
            )
        } else {
            viewModel!!.keywordBasedSearchResultsApi("", "",
                key_value!!,
                sessionTwiclo!!.userLat,
                sessionTwiclo!!.userLng,
                pagecount.toString()
            )
        }

        onResponse()
        cartitemView_LLstore.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(Intent(this, CartActivity::class.java).putExtra("store_id", SessionTwiclo(this).storeId))
            }
        }
    }

    override fun onBackPressed() {
        AppUtils.finishActivityLeftToRight(
            this
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onResponse() {
        viewModel!!.keywordBasedSearchResultsRes!!.observe(this) {
            dismissIOSProgress()
            swipeRefreshLaySearch.isRefreshing = false
            Log.d("sdfdddfssd", Gson().toJson(it))
            if (it.error_code == 200) {
                hit = 0
                isMore = it.more_value
                latestList!!.clear()

                if (pagecount > 0) {
                    latestList = it.stores as ArrayList
                    mainList!!.addAll(latestList!!)
                    searchCategoryStoreAdapter!!.updateData(mainList!!, isMore)
                    searchCategoryStoreAdapter!!.notifyDataSetChanged()
                }
                else {
                    mainList = it.stores as ArrayList
                    rvCategoryList(mainList!!)
                }
            }
        }
    }


    override fun onRestart() {
        super.onRestart()
        viewModel!!.keywordBasedSearchResultsApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
        SessionTwiclo(this).loggedInUserDetail.accessToken,
        key_value!!,
        SessionTwiclo(this).userLat,
        SessionTwiclo(this).userLng,
        pagecount.toString())
    }
    override fun onResume() {
        super.onResume()
        deleteRoomDataBase()
        storeIDCheckOnCart = storeID
        storeID = intent.getStringExtra("storeId")!!
        Log.d("OnRESUME___", "RESUME"+intent.getStringExtra("delivery_time"))
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (handleresponce == 1) {
            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            }
        }
    }

    private fun rvCategoryList(arrayList: ArrayList<Store>) {
        searchCategoryStoreAdapter = SearchCategoryStoreAdapter(this,arrayList, key_value!!,
            object : SearchCategoryStoreAdapter.CategoryItemClick {
                override fun onItemClick(pos: Int, model: Store) {
                    val delivery_time=model.delivery_time.toString()

                    AppUtils.startActivityRightToLeft(
                        this@NewSearchStoreListingActivity,
//                         Intent(this@NewSearchStoreListingActivity, StoreItemsActivity::class.java)
//                          Intent(this@NewSearchStoreListingActivity, NewStoreItemsActivity::class.java)
                        Intent(this@NewSearchStoreListingActivity, NewDBStoreItemsActivity::class.java)
                            .putExtra("storeId", model.store_id)
                            .putExtra("search_value", search_value)
                            .putExtra("storeName", model.store_name)
                            .putExtra("store_location", store_location)
                            .putExtra("delivery_time", delivery_time)
                            .putExtra("cuisine_types", model.cuisines.joinToString(separator = ", "))
                            .putExtra("coupon_desc", "")
                            .putExtra("distance", model.distance)
                            .putExtra("from_search", "1")
                    )
                }

               //@SuppressLint("SetTextI18n")
                override fun onProductItemClick(
                    mainPos: Int,
                    modelStore: Store,
                    pos: Int,
                    model: Product,
                    type:String,
                    count:Int
                )
                {
                    //addDishFromCart = 1
                    checkProductInRes = 1
                    searchStoreId = modelStore.store_id
                    searchStoreModel = modelStore
                    store_Name = modelStore.store_name
                    cusins_type = modelStore.cuisines.joinToString(separator = ", ")
                    checkSearchDish = 1
                    Log.d("onPrdItemClick__", "$mainPos--${Gson().toJson(modelStore)}--$pos--${Gson().toJson(model)}--$type-$count")
                    cus_itemProductId = model.product_id
                    Log.d("count__addID",model.product_id)
                    if (model.is_customize == "1"){
                        customIdsListTemp?.clear()
                        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            //searchLay.visibility = View.GONE
                        } else {
                            //searchLay.visibility = View.VISIBLE
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        }
                        tempProductId = cus_itemProductId
                        // showIOSProgress()
                        customIdsList!!.clear()
                        customNamesList!!.clear()
                        tempOfferPrice = model.offer_price
                        viewmodel?.customizeProductApi(
                            SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accountId,
                            SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accessToken,model.product_id
                        )
                    } else if (model.is_customize == "0"){
                        if (SessionTwiclo(this@NewSearchStoreListingActivity).storeId.equals(modelStore.store_id)
                            ||SessionTwiclo(this@NewSearchStoreListingActivity).storeId.equals("")) {

                            MainActivity.addCartTempList!!.clear()
                            val addCartInputModel = AddCartInputModel()
                            addCartInputModel.productId = model.product_id
                            addCartInputModel.quantity = count.toString()
                            addCartInputModel.message = "addviasearch"
                            addCartInputModel.customizeSubCatId = customIdsList!!
                            addCartInputModel.isCustomize = "0"
                            MainActivity.addCartTempList!!.add(addCartInputModel)

                            viewmodel!!.addToCartApi(
                                SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accountId,
                                SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accessToken,
                                MainActivity.addCartTempList!!,
                                cartId
                            )
                            Log.d("sec_test", "${SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accountId}"+
                                    " ${SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accessToken} "+
                                    "${Gson().toJson(MainActivity.addCartTempList!!)} $cartId")
                            MainActivity.tempProductList!!.clear()

                            updateProductS(count, model.product_id)

                            val delivery_time = modelStore.delivery_time.toString()

                            AppUtils.startActivityRightToLeft(
                                this@NewSearchStoreListingActivity,
                                Intent(this@NewSearchStoreListingActivity, NewDBStoreItemsActivity::class.java)
                                    .putExtra("storeId", modelStore.store_id)
                                    .putExtra("search_value", search_value)
                                    .putExtra("storeName", modelStore.store_name)
                                    .putExtra("store_location", store_location)
                                    .putExtra("delivery_time", delivery_time)
                                    .putExtra("cuisine_types", modelStore.cuisines.joinToString(separator = ", "))
                                    .putExtra("search_type", searchType)
                                    .putExtra("coupon_desc", "")
                                    .putExtra("is_custom", model.is_customize)
                                    .putExtra("distance", modelStore.distance)
                                    .putExtra("product_id", model.product_id)
                                    .putExtra("from_search", "1")
                            )
                            SessionTwiclo(this@NewSearchStoreListingActivity).storeId = modelStore.store_id
                        }
                        else {
                            val builder = AlertDialog.Builder(this@NewSearchStoreListingActivity)
                            builder.setTitle("Replace cart item!")
                            builder.setMessage("Do you want to discard the previous selection?")
                            builder.setIcon(android.R.drawable.ic_dialog_alert)
                            builder.setPositiveButton("Yes") { _, _ ->
                                viewmodel?.clearCartApi(
                                    SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accountId,
                                    SessionTwiclo(this@NewSearchStoreListingActivity).loggedInUserDetail.accessToken
                                )
                                updateProductS(count, model.product_id)
                                val delivery_time = modelStore.delivery_time.toString()

                                AppUtils.startActivityRightToLeft(this@NewSearchStoreListingActivity,
                                    Intent(this@NewSearchStoreListingActivity, NewDBStoreItemsActivity::class.java)
                                        .putExtra("storeId", modelStore.store_id)
                                        .putExtra("search_value", search_value)
                                        .putExtra("storeName", modelStore.store_name)
                                        .putExtra("store_location", store_location)
                                        .putExtra("delivery_time", delivery_time)
                                        .putExtra("cuisine_types", modelStore.cuisines.joinToString(separator = ", "))
                                        .putExtra("coupon_desc", "")
                                        .putExtra("is_custom",model.is_customize)
                                        .putExtra("distance", modelStore.distance)
                                        .putExtra("product_id", model.product_id)
                                        .putExtra("from_search", "1")
                                )
                            }
                            builder.setNegativeButton("No") { _, _ -> }
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                            cartitemView_LLstore.visibility = View.VISIBLE
                        }
                    }
                    //addDishFromSearch = 1
                }


                // My logic for add product


                /*override fun onProductItemClick(
                    mainPos: Int,
                    modelStore: Store,
                    pos: Int,
                    model: Product,
                    type:String,
                    count:Int,
                    count: Int
                    ) {
                    if (SessionTwiclo(this@NewSearchStoreListingActivity).storeId.equals(modelStore.store_id)
                        || SessionTwiclo(this@NewSearchStoreListingActivity).storeId.equals("")){
                        if (model.is_customize.equals("1")) {
                            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                                //searchLay.visibility = View.GONE
                            } else {
                                //searchLay.visibility = View.VISIBLE
                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                            }


                        }
                    }
                }*/




            })

        binding.searchFilterRv.adapter = searchCategoryStoreAdapter
        binding.searchFilterRv.layoutManager = manager
        binding.searchFilterRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                val firstVisibleItem = manager!!.findFirstCompletelyVisibleItemPosition()
                if (dy > 1) {
                    if (isScrolling && currentItems + scrollOutItems == totalItems) {
                        Log.d("totalItem___", table_count.toString() + "---" + firstVisibleItem)
                        if (isScrolling) {
                            if (sessionTwiclo!!.isLoggedIn) {
                                if (isMore) {
                                    if (hit == 0) {
                                        pagecount++
                                        viewModel!!.keywordBasedSearchResultsApi(
                                            sessionTwiclo!!.loggedInUserDetail.accountId,
                                            sessionTwiclo!!.loggedInUserDetail.accessToken,
                                            key_value!!,
                                            sessionTwiclo!!.userLat,
                                            sessionTwiclo!!.userLng,
                                            pagecount.toString())
                                        hit = 1
                                    }
                                }
                            }
                            isScrolling = false
                        }
                    }
                }
            }
        })
    }

    private fun updateByCartIdProductCustomized(
        count: Int,
        productId: String,
        customize_quantity: Int?,
        customizeItemName: String?,
        cart_id: String?,
        product_customize_id: String?
    ) {
        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(
                applicationContext,
                RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
            restaurantProductsDatabase.resProductsDaoAccess()!!.customizeProductUpdate(
                count,
                productId,
                customize_quantity!!,
                customizeItemName,
                cart_id,
                product_customize_id
            )
        }.start()
    }

    private fun updateProductCustomized(
        count: Int,
        productId: String,
        customize_quantity: Int?,
        customizeItemName: String?
    ) {
        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(
                applicationContext,
                RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
            restaurantProductsDatabase.resProductsDaoAccess()!!.updateCustomizeProducts(
                count,
                productId,
                customize_quantity!!,
                customizeItemName
            )
        }.start()
    }

    private fun deleteRoomDataBase() {
        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(applicationContext, RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            restaurantProductsDatabase.resProductsDaoAccess()!!.deleteAll()
        }.start()
    }

    private fun updateProductS(count: Int, productId: String) {
        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(applicationContext, RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            restaurantProductsDatabase.resProductsDaoAccess()!!.updateProducts(count,productId)
        }.start()
    }

    private fun clearCartPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Replace cart item!")
        builder.setMessage("Do you want to discard the previous selection?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { _, _ ->
            viewmodel?.clearCartApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
            updateProductS(custom_itemCount, cus_itemProductId)
        }
        builder.setNegativeButton("No") { dialogInterface, which -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showLoginDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Login") { _, _ ->
            sessionTwiclo!!.clearSession()
            startActivity(Intent(this, SplashActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    override fun onCustomRadioClick(checkedId: String?, position: String?) {
        var tempCat: String? = ""
        var tempPrice: String? = ""
        var tempPriceSubCat_Name: String? = ""
        for (i in 0 until mModelDataTemp?.category!!.size) {
            for (j in 0 until mModelDataTemp?.category!!.get(i).subCat.size) {
                if (checkedId == mModelDataTemp?.category!!.get(i).subCat.get(j).id) {
                    tempCat = mModelDataTemp?.category!!.get(i).catId
                    tempPrice = mModelDataTemp?.category!!.get(i).subCat.get(j).price
                    tempPriceSubCat_Name = mModelDataTemp?.category!!.get(i).subCat.get(j).subCatName
                    break
                }
                if (!tempCat.equals("")) {
                    break
                }
            }
        }
        var tempAddEdit: String? = "add"
        var tempAddEditId: String? = "add"

        for (i in 0 until categoryy!!.size) {
            if (categoryy!![i].category.equals(tempCat)) {
                /*  var customListModel: CustomListModel?= CustomListModel()
                  customListModel!!.category= category.get(i).catId
                  customListModel!!.id= category.get(i).subCat.get(0).id.toInt()*/
                tempAddEdit = "edit"
                tempAddEditId = i.toString()
                categoryy!![i].id = checkedId!!.toInt()
                categoryy!![i].price = tempPrice
                categoryy!![i].subCatName = tempPriceSubCat_Name
                break
            }
        }

        if (tempAddEdit.equals("edit")) {
            categoryy!![tempAddEditId!!.toInt()].id = checkedId!!.toInt()
            categoryy!![tempAddEditId.toInt()].price = tempPrice
            categoryy!![tempAddEditId.toInt()].subCatName = tempPriceSubCat_Name
        }
        else {
            val customListModel = CustomListModel()
            customListModel.category = tempCat
            customListModel.id = checkedId!!.toInt()
            customListModel.price = tempPrice
            customListModel.subCatName = tempPriceSubCat_Name
            categoryy!!.add(customListModel)
        }
        var tempPricee: Double? = 0.0
        for (i in 0 until customIdsListTemp!!.size) {
            tempPricee = tempPricee!! + customIdsListTemp!!.get(i).price.toDouble()
        }

        for (i in 0 until categoryy!!.size) {
            tempPricee = tempPricee!! + categoryy!!.get(i).price.toDouble()
        }
        tempPricee = tempOfferPrice!!.toDouble() + tempPricee!!
        customAddBtn.text = resources.getString(R.string.ruppee) + tempPricee.toString()
    }

    @SuppressLint("SetTextI18n")
    override fun onIdSelected(
        productId: String?,
        type: String?,
        price: String?,
        sub_cat_name: String?,
        tempSelectionCount: Int
    ) {
        if (type == "select") {
            if (productId != null) {
                customIdsList!!.add(productId)
                customNamesList!!.add(sub_cat_name!!)
            }
            val customCheckBoxModel =
                CustomCheckBoxModel()
            customCheckBoxModel.id = productId
            customCheckBoxModel.price = price
            customIdsListTemp!!.add(customCheckBoxModel)
            //customAddBtn.text = tempOfferPrice
        }
        else if (type == "unselect") {
            for (i in 0 until customIdsList!!.size) {
                if (customIdsList!![i] == productId) {
                    customIdsList!!.removeAt(i)
                    customNamesList!!.removeAt(i)
                    customIdsListTemp!!.removeAt(i)
                    break
                }
            }
        }

        var tempIdPrice: Double? = 0.0
        //plusMinusPrice = 0.0
        tempPrice = 0.0

        for (i in 0 until customIdsListTemp!!.size) {
            tempIdPrice = tempIdPrice!! + customIdsListTemp!![i].price.toDouble()
        }

        for (i in 0 until categoryy!!.size) {
            tempIdPrice = tempIdPrice!! + categoryy!![i].price.toDouble()
        }
        tempPrice = tempOfferPrice!!.toDouble() + tempIdPrice!!
        Log.e("TempPrice on ID ", tempPrice.toString())
        //plusMinusPrice = tempIdPrice + tempPrice!!
        //tempPrice = tempPrice?.plus(plusMinusPrice)
        customAddBtn.text = "Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()

        /*if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED){
            customAddBtn.text = tempOfferPrice
        }*/
    }

    override fun onItemClick(
        productId: String?,
        type: String?,
        count: String?,
        offerPrice: String?,
        customize_count: Int?,
        productType: String?,
        cart_id: String?
    ) {

        tempType = type
        tempCount = count
        this.count = count!!.toInt()
        tempProductId = productId
        mCustomizeCount = customize_count
        tempOfferPrice = offerPrice
        countValue.text = tempCount
        cus_itemProductId = productId!!
        custom_itemCount = count.toInt()

        if (type == "custom") {
            if (mCustomizeCount == 0) {
                customIdsListTemp?.clear()

                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    //searchLay.visibility = View.GONE
                } else {
                    //searchLay.visibility = View.VISIBLE
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
                tempProductId = productId
                // storeItemsRecyclerview.visibility= View.GONE
                // showIOSProgress()
                customIdsList!!.clear()
                customNamesList!!.clear()
                viewmodel?.customizeProductApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken, productId
                )
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Your previous customization")
                builder.setMessage(tempCount)
                // builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("I'LL CHOOSE") { _, _ ->
                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                        //searchLay.visibility = View.GONE
                    }
                    else {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        //searchLay.visibility = View.VISIBLE
                    }
                    tempProductId = productId
                    //  storeItemsRecyclerview.visibility= View.GONE
                    //  showIOSProgress()
                    //customIdsList!!.clear()
                    viewmodel?.customizeProductApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken, productId
                    )
                }
                //performing negative action
                builder.setNegativeButton("REPEAT") { dialogInterface, which -> }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(true)
                alertDialog.show()
            }
        }
        else {

            if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(this).storeId.equals("")) {
                Log.e("intent store id", intent.getStringExtra("storeId").toString())
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
                Log.e("  store id", SessionTwiclo(this).storeId.toString())
                //    storeItemsRecyclerview.visibility= View.GONE
                // showIOSProgress()
                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    ""
                )
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
                // product_customize_id
                updateProductCustomized(
                    custom_itemCount,
                    cus_itemProductId,
                    0,
                    lastCustomized_str
                )
            }
            else {
                clearCartPopup()
            }
        }
    }
}