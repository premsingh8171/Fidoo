package com.fidoo.user.restaurants.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.CartActivity
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.adapter.StoreCustomItemsAdapter
import com.fidoo.user.adapter.StoreItemsAdapter
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.activity.GroceryItemsActivity
import com.fidoo.user.interfaces.*
import com.fidoo.user.restaurants.adapter.RestaurantCategoryAdapter
import com.fidoo.user.restaurants.model.CustomCheckBoxModel
import com.fidoo.user.restaurants.model.CustomListModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.restaurants.model.StoreDetailsModel
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.ui.MainActivity.Companion.addCartTempList
import com.fidoo.user.ui.MainActivity.Companion.tempProductList
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.viewmodels.CartViewModel
import com.fidoo.user.restaurants.viewmodel.StoreDetailsViewModel
import com.fidoo.user.viewmodels.TrackViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.activity_store_items.*
import kotlinx.android.synthetic.main.activity_store_items.backIcon
import kotlinx.android.synthetic.main.activity_store_items.linear_progress_indicator
import kotlinx.android.synthetic.main.activity_store_items.tv_deliveryTime
import kotlinx.android.synthetic.main.activity_store_items.tv_location
import kotlinx.android.synthetic.main.activity_store_items.tv_store_name
import kotlinx.android.synthetic.main.select_cat_popup.*
import java.lang.NumberFormatException

class StoreItemsActivity :
    BaseActivity(),
    AdapterClick,
    CustomCartAddRemoveClick,
    AdapterCustomRadioClick,
    AdapterAddRemoveClick,
    AdapterCartAddRemoveClick {

    private var categoryy: ArrayList<CustomListModel>? = null
    private var mModelDataTemp: CustomizeProductResponseModel? = null
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
    var cartViewModel: CartViewModel? = null //Un used
    var viewmodel: StoreDetailsViewModel? = null
    var distanceViewModel: TrackViewModel? = null//Un used
    var customIdsList: ArrayList<String>? = null
    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0
    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempType: String? = ""
    var tempCount: String? = ""
    var count: Int = 1
    var countRes: Int = 0
    var veg: Int = 0
    var nonveg: Int = 0
    var nonveg_str: String = ""
    var contains_egg: String = ""
    private lateinit var mMap: GoogleMap
    var cartId: String = ""
    lateinit var storeID : String
    var cat_listShow: Int = 0
    companion object {

        var customerLatitude: String = ""
        var customerLongitude: String = ""
    }

    var veg_filter: Int = 0
    var egg_filter: Int = 0
    var cat_visible: Int = 0
    lateinit var storeItemsAdapter:StoreItemsAdapter
    lateinit var restaurantCategoryAdapter:RestaurantCategoryAdapter

    var selectCategoryDiolog: Dialog? = null
    private var slide_: Animation? = null
    lateinit var catrecyclerView: RecyclerView
    lateinit var viewAll_txt:TextView
    var cat_id: String? = ""
    var active_or_not:Int=-1
    val catList: ArrayList<StoreDetailsModel.Category> = ArrayList()


    //roomdb
    private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_store_items)

        distanceViewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        behavior = BottomSheetBehavior.from(bottom_sheet)

        tempProductList = ArrayList()
        addCartTempList = ArrayList()
        customIdsList = ArrayList()
        customIdsListTemp = ArrayList()

        customerLatitude = ""
        customerLongitude = ""
        
        Thread{
            restaurantProductsDatabase = Room.databaseBuilder(
                applicationContext,
                 RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()

        }.start()

        getRoomData()

        viewmodel = ViewModelProviders.of(this).get(StoreDetailsViewModel::class.java)
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        tv_location.text = intent.getStringExtra("store_location")

        storeID = intent.getStringExtra("storeId")!!

        cartitemView_LLstore.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(Intent(this, CartActivity::class.java).putExtra("store_id", SessionTwiclo(this).storeId)) } else {
                showLoginDialog("Please login to proceed")

            }

        }

        cartIcon.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(Intent(this, CartActivity::class.java).putExtra("store_id", SessionTwiclo(this).storeId)) } else {
                showLoginDialog("Please login to proceed")
                Log.e("STORE ID",SessionTwiclo(this).storeId)

            }

        }

        backIcon.setOnClickListener {
            AppUtils.finishActivityLeftToRight(this)
        }

        cat_FloatBtn.setOnClickListener {
           try {
               if (cat_visible==1) {
                   catPopUp()
               }
           }catch (e:Exception){
               e.printStackTrace()
           }

        }

        customAddBtn.setOnClickListener {
            Log.e("addCartTempList", Gson().toJson(addCartTempList))

            for (i in 0 until categoryy!!.size) {
                customIdsList!!.add(categoryy!![i].id.toString())
            }
            Log.e("customIdsList", customIdsList.toString())

            addCartTempList!!.clear()
            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = tempProductId
            addCartInputModel.quantity = countValue.text.toString()
            addCartInputModel.message = "add product"
            addCartInputModel.customizeSubCatId = customIdsList!!
            addCartInputModel.isCustomize = "1"
            addCartTempList!!.add(addCartInputModel)

            if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(this).storeId.equals("")) {
                showIOSProgress()
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")


                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    addCartTempList!!,
                    cartId
                )
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED


            } else {
                clearCartPopup()
            }
        }

        veg_switch_img.setOnClickListener {
            if (veg_filter==0){
                veg_switch_img.setImageResource(R.drawable.filter_on)
               // egg_switch_img.setImageResource(R.drawable.filter_off)
              //  egg_filter=0
                nonveg_str="0"
                getStoreDetailsApiCall()
                veg_filter=1

            }else{
                veg_switch_img.setImageResource(R.drawable.filter_off)
                nonveg_str=""
                getStoreDetailsApiCall()
                veg_filter=0

            }

        }

        egg_switch_img.setOnClickListener {
            if (egg_filter==0){
                egg_switch_img.setImageResource(R.drawable.filter_on)
               // veg_switch_img.setImageResource(R.drawable.filter_off)
                contains_egg="1"
                getStoreDetailsApiCall()
                egg_filter=1

            }else{
                egg_switch_img.setImageResource(R.drawable.filter_off)
                contains_egg=""
                getStoreDetailsApiCall()
                egg_filter=0

            }
        }

        //Default behaviour of Bottom Sheet
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        tv_store_name.text = intent.getStringExtra("storeName")
        //storeID = intent.getStringExtra("storeId")!!

        transLay.setOnClickListener {
            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                //searchLay.visibility = View.GONE
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                //searchLay.visibility = View.VISIBLE
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                cartitemView_LLstore.visibility = View.VISIBLE

            }

        }

        if (isNetworkConnected) {
            getStoreDetailsApiCall()
        } else {
            showInternetToast()
        }

        //cartcount responce
        viewmodel?.cartCountResponse?.observe(this,{cartcount->
            dismissIOSProgress()
            addCartTempList!!.clear()
            tempProductList!!.clear()
            //Log.d("cartCountResponse___",cartcount.toString())
            var count = cartcount.count
            var price = cartcount.price
            SessionTwiclo(this).storeId = cartcount.store_id
            if (!cartcount.error){
                if (count!="0"){
                    cartIcon.setImageResource(R.drawable.cart_icon)
                    cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
                    itemQuantity_textstore.text=count
                    totalprice_txtstore.text= "₹ "+price
                    cartitemView_LLstore.visibility=View.VISIBLE
                    slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
                    cartitemView_LLstore?.startAnimation(slide_)
                }else{
                    cartIcon.setImageResource(R.drawable.ic_cart)
                    cartIcon.setColorFilter(Color.argb(255, 199, 199, 199))
                    cartitemView_LLstore.visibility=View.GONE
                    slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                    cartitemView_LLstore?.startAnimation(slide_)
                }
            }

        })

        viewmodel?.getStoreDetailsApi?.observe(this, Observer { storeData ->
            dismissIOSProgress()
            linear_progress_indicator.visibility = View.GONE
            tempProductList!!.clear()
            addCartTempList!!.clear()
            if (cat_listShow==0){
                catList!!.clear()
            }
            Log.e("stor_edetails_res", Gson().toJson(storeData))

            val productList: ArrayList<StoreDetailsModel.Product> = ArrayList()
          //  val catList: ArrayList<StoreDetailsModel.Category> = ArrayList()


            if (storeData.category.size != 0) {

                for (i in 0 until storeData.category.size) {
                    val categoryData = storeData.category[i]

                    for (j in 0 until storeData.category[i].product.size) {
                        val productData = storeData.category[i].product[j]
                        productList.add(productData)
                        Log.e("Product_", j.toString())

//                        Thread{
//                            //Runnable {
//                            restaurantProductsDatabase!!.resProductsDaoAccess()!!.insertResProducts(
//                                StoreItemProductsEntity(productData.cartQuantity,
//                                    productData.companyName,
//                                    productData.image,
//                                    productData.in_out_of_stock_status,
//                                    productData.isCustomize,
//                                    productData.is_customize_quantity,
//                                    productData.isNonveg,
//                                    productData.isPrescription,
//                                    productData.offerPrice,
//                                    productData.price,
//                                    productData.productId,
//                                    productData.productName,
//                                    productData.weight,
//                                    productData.unit,
//                                    productData.cartId,
//                                    productData.customizeItem.toString(),
//                                )
//                            )
//                           // }
//                        }.start()
                    }

                    if (cat_listShow==0){
                        catList.add(categoryData)
                    }


                }

                rvStoreItemlisting(productList)



                if (storeData.fssai != null) {
                    if (!storeData.fssai.equals("")) {
                        //fssaiTxt.text = "Fssai: " + user.fssai
                    }
                }
                //ratingValue.text = user.rating
                tv_deliveryTime.text = intent.getStringExtra("delivery_time") + " minutes"

                if (storeData.categoryId != null) {
                    if (storeData.categoryId.equals("5")) {
                        //vegOnlySwitch.visibility = View.VISIBLE
                    } else {
                        //vegOnlySwitch.visibility = View.GONE
                    }
                }
                 cat_visible=1

            }else{
                cat_visible=0
                productList.clear()!!
                rvStoreItemlisting(productList)
                val toast = Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT)
                toast.show()
            }
        })

        viewmodel?.addRemoveCartResponse?.observe(this, { user ->
            dismissIOSProgress()
            Log.e("addRemoveCartResponse____", Gson().toJson(user))
            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            }

            getStoreDetailsApiCall()

        })

        viewmodel?.failureResponse?.observe(this, { user ->
            dismissIOSProgress()
            Log.e("cart response", Gson().toJson(user))
            //showToast(user)
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.addToCartResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("stores_addresponse", Gson().toJson(user))
            val mModelData: com.fidoo.user.data.model.AddToCartModel = user
            if (tempType.equals("custom")) {
                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    //searchLay.visibility = View.GONE
                } else {
                    //searchLay.visibility = View.VISIBLE
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }

            } else {
                tempProductList!!.clear()
                addCartTempList!!.clear()

            }
            viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
            getStoreDetailsApiCall()
        })

        viewmodel?.customizeProductResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            cartitemView_LLstore.visibility = View.GONE

            Log.e("stores___esponse", Gson().toJson(user))
            mModelDataTemp = user

            categoryy = ArrayList()

            for (i in 0 until mModelDataTemp?.category?.size!! ) {
                if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
                    var customListModel: CustomListModel? =
                        CustomListModel()
                    customListModel!!.category = mModelDataTemp?.category?.get(i)!!.catId
                    customListModel.id = mModelDataTemp?.category?.get(i)!!.subCat[0].id.toInt()
                    customListModel.price = mModelDataTemp?.category?.get(i)!!.subCat[0].price
                    categoryy!!.add(customListModel)
                } else {

                }
            }

            tempPrice = 0.0

            //var tempPrice: Double? = 0.0
            for (i in 0 until customIdsListTemp!!.size ) {
                tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
            }

            for (i in 0 until categoryy!!.size ) {
                if (categoryy!!.get(i).price != null) {
                    tempPrice = tempPrice!! + categoryy!![i].price.toDouble()
                }
            }
            Log.e("tempPrice", tempPrice.toString())

            try {
                tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
            }catch (e: NumberFormatException){
                Log.e("Exception", e.toString())
            }


            //tempPrice = tempPrice!! + plusMinusPrice
            //showToast(tempPrice.toString())
            Log.e("tempPriceTotal", tempPrice.toString())

            customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()

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
        })

        viewmodel?.clearCartResponse?.observe(this, Observer { user ->
            dismissIOSProgress()

            Log.e("stores response", Gson().toJson(user))
            if (tempType.equals("custom")) {

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    addCartTempList!!,
                    ""
                )
            } else {
                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    addCartTempList!!,
                    ""

                )
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

    }



    private fun catPopUp() {
        selectCategoryDiolog = Dialog(this)
        selectCategoryDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectCategoryDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selectCategoryDiolog?.setContentView(R.layout.select_cat_restaurant_popup)
        selectCategoryDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        selectCategoryDiolog?.layout_catPopup?.startAnimation(slide_)
      //selectCategoryDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        selectCategoryDiolog?.setCanceledOnTouchOutside(true)
        selectCategoryDiolog?.show()
        val outsid_viewRl = selectCategoryDiolog?.findViewById<RelativeLayout>(R.id.outsid_viewRl)
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

        if (active_or_not==-1){
            viewAll_txt.setTextColor(Color.parseColor("#339347"))
        }else{
            viewAll_txt.setTextColor(Color.parseColor("#000000"))

        }
        viewAll_txt?.setOnClickListener(View.OnClickListener {
            cat_id = ""
            active_or_not=-1
            viewAll_txt.setTextColor(Color.parseColor("#339347"))
            restaurantCategoryAdapter.notifyDataSetChanged()
            selectCategoryDiolog?.dismiss()

            //  showIOSProgress()
            //api call here
            getStoreDetailsApiCall()
        })
        cat_listShow=1
       rvCategory(catList)

    }

    private fun rvCategory(catList: ArrayList<StoreDetailsModel.Category>) {
        restaurantCategoryAdapter = RestaurantCategoryAdapter(this,catList,active_or_not,object :RestaurantCategoryAdapter.CategoryItemClick{
            override fun onItemClick(pos: Int, category: StoreDetailsModel.Category) {
                Log.d("category_id__",category.catId)
                active_or_not=pos
                cat_id=category.catId
                viewAll_txt.setTextColor(Color.parseColor("#000000"))
                selectCategoryDiolog?.dismiss()
                getStoreDetailsApiCall()
            }

        })
        catrecyclerView?.adapter=restaurantCategoryAdapter

    }

    private fun rvStoreItemlisting(productList_: ArrayList<StoreDetailsModel.Product>) {
        if(countRes==0) {
            storeItemsRecyclerview.layoutManager = LinearLayoutManager(this)
            storeItemsRecyclerview.setHasFixedSize(true)
            storeItemsAdapter= StoreItemsAdapter(
                this,
                this,
                productList_,
                catList,
                "",
                "",
                "3.5",
                "5",
                this,
                this,
                0,
                storeID,
                ""
            )
            storeItemsRecyclerview.adapter = storeItemsAdapter
            //countRes=1
        }else{
            storeItemsAdapter.updateData(productList_)
        }

    }

    private fun getRoomData() {
        Handler().postDelayed(
            {
                restaurantProductsDatabase!!.resProductsDaoAccess()!!.getAllProducts2("10").observe(this, Observer {t ->

                    Log.d("restaurantProductsDatabase",t.size.toString())
                })
                dismissIOSProgress()

            },
            10
        )
    }

    //delete room data
    private fun deleteRoomDataBase() {
            Thread{
                restaurantProductsDatabase = Room.databaseBuilder(
                    applicationContext,
                    RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
               // restaurantProductsDatabase!!.resProductsDaoAccess()!!.deleteAll()

            }.start()

    }

    private fun getStoreDetailsApiCall() {
        if (isNetworkConnected) {
            showIOSProgress()
            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getStoreDetails(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    intent.getStringExtra("storeId"),
                    nonveg_str,
                    cat_id,
                    contains_egg
                )
            } else {
                viewmodel?.getStoreDetails(
                    "",
                    "",
                    intent.getStringExtra("storeId"),
                    nonveg_str,
                    cat_id,
                    contains_egg
                )
            }
        }else{
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
        //set title for alert dialog
        builder.setTitle("Replace cart item!")
        //set message for alert dialog
        builder.setMessage("Do you want to discard the previous selection?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { _, _ ->
            viewmodel?.clearCartApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
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
                if (productId != null) {
                    viewmodel?.customizeProductApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken, productId
                    )
                }
            } else {
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle("Your previous customization")
                //set message for alert dialog
                builder.setMessage(tempCount)
                // builder.setIcon(android.R.drawable.ic_dialog_alert)

                //performing positive action
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
                builder.setNegativeButton("REPEAT") { dialogInterface, which ->
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(true)
                alertDialog.show()
            }


        } else {

            if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(this).storeId.equals("")
            ) {
                Log.e("intent store id", intent.getStringExtra("storeId").toString())
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
                Log.e("  store id", SessionTwiclo(this).storeId.toString())
                showIOSProgress()

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    addCartTempList!!,
                    ""
                )
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
            } else {
                clearCartPopup()
            }
        }
    }

    override fun onIdSelected(
        productId: String?,
        type: String?,
        price: String?,
        tempSelectionCount: Int
    ) {
        if (type == "select") {
            if (productId != null) {
                customIdsList!!.add(productId)
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
        customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()

        /*if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED){
            customAddBtn.text = tempOfferPrice
        }*/

    }

    override fun onCustomRadioClick(checkedId: String?, position: String?) {
        var tempCat: String? = ""
        var tempPrice: String? = ""
        for (i in 0 until mModelDataTemp?.category!!.size) {
            for (j in 0 until mModelDataTemp?.category!!.get(i).subCat.size ) {
                if (checkedId == mModelDataTemp?.category!!.get(i).subCat.get(j).id) {
                    tempCat = mModelDataTemp?.category!!.get(i).catId
                    tempPrice = mModelDataTemp?.category!!.get(i).subCat.get(j).price
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
                break
            }
        }
        if (tempAddEdit.equals("edit")) {
            categoryy!!.get(tempAddEditId!!.toInt()).id = checkedId!!.toInt()
            categoryy!!.get(tempAddEditId.toInt()).price = tempPrice
        } else {
            var customListModel: CustomListModel? =
                CustomListModel()
            customListModel!!.category = tempCat
            customListModel.id = checkedId!!.toInt()
            customListModel.price = tempPrice
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

        Log.d("count", count!!)
        Log.d("ID", productId!!)

        showIOSProgress()
        //SessionTwiclo(this).storeId = intent.getStringExtra("storeId")


        if (type.equals("add")) {
            cartIcon.setImageResource(R.drawable.cart_icon)

            if (tempProductList!!.size == 0) {
                //customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()
                val tempProductListModel = TempProductListModel()
                tempProductListModel.productId = productId
                tempProductListModel.quantity = count
                tempProductListModel.price = price
                tempProductList!!.add(tempProductListModel)

                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = productId
                addCartInputModel.quantity = count
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "0"
                addCartTempList!!.add(addCartInputModel)


                if (cartId != null) {
                    viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        addCartTempList!!,
                        ""
                    )
                }
                tempProductList!!.clear()
            } else {
                var check: String = ""
                var tempPos: Int = 0

                for (i in 0 until tempProductList!!.size) {
                    Log.e("check1", tempProductList!!.get(i).productId)
                    Log.e("check2", productId)
                    if (tempProductList!![i].productId.equals(productId)) {
                        check = "edit"
                        tempPos = i
                        //tempProductList!!.get(i).quantity = count
                        break
                    }
                }
                if (check == "edit") {

                    try {
                        if (addCartTempList!!.size!=0) {
                            tempProductList!![tempPos].quantity = count
                            addCartTempList!![tempPos].quantity = count
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }


                } else {
                    val tempProductListModel = TempProductListModel()
                    tempProductListModel.productId = productId
                    tempProductListModel.quantity = count
                    tempProductListModel.price = price
                    tempProductList!!.add(tempProductListModel)

                    val addCartInputModel = AddCartInputModel()
                    addCartInputModel.productId = productId
                    addCartInputModel.quantity = count
                    addCartInputModel.message = "add product"
                    addCartInputModel.customizeSubCatId = customIdsList!!
                    addCartInputModel.isCustomize = "0"
                    addCartTempList!!.add(addCartInputModel)
                }

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    addCartTempList!!,
                    ""
                )
            }


        } else {
            cartIcon.setImageResource(R.drawable.ic_cart)
            var check = "edit"
            var checkPos = 0
            Log.e("check1", Gson().toJson(tempProductList!!))
            for (i in 0 until tempProductList!!.size) {
                if (tempProductList!![i].productId.equals(productId)) {
                    if (count == "0") {
                        check = "remove"
                        checkPos = i
                        break
                    }
                }
            }
            Log.d("checkpos", checkPos.toString())
            if (check == "remove") {
                cartIcon.setImageResource(R.drawable.ic_cart)
                //ll_view_cart.visibility = View.GONE // to hide the bottom cart bar if non-customized item quantity becomes zero
                addCartTempList!!.removeAt(checkPos)
                tempProductList!!.removeAt(checkPos)
                //customIdsList!!.clear()

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
                if (tempProductList!!.size!=0) {
                    tempProductList!![checkPos].quantity = count
                    addCartTempList!![checkPos].quantity = count
                }

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

        }
        //plusMinusPrice = 0.0
        tempPrice = 0.0
        Log.d("check1", Gson().toJson(tempProductList!!))
        var bottomPrice: Double? = 0.0
        var bottomCount: Int? = 0
        for (i in 0 until tempProductList!!.size) {
            bottomPrice = bottomPrice!! + (tempProductList!!.get(i).price.toDouble() * tempProductList!!.get(i).quantity.toInt())
            bottomCount = bottomCount!! + tempProductList!!.get(i).quantity.toInt()
        }
        //txt_price_.text = resources.getString(R.string.ruppee) + bottomPrice.toString()
        //cartCountTxt.text = bottomCount.toString()
        cartIcon.setImageResource(R.drawable.cart_icon)
        cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
        if (bottomCount == 1) {
            //txt_items_.text = bottomCount.toString() + " item"
        } else {
            //txt_items_.text = bottomCount.toString() + " items"
        }


    }

    override fun onAddItemClick(
        productId: String?,
        items: String?,
        offerPrice: String?,
        isCustomize: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    ) {
        tempOfferPrice = offerPrice
        //plusMinusPrice = 0.0
        tempPrice = 0.0
        tempProductId = productId

        if (cart_id != null) {
            cartId = cart_id
        }

        Log.d("isCustomize__",isCustomize!!+"\n"+items)

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

            //tempProductId = productId
            showIOSProgress()
            customIdsList!!.clear()
            viewmodel?.customizeProductApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, productId!!
            )
            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
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
        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))

        } else {
            //showIOSProgress()
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
            }
        }
    }

    private fun showLoginDialog(message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Login") { _, which ->
            startActivity(
                Intent(this, LoginActivity::class.java)
            )


        }
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        storeID = intent.getStringExtra("storeId")!!
        tv_cuisnes.text = intent.getStringExtra("cuisine_types")
        tv_distance.text = intent.getStringExtra("distance") + "kms"

        Log.d("OnRESUME", "RESUME")
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (SessionTwiclo(this).isLoggedIn) {
            viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
        }


        getStoreDetailsApiCall()

    }

    override fun onBackPressed() {
        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            super.onBackPressed()
        } else {
            //searchLay.visibility = View.VISIBLE
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

        }
        AppUtils.finishActivityLeftToRight(this)
    }

}