package com.fidoo.user.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.cartview.viewmodel.CartViewModel
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.newRestaurants.model.Product
import com.fidoo.user.newRestaurants.model.Subcategory
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.restaurants.activity.NewDBStoreItemsActivity
import com.fidoo.user.restaurants.activity.New_storeitem_search
import com.fidoo.user.restaurants.adapter.CategoryHeaderAdapter
import com.fidoo.user.restaurants.adapter.NewDbRestaurantCategoryAdapter
import com.fidoo.user.restaurants.adapter.StoreCustomItemsAdapter
import com.fidoo.user.restaurants.adapter.new_storeItem_searchAdapter
import com.fidoo.user.restaurants.listener.AdapterCartAddRemoveClick
import com.fidoo.user.restaurants.listener.CustomCartPlusMinusClick
import com.fidoo.user.restaurants.model.CustomCheckBoxModel
import com.fidoo.user.restaurants.model.CustomListModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity
import com.fidoo.user.restaurants.viewmodel.StoreDetailsViewModel
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.hideKeyboard
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.utils.showKeyboard
import com.google.android.datatransport.runtime.ExecutionModule_ExecutorFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.activity_new_product_search.*
import kotlinx.android.synthetic.main.activity_new_store_items.*
import kotlinx.android.synthetic.main.activity_new_store_items.bottom_sheet
import kotlinx.android.synthetic.main.activity_new_store_items.countValue
import kotlinx.android.synthetic.main.activity_new_store_items.customAddBtn
import kotlinx.android.synthetic.main.activity_new_store_items.customItemsRecyclerview
import kotlinx.android.synthetic.main.activity_new_store_items.totalprice_txtstore
import kotlinx.android.synthetic.main.activity_new_store_items.transLay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.math.RoundingMode

class newhotel_ProductSearch():Fragment(), AdapterClick,
    CustomCartPlusMinusClick,
    AdapterCustomRadioClick,
    AdapterAddRemoveClick,
    AdapterCartAddRemoveClick {

    private var categoryy: ArrayList<CustomListModel>? = null
    private var mainlist: ArrayList<StoreItemProductsEntity>? = null
    private var veg_item_list: ArrayList<StoreItemProductsEntity>? = null
    private var productListFilter: ArrayList<StoreItemProductsEntity>? = null
    private var filterdSearch_list: ArrayList<StoreItemProductsEntity>? = null
    private var mModelDataTemp: CustomizeProductResponseModel? = null
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
    var cartViewModel: CartViewModel? = null //Un used
    var viewmodel: StoreDetailsViewModel? = null
    var distanceViewModel: TrackViewModel? = null//Un used
    var customIdsList: ArrayList<String>? = null
    var customNamesList: ArrayList<String>? = null
    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0
    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempType: String? = ""
    var tempCount: String? = ""
    var sub_cat_nameStr: String? = ""
    var count: Int = 1
    var countRes: Int = 0
    var veg: Int = 0
    var nonveg: Int = 0
    var vegcount=0
    var isonlyveg= false
    var nonveg_str: String = ""
    var contains_egg: String = ""
    private lateinit var mMap: GoogleMap
    var cartId: String = ""
    lateinit var storeID: String
    var cat_listShow: Int = 0
    var cus_itemProductId: String = ""
    var custom_itemCount: Int = 0
    var total_cart_count: Int = 0
    var search_value: String? = ""
    var storeName: String = ""
    private var mMixpanel: MixpanelAPI? = null
    lateinit var isVegApplied : Flow<List<StoreItemProductsEntity>>
    lateinit var searchResult1 : LiveData<List<StoreItemProductsEntity>>


    companion object {
        lateinit var mView: View
        var lastCustomized_str: String = ""
        var product_customize_id: String = ""
        var customerLatitude: String = ""
        var customerLongitude: String = ""
        var handleresponce: Int = 0
        var productsListing_Count: Int? = 0
        var storeIDCheckOnCart: String = ""
        //for bottom view of restaurant license
        var fssai: String? = ""
        var restaurantName: String? = ""
        var restaurantAddress: String? = ""
    }



    //for pagination
    var totalItem: Int? = 800
    var table_count: Int? = 0
    private var manager: GridLayoutManager? = null
    private var manager1: GridLayoutManager? = null
    private var currentItems = 0
    private var totalItems: Int = 0
    private var scrollOutItems: Int = 0
    private var isScrolling = false

    var veg_filter: Int = 0
    var egg_filter: Int = 0
    var cat_visible: Int = 0
    var isCustomizeOpen: Int = 0
    var filterActive: Int = 0// for handle filter api call response
    var cart_count: Int = 0
    lateinit var storeItemsAdapter: new_storeItem_searchAdapter
    lateinit var restaurantCategoryAdapter: NewDbRestaurantCategoryAdapter
    lateinit var categoryHeaderAdapter: CategoryHeaderAdapter

    var selectCategoryDiolog: Dialog? = null
    private var slide_: Animation? = null
    lateinit var catrecyclerView: RecyclerView
    lateinit var viewAll_txt: TextView
    var cat_id: String? = ""
    var headerActiveorNot: String? = ""
    var active_or_not: Int = 0
    var clickevent: Int = 1
    var gotdata:Int=0
    var catList: ArrayList<Subcategory> = ArrayList()
    var new_veggielist: ArrayList<Subcategory> = ArrayList()
    var latestCatList: ArrayList<Subcategory> = ArrayList()
    var sessionTwiclo: SessionTwiclo? = null
    var viewmodelusertrack: UserTrackerViewModel? = null

    //roomdb
    private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase
    private var barOffset = 0
    private var fabVisible = true
    var pagecount: Int = 0
    var next_available: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView= inflater.inflate(R.layout.activity_new_product_search, container, false)



        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
        distanceViewModel = ViewModelProvider(this).get(TrackViewModel::class.java)
        behavior = BottomSheetBehavior.from(bottom_sheet)
        MainActivity.tempProductList = ArrayList()
        MainActivity.addCartTempList = ArrayList()
        customIdsList = ArrayList()
        customNamesList = ArrayList()
        customIdsListTemp = ArrayList()
        mainlist = ArrayList()
        productListFilter = ArrayList()
        storeID = intent.getStringExtra("storeId")!!
        New_storeitem_search.storeIDCheckOnCart = storeID
        Log.d("storeIDCheckOnCart___", New_storeitem_search.storeIDCheckOnCart)
        manager = GridLayoutManager(this, 1)
        manager1 = GridLayoutManager(this, 1)

        New_storeitem_search.customerLatitude = ""
        New_storeitem_search.customerLongitude = ""
        //  store_preference_Rlay  for meat ui Gone
        mainlist!!.clear()
        sessionTwiclo = SessionTwiclo(this)


        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(
                applicationContext,
                RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()

        }.start()



        viewmodel = ViewModelProvider(this).get(StoreDetailsViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        viewmodelusertrack = ViewModelProvider(this).get(UserTrackerViewModel::class.java)


        cartitemView_LLstore1.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(
                    Intent(this, CartActivity::class.java).putExtra(
                        "store_id",
                        SessionTwiclo(this).storeId
                    )
                )
            } else {
                showLoginDialog("Please login to proceed")
            }
        }



        backIcon_Btn.setOnClickListener {
            searchKeyETxtAct.text.clear()
            productListFilter!!.clear()
//            finish()
//            AppUtils.finishActivityLeftToRight(this)

        }


        search_cardvv.setOnClickListener {
            searchKeyETxtAct.isCursorVisible = true
            showKeyboard(searchKeyETxtAct)
        }

        searchKeyETxtAct.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tvResturnt_name.text= intent.getStringExtra("storeName")
                search_cardvv.visibility= View.GONE
                editTxtAct.visibility= View.GONE
                search_cardvvname.visibility= View.VISIBLE
                search_ingrl.visibility= View.VISIBLE
                showingResult.text= " You Searched \"${searchKeyETxtAct.text}\" "
                hideKeyboard(searchKeyETxtAct)
            }
            false
        }

        searchImg_changeAdd221.setOnClickListener {
            search_cardvv.visibility= View.VISIBLE
            editTxtAct.visibility= View.VISIBLE
            search_cardvvname.visibility= View.GONE
            search_ingrl.visibility= View.GONE
            showingResult.text= "Showing Result "
            searchKeyETxtAct.isCursorVisible = true
            showKeyboard(searchKeyETxtAct)
        }





        searchKeyETxtAct?.addTextChangedListener(object  : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editTxtAct.setTextColor(resources.getColor(R.color.colorTextGray))
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                search_value = p0.toString()




                searchQuery(search_value)
                rvStoreItemlisting(productListFilter!!)


            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

//

        customAddBtn.setOnClickListener {
            //Log.e("addCartTempList", Gson().toJson(addCartTempList))
            for (i in 0 until categoryy!!.size) {
                customIdsList!!.add(categoryy!![i].id.toString())
                customNamesList!!.add(categoryy!![i].subCatName.toString())
            }
            var lastCustomized: String = ""
            lastCustomized = customNamesList.toString()
            val regex = "\\[|\\]"
            New_storeitem_search.lastCustomized_str = lastCustomized.replace(regex.toRegex(), "")
            New_storeitem_search.product_customize_id = "1"
            //Log.e("customIdsList", customIdsList.toString() + "\n" + lastCustomized_str)

            MainActivity.addCartTempList!!.clear()
            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = tempProductId
            addCartInputModel.quantity = countValue.text.toString()
            addCartInputModel.message = "add product"
            addCartInputModel.customizeSubCatId = customIdsList!!
            addCartInputModel.isCustomize = "1"
            MainActivity.addCartTempList!!.add(0, addCartInputModel)



            if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(
                    this
                ).storeId.equals("")
            ) {

                showIOSProgress()
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
                SessionTwiclo(this).serviceId = MainActivity.service_idStr

                New_storeitem_search.handleresponce = 1
                // product_customize_id
                updateProductCustomized(
                    custom_itemCount,
                    cus_itemProductId!!,
                    0,
                    New_storeitem_search.lastCustomized_str!!
                )

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    cartId
                )
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                clearCartPopup()
            }
        }


        //Default behaviour of Bottom Sheet
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        storeName = intent.getStringExtra("storeName").toString()



        transLay.setOnClickListener {
            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                //searchLay.visibility = View.GONE
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                //searchLay.visibility = View.VISIBLE
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

            }
        }

        if (isNetworkConnected) {
            if (sessionTwiclo!!.isLoggedIn) {
                getStoreDetailsApiCall()

                if (SessionTwiclo(this).loggedInUserDetail.accountId != null) {
                    viewmodelusertrack?.customerActivityLog(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).mobileno,
                        "Restaurant Screen",
                        SplashActivity.appversion,
                        StoreListActivity.serive_id_,
                        SessionTwiclo(this).deviceToken
                    )
                }
            } else {
                getStoreDetailsApiCall()
            }
        } else {

        }


        viewmodel?.newStoreDetailsRes?.observe(this@New_storeitem_search, Observer { storeData ->

            Log.d("getStoreDetailsApi__", Gson().toJson(storeData))

            MainActivity.tempProductList!!.clear()
            MainActivity.addCartTempList!!.clear()
            next_available = storeData.next_available
            filterActive = storeData.next_available
            latestCatList.clear()

            // if (next_available.toString().equals("1")){






            dismissIOSProgress()
            // }

//            if (cat_listShow == 0) {
//                catList!!.clear()
//            }

            //fidooLoaderCancel()

            val productList: ArrayList<Product> = ArrayList()

            //  try {


            if(storeData.address.isNotEmpty()) {
                New_storeitem_search.restaurantAddress = storeData.address.toString()
                New_storeitem_search.fssai = "License no. " + storeData.fssai.toString()
                New_storeitem_search.restaurantName = storeData.store_name.toString()
            }

            Log.d("updateData__",
                New_storeitem_search.restaurantAddress +"-"+ New_storeitem_search.fssai +"-"+ New_storeitem_search.restaurantName
            )


//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            //   dismissIOSProgress()

            if (storeData.error_code==200) {

//                if (pagecount>0){
////                    latestCatList =storeData.subcategory as ArrayList
////                    catList.addAll(latestCatList)
////                    val s: Set<Subcategory> =
////                        LinkedHashSet<Subcategory>(catList)
////                    catList!!.clear()
////                    catList!!.addAll(s)
//
//                }else{
//                    catList=storeData.subcategory as ArrayList
//                    val s: Set<Subcategory> =
//                        LinkedHashSet<Subcategory>(catList)
//                    catList!!.clear()
//                    catList!!.addAll(s)
//                }


                if (storeData.subcategory.isNotEmpty()) {

                    ExecutionModule_ExecutorFactory.executor().execute {
                        for (i in storeData.subcategory.indices) {
                            val categoryData = storeData.subcategory[i]

                            for (j in 0 until storeData.subcategory[i].product.size) {
                                val productData = storeData.subcategory[i].product[j]
                                productList.add(productData)
                                if (storeData.subcategory[i].product[j].is_nonveg.equals("0")){
                                    new_veggielist.add(storeData.subcategory[i])
                                }
                                catList.add(storeData.subcategory[i])
                                NewDBStoreItemsActivity.lastCustomized_str = ""
                                NewDBStoreItemsActivity.product_customize_id = ""
                                var customNamesList_: ArrayList<String>? = ArrayList()

                                if (productData.customize_item.size != 0) {
                                    for (k in 0 until storeData.subcategory[i].product[j].customize_item.size) {
                                        try {
                                            val productData =
                                                storeData.subcategory[i].product[j].customize_item[k]
                                            var lastCustomized = productData.sub_cat_name
                                            customNamesList_!!.add(lastCustomized)
                                            val s: Set<String> =
                                                LinkedHashSet<String>(customNamesList_)
                                            customNamesList_.clear()
                                            customNamesList_.addAll(s)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                    var lastCustomized: String = ""
                                    lastCustomized = customNamesList_.toString()
                                    val regex = "\\[|\\]"
                                    New_storeitem_search.lastCustomized_str = lastCustomized.replace(regex.toRegex(), "")
                                    New_storeitem_search.product_customize_id = "1"
                                }

                                //   Thread{
                                sub_cat_nameStr =
                                    storeData.subcategory[i].subcategory_name.toString()

                                if (j == 0) {
                                    headerActiveorNot = "1"
                                } else {
                                    headerActiveorNot = "0"
                                }

                                //Runnable {

                                restaurantProductsDatabase!!.resProductsDaoAccess()!!
                                    .insertResProducts(
                                        StoreItemProductsEntity(
                                            headerActiveorNot, productData.product_sub_category_id,
                                            sub_cat_nameStr,
                                            productData.cart_quantity,
                                            productData.company_name,
                                            productData.image,
                                            productData.in_out_of_stock_status,
                                            productData.is_customize,
                                            productData.is_customize_quantity,
                                            productData.is_nonveg,
                                            productData.contains_egg,
                                            productData.is_prescription,
                                            productData.offer_price,
                                            productData.price,
                                            productData.product_id,
                                            productData.product_name,
                                            productData.weight,
                                            productData.unit,
                                            productData.cart_id,
                                            NewDBStoreItemsActivity.lastCustomized_str,
                                            NewDBStoreItemsActivity.product_customize_id,
                                            productData.product_desc
                                        )
                                    )

                                // }
                                //    }.start()

                                Log.e("headerActiveorNot__", headerActiveorNot!!)
                                Log.e("custom_catNamesList_api",
                                    New_storeitem_search.lastCustomized_str
                                )

                                if((j== storeData.subcategory[i].product.size) && (i == storeData.subcategory.size) ){
                                    gotdata=1

                                }

                            }



                            if (cat_listShow == 0) {
                                //catList.add(categoryData)
                            }


                        }
                    }
                    pagecount = storeData.start_id


                    //  rvHeaderCategory(catList)
                    Log.e("Product_", productList.size.toString())



                    //ratingValue.text = user.rating
                    //  tv_deliveryTime.text = intent.getStringExtra("delivery_time") + " minutes"

                    if (next_available == 0) {
                        //	Handler(Looper.getMainLooper()).postDelayed({
                        if (isNetworkConnected) {
                            if (SessionTwiclo(this@New_storeitem_search).isLoggedIn) {

                                viewmodel?.getStoreDetailsApiNew(
                                    SessionTwiclo(this@New_storeitem_search).loggedInUserDetail.accountId,
                                    SessionTwiclo(this@New_storeitem_search).loggedInUserDetail.accessToken,
                                    intent.getStringExtra("storeId"),
                                    nonveg_str,
                                    cat_id,
                                    contains_egg, pagecount.toString()
                                )

                            } else {
                                viewmodel?.getStoreDetailsApiNew(
                                    "",
                                    "",
                                    intent.getStringExtra("storeId"),
                                    nonveg_str,
                                    cat_id,
                                    contains_egg, pagecount.toString()
                                )
                            }
                        }

                    }


                    cat_visible = 1


                }

            } else if (storeData.error_code == 101) {
                showAlertDialog(this@New_storeitem_search)
            } else {
                cat_visible = 0


                dismissIOSProgress()
//                val toast =
//                    Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT)
//                toast.show()


            }
        })




        //cartcount responce
        viewmodel?.cartCountResponse?.observe(this) { cartcount ->
            // dismissIOSProgress()
            MainActivity.addCartTempList!!.clear()
            MainActivity.tempProductList!!.clear()
            //Log.d("cartCountResponse___",cartcount.toString())
            var count = cartcount.count
            var price = cartcount.price
            SessionTwiclo(this).storeId = cartcount.store_id
            if (!cartcount.error) {
                if (!count.equals("0")) {
                    cart_count = 1
                    //  cartIcon.setImageResource(R.drawable.cart_icon)
                    //  cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
                    itemQuantity_textstore1.text= count

                    val rounded = price.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

                    totalprice_txtstore.text = "₹ " + rounded
                    cartitemView_LLstore1.visibility = View.VISIBLE
                    if (total_cart_count == 0) {
                        total_cart_count = 1
                        slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
                        cartitemView_LLstore1?.startAnimation(slide_)
                    }
                } else {
                    cart_count = 0
                    itemQuantity_textstore1.text = "0"
                    totalprice_txtstore.text = ""
                    //   cartIcon.setImageResource(R.drawable.ic_cart)
                    //  cartIcon.setColorFilter(Color.argb(255, 199, 199, 199))
                    cartitemView_LLstore1.visibility = View.GONE
                    total_cart_count = 0
                    slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                    cartitemView_LLstore1?.startAnimation(slide_)
                    SessionTwiclo(this).serviceId = ""

                }

            }

        }


        viewmodel?.addRemoveCartResponse?.observe(this) { user ->

            dismissIOSProgress()
            Log.e("addRemoveCartRes____", Gson().toJson(user))
            if (user.errorCode == 200) {
                New_storeitem_search.handleresponce = 1
                try {
                    New_storeitem_search.product_customize_id = user.product_customize_id
                    Thread {
                        updateByCartIdProductCustomized(
                            user.cart_quantity!!.toInt(),
                            user.product_id!!,
                            user.is_customize_quantity!!.toInt(),
                            New_storeitem_search.lastCustomized_str!!,
                            user.cart_id!!,
                            New_storeitem_search.product_customize_id!!
                        )
                    }.start()

                } catch (e: Exception) {
                }


                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getCartCountApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                }
            } else if (user.errorCode == 101) {
                showAlertDialog(this)

            }
            //   getStoreDetailsApiCall()
        }

        viewmodel?.failureResponse?.observe(this) { user ->

            dismissIOSProgress()
            Log.e("cart response", Gson().toJson(user))
            //showToast(user)
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        }

        viewmodel?.addToCartResponse?.observe(this, Observer { user ->

            dismissIOSProgress()
            if (user.errorCode == 200) {
                New_storeitem_search.handleresponce = 1
                Log.e("stores_addResponse____", Gson().toJson(user))
                if (tempType.equals("custom")) {
                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        //searchLay.visibility = View.GONE
                    } else {
                        //searchLay.visibility = View.VISIBLE
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }

                } else {
                    MainActivity.tempProductList!!.clear()
                    MainActivity.addCartTempList!!.clear()
                }
                try {
                    New_storeitem_search.product_customize_id = user.product_customize_id
                    Thread {
                        if (user.cart_quantity.equals("0")) {
                        } else {
                            updateByCartIdProductCustomized(
                                user.cart_quantity!!.toInt(),
                                user.product_id!!,
                                user.is_customize_quantity!!.toInt(),
                                New_storeitem_search.lastCustomized_str!!,
                                user.cart_id!!,
                                user.product_customize_id
                            )
                        }
                    }.start()
                } catch (e: Exception) {
                }


                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            } else if (user.errorCode == 101) {
                showAlertDialog(this)

            }
            // getStoreDetailsApiCall()
        })

        viewmodel?.customizeProductResponse?.observe(this, Observer { user ->

            dismissIOSProgress()
            cartitemView_LLstore1.visibility = View.GONE

            Log.e("stores___esponse", Gson().toJson(user))

            if (user.errorCode == 200) {
                mModelDataTemp = user

                categoryy = ArrayList()

                for (i in 0 until mModelDataTemp?.category?.size!!) {
                    if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
                        try {
                            if (mModelDataTemp?.category?.get(i)!!.isMandatory.equals("0")) {
                            } else {
                                var customListModel: CustomListModel? = CustomListModel()
                                customListModel!!.category =
                                    mModelDataTemp?.category?.get(i)!!.catId
                                try {
                                    customListModel!!.id =
                                        mModelDataTemp?.category?.get(i)!!.subCat[0].id.toInt()
                                    customListModel!!.price =
                                        mModelDataTemp?.category?.get(i)!!.subCat[0].price
                                    customListModel!!.subCatName =
                                        mModelDataTemp?.category?.get(i)!!.subCat[0].subCatName
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
                // Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
            } else if (user.errorCode == 101) {
                showAlertDialog(this)

            }
        })

        viewmodel?.clearCartResponse?.observe(this, Observer { user ->

            dismissIOSProgress()
            if (user.errorCode == 200) {
                Log.e("stores_response", Gson().toJson(user))
                if (tempType.equals("custom")) {

                    viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                    )
                } else {

                    MainActivity.addCartTempList!!.clear()
                    val addCartInputModel = AddCartInputModel()
                    addCartInputModel.productId = cus_itemProductId
                    addCartInputModel.quantity = "1"
                    addCartInputModel.message = "add product"
                    addCartInputModel.customizeSubCatId = customIdsList!!
                    addCartInputModel.isCustomize = "0"
                    MainActivity.addCartTempList!!.add(0, addCartInputModel)

                    viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""

                    )
                }
                //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
            } else if (user.errorCode == 101) {
                showAlertDialog(this)

            }
        })

        return mView
    }


    private fun visibilityView() {

        slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        cartitemView_LL?.startAnimation(slide_)
    }

    private fun catPopUp() {
        clickevent == 1
        selectCategoryDiolog = Dialog(this)
        selectCategoryDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectCategoryDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selectCategoryDiolog?.setContentView(R.layout.select_cat_restaurant_popup)
        selectCategoryDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

//        slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
//        selectCategoryDiolog?.layout_catPopup?.startAnimation(slide_)
        selectCategoryDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        selectCategoryDiolog?.setCanceledOnTouchOutside(true)
        selectCategoryDiolog?.show()
        val outsid_viewRl =
            selectCategoryDiolog?.findViewById<ConstraintLayout>(R.id.outsid_viewRl)
        val txtError = selectCategoryDiolog?.findViewById<TextView>(R.id.txtError)
        viewAll_txt = selectCategoryDiolog?.findViewById<TextView>(R.id.viewAll_txt_)!!
        val dismisspopUp = selectCategoryDiolog?.findViewById<ImageView>(R.id.dismisspopUp)
        catrecyclerView = selectCategoryDiolog?.findViewById(R.id.cat_resRecyclerview)!!

        // catRecyclerview
        txtError?.setOnClickListener(View.OnClickListener {
            selectCategoryDiolog?.dismiss()
        })

        outsid_viewRl?.setOnClickListener(View.OnClickListener {
            selectCategoryDiolog?.dismiss()
        })

        dismisspopUp?.setOnClickListener(View.OnClickListener {
            selectCategoryDiolog?.dismiss()
        })

        if (active_or_not == 0) {
            viewAll_txt.setTextColor(Color.parseColor("#a9a9a9"))
        } else {
            viewAll_txt.setTextColor(Color.parseColor("#000000"))

        }

        viewAll_txt.setOnClickListener(View.OnClickListener {
            cat_id = ""
            active_or_not = 0
            viewAll_txt.setTextColor(Color.parseColor("#a9a9a9"))
            restaurantCategoryAdapter.notifyDataSetChanged()
            selectCategoryDiolog?.dismiss()
            //  showIOSProgress()

            // storeItemsRecyclerview?.smoothScrollToPosition(0)

            //api call here

            getStoreDetailsApiCall()
            visibilityView()

            getRoomData()

        })

        cat_listShow = 1
        if (!isonlyveg) {
            val s: Set<Subcategory> =
                LinkedHashSet<Subcategory>(new_veggielist)
            new_veggielist.clear()
            new_veggielist.addAll(s)


        } else {
            val s: Set<Subcategory> =
                LinkedHashSet<Subcategory>(catList)
            catList.clear()
            catList.addAll(s)
            rvCategory(catList)
        }

    }

    private fun rvCategory(catList: ArrayList<Subcategory>) {
        clickevent = 1
        restaurantCategoryAdapter = NewDbRestaurantCategoryAdapter(
            this,
            catList,
            active_or_not,
            object : NewDbRestaurantCategoryAdapter.CategoryItemClick {
                override fun onItemClick(pos: Int, category: Subcategory) {
                    Log.d("category_id__", category.product_sub_category_id)
                    active_or_not = pos
                    cat_id = category.product_sub_category_id
                    viewAll_txt.setTextColor(Color.parseColor("#000000"))
                    selectCategoryDiolog?.dismiss()
                    totalItem = 600

                    try {
                        for (i in mainlist!!.indices) {
                            if (mainlist!![i].subcategory_name.equals(category.subcategory_name.toString())) {
                                if (clickevent == 1) {
                                    Log.e("product_sub_category_id_", i.toString())
                                    clickevent = 0


                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

//                    deleteRoomDataBase()
//                    getStoreDetailsApiCall()
//                    getRoomData()

                }
            })

        catrecyclerView?.adapter = restaurantCategoryAdapter

    }


    private fun rvStoreItemlisting(productList_: ArrayList<StoreItemProductsEntity>) {

        if (countRes == 0) {
            store_SearchResult.layoutManager = LinearLayoutManager(this)
            store_SearchResult.setHasFixedSize(true)

            storeItemsAdapter = new_storeItem_searchAdapter(
                this,
                this,
                productList_,
                New_storeitem_search.fssai!!,
                intent.getStringExtra("storeName").toString(),
                // restaurantName!!,
                "3.5",
                // restaurantAddress!!,
                intent.getStringExtra("store_location").toString().replace(" ,", ", "),
                this,
                this,
                table_count!!.toInt(),
                storeID
            )

            store_SearchResult.adapter = storeItemsAdapter
            store_SearchResult.layoutManager = manager
            store_SearchResult?.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                        //handleresponce = 0
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    currentItems = manager!!.childCount
                    totalItems = manager!!.itemCount
                    scrollOutItems = manager!!.findFirstVisibleItemPosition()
                    var firstvisibleItem =
                        manager!!.findFirstCompletelyVisibleItemPosition()

                    //	 Log.d("value_gg_", "$dy-$currentItems---$totalItems---$scrollOutItems---$firstvisibleItem--$--"+mainlist!!.get(firstvisibleItem)!!.subcategory_name.toString());

                    if (searchKeyETxtAct.getText().toString()
                            .equals("") || searchKeyETxtAct.getText().toString()
                            .startsWith(" ")
                    ) {
                        try {
                            category_header_.visibility = View.VISIBLE
                            category_header_.text =
                                mainlist!!.get(scrollOutItems + 1)!!.subcategory_name.toString()

                            //Log.d("totalItem___", table_count.toString())

                            try {
                                for (i in catList.indices) {
                                    if (catList[i].subcategory_name.equals(
                                            category_header_.text.toString()
                                        )
                                    ) {
                                        Log.d("totalItem__gg_", "$i--${catList.size}")
                                        active_or_not = i
                                        restaurantCategoryAdapter.activePos(active_or_not)
                                        restaurantCategoryAdapter.notifyDataSetChanged()

                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (dy > 1) {
                            if (isScrolling && (currentItems + scrollOutItems) / 2 == totalItems / 2) {
                                New_storeitem_search.handleresponce = 1
                                Log.d(
                                    "isScrolling__",
                                    "$currentItems-$scrollOutItems-$totalItems"
                                )

                                if (table_count!! > New_storeitem_search.productsListing_Count!!) {
                                    if (isScrolling == true) {
                                        totalItem = totalItem?.plus(100)
                                        New_storeitem_search.handleresponce = 1
                                        //showIOSProgress()
                                        getRoomData()
                                        isScrolling = false
                                    }
                                }
                            }
                        }

                    } else {
                        try {
                            category_header_.visibility = View.VISIBLE
                            category_header_.text =
                                productListFilter!!.get(scrollOutItems + 1)!!.subcategory_name.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

            })

        } else {
            storeItemsAdapter.updateData(productList_, table_count!!)

        }

    }

    private fun vegStoreItemlisting(vegproductList_: ArrayList<StoreItemProductsEntity>) {

        if (countRes == 0) {
            store_SearchResult.layoutManager = LinearLayoutManager(this)
            store_SearchResult.setHasFixedSize(true)

            storeItemsAdapter = new_storeItem_searchAdapter(
                this,
                this,
                vegproductList_,
                New_storeitem_search.fssai!!,
                intent.getStringExtra("storeName").toString(),
                // restaurantName!!,
                "3.5",
                // restaurantAddress!!,
                intent.getStringExtra("store_location").toString().replace(" ,", ", "),
                this,
                this,
                table_count!!.toInt(),
                storeID
            )

            store_SearchResult.adapter = storeItemsAdapter
            store_SearchResult.layoutManager = manager1
            store_SearchResult?.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                        //handleresponce = 0
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    currentItems = manager1!!.childCount
                    totalItems = manager1!!.itemCount
                    scrollOutItems = manager1!!.findFirstVisibleItemPosition()
                    var firstvisibleItem =
                        manager1!!.findFirstCompletelyVisibleItemPosition()

                    //	 Log.d("value_gg_", "$dy-$currentItems---$totalItems---$scrollOutItems---$firstvisibleItem--$--"+mainlist!!.get(firstvisibleItem)!!.subcategory_name.toString());

                    if (searchKeyETxtAct.getText().toString()
                            .equals("") || searchKeyETxtAct.getText().toString()
                            .startsWith(" ")
                    ) {
                        try {
                            category_header_.visibility = View.VISIBLE
                            category_header_.text =
                                veg_item_list!!.get(scrollOutItems + 1)!!.subcategory_name.toString()

                            //Log.d("totalItem___", table_count.toString())

                            try {
                                for (i in new_veggielist.indices) {
                                    if (new_veggielist[i].subcategory_name.equals(
                                            category_header_.getText().toString()
                                        )
                                    ) {
                                        Log.d("totalItem__gg_", "$i--${catList.size}")
                                        active_or_not = i
                                        restaurantCategoryAdapter.activePos(active_or_not)
                                        restaurantCategoryAdapter.notifyDataSetChanged()

                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (dy > 1) {
                            if (isScrolling && (currentItems + scrollOutItems) / 2 == totalItems / 2) {
                                New_storeitem_search.handleresponce = 1
                                Log.d(
                                    "isScrolling__",
                                    "$currentItems-$scrollOutItems-$totalItems"
                                )

                                if (table_count!! > New_storeitem_search.productsListing_Count!!) {
                                    if (isScrolling == true) {
                                        totalItem = totalItem?.plus(100)
                                        New_storeitem_search.handleresponce = 1
                                        //showIOSProgress()

                                        isScrolling = false
                                    }
                                }
                            }
                        }

                    } else {
                        try {
                            category_header_.visibility = View.VISIBLE
                            category_header_.text =
                                productListFilter!!.get(scrollOutItems + 1)!!.subcategory_name.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

            })

        } else {
            storeItemsAdapter.updateData(vegproductList_, table_count!!)

        }

    }


    private fun searchQuery(query: String?) {
        var search_key = "%$query%"
        Log.d("searchData_", search_key.toString())
        Handler(Looper.getMainLooper()).postDelayed(
            {
                restaurantProductsDatabase!!.resProductsDaoAccess()!!
                    .searchQuery(search_key)
                    .observe(this, Observer { search ->
                        if (!query.equals("")) {
                            productListFilter = search as ArrayList<StoreItemProductsEntity>
                            storeItemsAdapter.updateData(
                                productListFilter!!,
                                productListFilter!!.size.toInt()
                            )

                            try {
                                category_header_.text =
                                    productListFilter!!.get(0)!!.subcategory_name.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                category_header_.text = ""
                            }

                        } else {
                            try {
                                category_header_.text =
                                    mainlist!!.get(0)!!.subcategory_name.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                category_header_.text = ""
                            }
                            storeItemsAdapter.updateData(mainlist!!, table_count!!)
                        }
                        storeItemsAdapter?.notifyDataSetChanged()
                        Log.d("searchdata_", search.toString())


                    })

            }, 1
        )


    }

    override fun onStart() {
        super.onStart()
        isonlyveg= false
        productListFilter!!.clear()
        getstoreadapter()
    }

    private fun getstoreadapter(){

        store_SearchResult.layoutManager = LinearLayoutManager(this)
        store_SearchResult.setHasFixedSize(true)

        storeItemsAdapter = new_storeItem_searchAdapter(
            this,
            this,
            productListFilter!!,
            New_storeitem_search.fssai!!,
            intent.getStringExtra("storeName").toString(),
            // restaurantName!!,
            "3.5",
            // restaurantAddress!!,
            intent.getStringExtra("store_location").toString().replace(" ,", ", "),
            this,
            this,
            table_count!!.toInt(),
            storeID
        )

        store_SearchResult.adapter = storeItemsAdapter
        store_SearchResult.layoutManager = manager1
        storeItemsAdapter.updateData(productListFilter!!, productListFilter!!.size)

    }


    //get data from room
    private fun getRoomData() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (searchKeyETxtAct.getText().toString()
                    .equals("") || searchKeyETxtAct.getText()
                    .toString().startsWith(" ")
            ) {

                restaurantProductsDatabase!!.resProductsDaoAccess()!!.getTableCount()
                    .observe(this, { c ->
                        Log.d("table_count", c.toString())
                        table_count = c.toInt()
                    })

                isVegApplied = restaurantProductsDatabase!!.resProductsDaoAccess()!!
                    .getAllProducts2(totalItem.toString())



                lifecycleScope.launchWhenCreated {


                    isVegApplied
                        .collect() {
                            Log.d(
                                "restaurantPrdD",
                                it.size.toString() + "--" + New_storeitem_search.handleresponce
                            )

                            if (New_storeitem_search.handleresponce == 0) {

                                mainlist = it as ArrayList<StoreItemProductsEntity>?
                                val s: Set<StoreItemProductsEntity> =
                                    LinkedHashSet<StoreItemProductsEntity>(mainlist)
                                mainlist!!.clear()

                                mainlist!!.addAll(s)

                                New_storeitem_search.productsListing_Count = mainlist!!.size
//                                if (isonlyveg) {
//                                    Log.d("dudi", "Second: $productsListing_Count")
//                                    rvStoreItemlisting(mainlist!!)
//                                }
                            } else {
                                var productListUpdate: ArrayList<StoreItemProductsEntity> =
                                    ArrayList()
                                productListUpdate = it as ArrayList<StoreItemProductsEntity>
                                mainlist = productListUpdate
                                val s: Set<StoreItemProductsEntity> =
                                    LinkedHashSet<StoreItemProductsEntity>(mainlist)
                                mainlist!!.clear()
                                mainlist!!.addAll(s)

                                New_storeitem_search.productsListing_Count = mainlist!!.size
//                                if (isonlyveg) {
//                                    storeItemsAdapter.updateData(mainlist!!, table_count!!)
//                                }
                            }
                        }
                }

//                if (vegToggle .equals("On") || vegToggle.equals("Off")) {
//                    dismissIOSProgress()
//                }
            } else {
                searchQuery(search_value)
            }
        }, 1)


    }

    //update database
    private fun updateProductS(count: Int, productId: String) {
        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(
                applicationContext,
                RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
            restaurantProductsDatabase!!.resProductsDaoAccess()!!
                .updateProducts(count.toInt(), productId!!)

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
            restaurantProductsDatabase!!.resProductsDaoAccess()!!.updateCustomizeProducts(
                count,
                productId!!,
                customize_quantity!!,
                customizeItemName
            )

        }.start()
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
            restaurantProductsDatabase!!.resProductsDaoAccess()!!.customizeProductUpdate(
                count,
                productId!!,
                customize_quantity!!,
                customizeItemName,
                cart_id,
                product_customize_id
            )

        }.start()
    }

    //delete room data
    private fun deleteRoomDataBase() {
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

    private fun getStoreDetailsApiCall() {
        if (isNetworkConnected) {

            showIOSProgress()

            //fidooLoaderShow()
            category_header_.visibility= View.VISIBLE
            category_header_.text = ""

            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )

                viewmodel?.getStoreDetailsApiNew(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    intent.getStringExtra("storeId"),
                    nonveg_str,
                    cat_id,
                    contains_egg, pagecount.toString()
                )
            } else {
                viewmodel?.getStoreDetailsApiNew(
                    "",
                    "",
                    intent.getStringExtra("storeId"),
                    nonveg_str,
                    cat_id,
                    contains_egg, pagecount.toString()
                )
            }
        } else {
            showInternetToast()
        }
    }

    override fun clearCart() {
        viewmodel?.clearCartApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken
        )
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
            updateProductS(custom_itemCount.toInt(), cus_itemProductId)

        }
        builder.setNegativeButton("No") { dialogInterface, which -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // This lisner call when customize item is going to add
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
        custom_itemCount = count!!.toInt()

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
                //  tempProductId = productId

                showIOSProgress()
                customIdsList!!.clear()
                customNamesList!!.clear()
                if (productId != null) {
                    viewmodel?.customizeProductApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken, productId
                    )
                }
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Your previous customization")
                builder.setMessage(tempCount)
                // builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("I'LL CHOOSE") { _, _ ->
                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                        //searchLay.visibility = View.GONE
                    } else {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        //searchLay.visibility = View.VISIBLE
                    }

                    //  tempProductId = productId
                    showIOSProgress()
                    //customIdsList!!.clear()
                    if (productId != null) {
                        viewmodel?.customizeProductApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken, productId
                        )
                    }
                }

                //performing negative action
                builder.setNegativeButton("REPEAT") { dialogInterface, which -> }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(true)
                alertDialog.show()
            }

        } else {

            if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(
                    this
                ).storeId.equals("")
            ) {
                Log.e("intent store id", intent.getStringExtra("storeId").toString())
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
                Log.e("  store id", SessionTwiclo(this).storeId.toString())

                showIOSProgress()

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
                    cus_itemProductId!!,
                    0,
                    New_storeitem_search.lastCustomized_str!!
                )

            } else {
                clearCartPopup()
            }
        }
    }

    override fun onIdSelected(
        productId: String?,
        type: String?,
        price: String?,
        sub_cat_name: String?,
        tempSelectionCount: Int
    ) {
        //Log.d("")
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

        } else if (type == "unselect") {
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
        customAddBtn.text =
            "Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()

        /*if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED){
    customAddBtn.text = tempOfferPrice
}*/
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
                    tempPriceSubCat_Name =
                        mModelDataTemp?.category!!.get(i).subCat.get(j).subCatName
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
            if (categoryy!!.get(i).category.equals(tempCat)) {
                /*  var customListModel: CustomListModel?= CustomListModel()
          customListModel!!.category= category.get(i).catId
          customListModel!!.id= category.get(i).subCat.get(0).id.toInt()*/
                tempAddEdit = "edit"
                tempAddEditId = i.toString()
                categoryy!!.get(i).id = checkedId!!.toInt()
                categoryy!!.get(i).price = tempPrice
                categoryy!!.get(i).subCatName = tempPriceSubCat_Name
                break
            }
        }

        if (tempAddEdit.equals("edit")) {
            categoryy!!.get(tempAddEditId!!.toInt()).id = checkedId!!.toInt()
            categoryy!!.get(tempAddEditId.toInt()).price = tempPrice
            categoryy!!.get(tempAddEditId.toInt()).subCatName = tempPriceSubCat_Name
        } else {
            var customListModel: CustomListModel? =
                CustomListModel()
            customListModel!!.category = tempCat
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

    override fun onItemAddRemoveClick(
        productId: String?,
        count: String?,
        type: String?,
        price: String?,
        sid: String?,
        cartId: String?,
        position: Int
    ) {
        cus_itemProductId = productId.toString()
        Log.d("count__add", count!! + "=" + cartId + "=" + type)
        Log.d("count__addID", productId!!)
        // showIOSProgress()

        if (type.equals("add")) {
            //  cartIcon.setImageResource(R.drawable.cart_icon)

            if (MainActivity.tempProductList!!.size == 0) {
                //customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()
//                    val tempProductListModel = TempProductListModel()
//                    tempProductListModel.productId = productId
//                    tempProductListModel.quantity = count
//                    tempProductListModel.price = price
//                    tempProductList!!.add(tempProductListModel)

                MainActivity.addCartTempList!!.clear()
                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = productId
                addCartInputModel.quantity = count
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "0"
                MainActivity.addCartTempList!!.add(0, addCartInputModel)



                if (cartId != null) {
                    viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        cartId
                    )
                }
                MainActivity.tempProductList!!.clear()
            } else {
                var check: String = ""
                var tempPos: Int = 0

                for (i in 0 until MainActivity.tempProductList!!.size) {
                    Log.e("check1", MainActivity.tempProductList!!.get(i).productId)
                    Log.e("check2", productId)
                    if (MainActivity.tempProductList!![i].productId.equals(productId)) {
                        check = "edit"
                        tempPos = i
                        //tempProductList!!.get(i).quantity = count
                        break
                    }
                }
                if (check == "edit") {

                    try {
                        if (MainActivity.addCartTempList!!.size != 0) {
                            MainActivity.tempProductList!![tempPos].quantity = count
                            MainActivity.addCartTempList!![tempPos].quantity = count
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                } else {

                    val tempProductListModel = TempProductListModel()
                    tempProductListModel.productId = productId
                    tempProductListModel.quantity = count
                    tempProductListModel.price = price
                    MainActivity.tempProductList!!.add(tempProductListModel)
                    MainActivity.addCartTempList!!.clear()
                    val addCartInputModel = AddCartInputModel()
                    addCartInputModel.productId = productId
                    addCartInputModel.quantity = count
                    addCartInputModel.message = "add product"
                    addCartInputModel.customizeSubCatId = customIdsList!!
                    addCartInputModel.isCustomize = "0"
                    MainActivity.addCartTempList!!.add(0, addCartInputModel)

                }

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    cartId!!.toString()
                )
            }

            updateProductS(count.toInt(), productId)
        } else {
            //  cartIcon.setImageResource(R.drawable.ic_cart)
            var check = "edit"
            var checkPos = 0
            Log.e("check1", Gson().toJson(MainActivity.tempProductList!!))
            for (i in 0 until MainActivity.tempProductList!!.size) {
                if (MainActivity.tempProductList!![i].productId.equals(productId)) {
                    if (count == "0") {
                        check = "remove"
                        checkPos = i
                        break
                    }
                }
            }
            Log.d("checkpos", checkPos.toString())

            if (check == "remove") {
                //   cartIcon.setImageResource(R.drawable.ic_cart)
                //ll_view_cart.visibility = View.GONE // to hide the bottom cart bar if non-customized item quantity becomes zero
                MainActivity.addCartTempList!!.removeAt(checkPos)
                MainActivity.tempProductList!!.removeAt(checkPos)
                customIdsList!!.clear()

                if (cartId != null) {
                    viewmodel?.addRemoveCartDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        productId,
                        "remove",
                        "0",
                        "",
                        cartId,
                        customIdsList!!
                    )
                }


            } else {

                if (MainActivity.tempProductList!!.size != 0) {
                    MainActivity.tempProductList!![checkPos].quantity = count
                    MainActivity.addCartTempList!![checkPos].quantity = count
                }
                customIdsList!!.clear()

                if (cartId != null) {
                    viewmodel?.addRemoveCartDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        productId,
                        "remove",
                        "0",
                        "",
                        cartId,
                        customIdsList!!
                    )
                }

            }

            if (count!!.toInt() == 0) {
                //  product_customize_id
                updateProductCustomized(
                    count!!.toInt(),
                    productId!!,
                    0,
                    New_storeitem_search.lastCustomized_str!!
                )
            } else {
                updateProductS(count!!.toInt(), productId!!)
            }
        }
        //plusMinusPrice = 0.0
        tempPrice = 0.0
        Log.d("check1", Gson().toJson(MainActivity.tempProductList!!))
        var bottomPrice: Double? = 0.0
        var bottomCount: Int? = 0
        for (i in 0 until MainActivity.tempProductList!!.size) {
            bottomPrice =
                bottomPrice!! + (MainActivity.tempProductList!!.get(i).price.toDouble() * MainActivity.tempProductList!!.get(
                    i
                ).quantity.toInt())
            bottomCount = bottomCount!! + MainActivity.tempProductList!!.get(i).quantity.toInt()
        }
        //txt_price_.text = resources.getString(R.string.ruppee) + bottomPrice.toString()
        //cartCountTxt.text = bottomCount.toString()
        //  cartIcon.setImageResource(R.drawable.cart_icon)
        //   cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
        if (bottomCount == 1) {
            //txt_items_.text = bottomCount.toString() + " item"
        } else {
            //txt_items_.text = bottomCount.toString() + " items"
        }

    }


    override fun onAddItemClick(
        quantity: String?,
        productId: String?,
        items: String?,
        offerPrice: String?,
        isCustomize: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    ) {
        New_storeitem_search.product_customize_id = prodcustCustomizeId!!
        tempOfferPrice = offerPrice
        //plusMinusPrice = 0.0
        tempPrice = 0.0
        tempProductId = productId
        cus_itemProductId = productId!!
        custom_itemCount = quantity!!.toInt()

        if (cart_id != null) {
            cartId = cart_id
        }

        Log.d("isCustomize__", isCustomize!! + "\n" + items + "---" + prodcustCustomizeId)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Your previous customization")
        builder.setMessage(items)
        builder.setPositiveButton("I'LL CHOOSE") { _, which ->

            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                //searchLay.visibility = View.GONE
            } else {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                //searchLay.visibility = View.VISIBLE
            }


            showIOSProgress()
            customIdsList!!.clear()
            customNamesList!!.clear()
            viewmodel?.customizeProductApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, productId!!
            )
        }

        //performing negative action
        builder.setNegativeButton("REPEAT") { _, which ->

            showIOSProgress()
            viewmodel?.addRemoveCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                productId!!,
                "add",
                isCustomize!!,
                prodcustCustomizeId!!,
                cart_id!!,
                customIdsList!!
            )
            updateProductS(quantity.toInt(), productId)
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    override fun onRemoveItemClick(
        productId: String?,
        quantity: String?,
        isCustomize: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    ) {
        New_storeitem_search.product_customize_id = prodcustCustomizeId!!
        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))
        } else {
            if (cart_id != null) {
                viewmodel?.addRemoveCartDetails(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    productId!!,
                    "remove",
                    isCustomize!!,
                    prodcustCustomizeId!!,
                    cart_id,
                    customIdsList!!
                )
                if (isCustomize.equals("1")) {
                    updateProductCustomized(
                        quantity!!.toInt(),
                        productId!!,
                        0,
                        New_storeitem_search.lastCustomized_str!!
                    )
                } else {
                    updateProductS(quantity!!.toInt(), productId!!)

                }
            }
        }
    }

    private fun showLoginDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Login") { _, which ->
            sessionTwiclo!!.clearSession()
            startActivity(
                Intent(
                    this,
                    SplashActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )

//            startActivity(
//                Intent(this, LoginActivity::class.java)
//            )
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    override fun onResume() {
        dismissIOSProgress()
        super.onResume()
        dismissIOSProgress()
        storeID = intent.getStringExtra("storeId")!!
        New_storeitem_search.storeIDCheckOnCart = storeID



        Log.d("OnRESUME___", "RESUME" + intent.getStringExtra("delivery_time"))
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (New_storeitem_search.handleresponce == 1) {
            getRoomData()
            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            }
        }
        // getStoreDetailsApiCall()

    }
    private fun putdata_room(){




    }

}