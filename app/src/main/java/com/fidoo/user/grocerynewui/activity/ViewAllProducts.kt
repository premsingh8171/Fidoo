package com.fidoo.user.grocerynewui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocerynewui.adapter.ViewAllSubCatAdapter
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Subcategory
import com.fidoo.user.grocerynewui.onclicklistener.ItemViewOnClickListener
import com.fidoo.user.grocerynewui.viewmodel.GroceryProductsViewModel
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.newsearch.ui.NewSearchActivity
import com.fidoo.user.search.ui.SearchItemActivity
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_grocery_new_ui.*
import kotlinx.android.synthetic.main.activity_view_all_products.*
import kotlinx.android.synthetic.main.activity_view_all_products.backIcon
import kotlinx.android.synthetic.main.activity_view_all_products.cartitemView_LL
import kotlinx.android.synthetic.main.activity_view_all_products.cat_rl
import kotlinx.android.synthetic.main.activity_view_all_products.grocery_constL
import kotlinx.android.synthetic.main.activity_view_all_products.itemQuantity_text
import kotlinx.android.synthetic.main.activity_view_all_products.linear_progress_indicator
import kotlinx.android.synthetic.main.activity_view_all_products.no_internet_grocery
import kotlinx.android.synthetic.main.activity_view_all_products.no_item_foundlll
import kotlinx.android.synthetic.main.activity_view_all_products.search_onDailyNeed
import kotlinx.android.synthetic.main.activity_view_all_products.totalprice_txt
import kotlinx.android.synthetic.main.no_internet_connection.*
import kotlinx.android.synthetic.main.no_item_found.*


@Suppress("DEPRECATION")
class ViewAllProducts : BaseActivity(),
    AdapterAddRemoveClick, ItemViewOnClickListener {
    var viewAllSubCatAdapter: ViewAllSubCatAdapter? = null
    var viewmodel: GroceryProductsViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    lateinit var recyclerView: RecyclerView
    var productList: ArrayList<Product>? = null
    var cat_Diolog: Dialog? = null
    lateinit var catrecyclerView: RecyclerView
    lateinit var viewAll_txt: TextView
    var cat_id: String? = ""
    var subcat_name: String? = ""
    var selectedValue: String? = "default"
    var sub_cat_id: String? = ""
    var customIdsList: ArrayList<String>? = null
    private var slide_: Animation? = null
    var selected_cat: Int = -1
    var selected_Subcat: Int = 0
    var checkStatus: Int = 0

    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var product_count: Int? = 0
        var product_Id: String? = ""
        var onresumeHandle: Int? = 0//api hit handle on resume
        var product_listCount: Int? = 0
        var has_subcategory: Int? = 1

        val SEARCH_RESULT_CODE = 106
        val VIEWALL_RESULT_CODE = 103

        var store_nameStr: String = ""
        var store_ID: String = ""
    }

    //for pagination
    var totalItem: Int? = 20
    var table_count: Int? = 0
    private var manager: GridLayoutManager? = null

    //
    var search_value: String? = ""
    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0
    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempType: String? = ""
    var tempCount: String? = ""
    var count: Int = 1
    var total_cart_count: Int = 0
    var storeID: String? = ""
    var store_id: String? = ""
    var sessionTwiclo: SessionTwiclo? = null
    var height = 0
    var width = 0
    var new_width = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_view_all_products)

        if (intent.getStringExtra("storeId")!!.isNotEmpty()) {
            store_ID = intent.getStringExtra("storeId")!!
            store_id = store_ID
            store_nameStr = intent.getStringExtra("store_name")!!
            store_nameTxt.text = store_nameStr
        }
        viewmodel = ViewModelProviders.of(this).get(GroceryProductsViewModel::class.java)
        viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)
        sessionTwiclo = SessionTwiclo(this)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        // Display size
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = Math.round(width * 0.5).toInt()
        new_width = Math.round((width - 160.0) / 2).toInt()
        height = Math.round(((displayMetrics.heightPixels * 40) / 100).toDouble()).toInt()

        manager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.grocery_sub_cat_rvViewAll)
        try {
            if (!intent.getStringExtra("sub_catId").isNullOrEmpty()) {
                sub_cat_id = intent.getStringExtra("sub_catId")
                Log.d("sub_cat_id_", sub_cat_id!!)

            }

            if (!intent.getStringExtra("store_name").isNullOrEmpty()) {
                store_name.text = intent.getStringExtra("store_name")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        MainActivity.tempProductList = ArrayList()
        MainActivity.addCartTempList = ArrayList()

        customIdsList = ArrayList()
        productList = ArrayList()
        productList!!.clear()

        rvProductList(productList!!)

        //Here we have called Api of getGroceryProducts
        if (isNetworkConnected) {

            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getGroceryProductsFun(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    store_id, cat_id, sub_cat_id
                )

                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            } else {
                viewmodel?.getGroceryProductsFun(
                    "",
                    "",
                    store_id, cat_id, sub_cat_id
                )
            }

            linear_progress_indicator.visibility = View.VISIBLE
            grocery_constL.visibility = View.VISIBLE
            no_internet_grocery.visibility = View.GONE
            no_internet_Ll.visibility = View.GONE

            viewmodelusertrack?.customerActivityLog(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).mobileno,
                "Grocery ViewAll Product Screen",
                SplashActivity.appversion,
                StoreListActivity.serive_id_,
                SessionTwiclo(this).deviceToken
            )
        } else {
            grocery_constL.visibility = View.GONE
            cartitemView_LL.visibility = View.GONE
            linear_progress_indicator.visibility = View.GONE
            no_internet_grocery.visibility = View.VISIBLE
            no_internet_Ll.visibility = View.VISIBLE
            dismissIOSProgress()
        }

        retry_onRefresh.setOnClickListener {
            if (isNetworkConnected) {
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getGroceryProductsFun(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        store_id, cat_id, sub_cat_id
                    )

                    viewmodel?.getCartCountApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                    viewmodelusertrack?.customerActivityLog(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).mobileno,
                        "Grocery ViewAll Product Screen",
                        SplashActivity.appversion,
                        StoreListActivity.serive_id_,
                        SessionTwiclo(this).deviceToken
                    )
                } else {
                    viewmodel?.getGroceryProductsFun(
                        "",
                        "",
                        store_id, cat_id, sub_cat_id
                    )
                }
                //api hit
                linear_progress_indicator.visibility = View.VISIBLE
                linear_progress_indicator.visibility = View.VISIBLE
                grocery_constL.visibility = View.VISIBLE
                no_internet_grocery.visibility = View.GONE
                no_internet_Ll.visibility = View.GONE
            } else {
                grocery_constL.visibility = View.GONE
                cartitemView_LL.visibility = View.GONE
                linear_progress_indicator.visibility = View.GONE
                no_internet_grocery.visibility = View.VISIBLE
                dismissIOSProgress()
            }
        }

        cartitemView_LL.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(
                    Intent(this, CartActivity::class.java).putExtra(
                        "store_id", SessionTwiclo(
                            this
                        ).storeId
                    )
                )
            } else {
                showLoginDialog("Please login to proceed")
            }

        }

        //Here we have got api response from observer
        viewmodel?.groceryProductsResponse?.observe(this, { grocery ->
            linear_progress_indicator.visibility = View.GONE
            MainActivity.tempProductList!!.clear()
            MainActivity.addCartTempList!!.clear()
            productList!!.clear()
            dismissIOSProgress()
            if (grocery.category.isNotEmpty()) {
                if (!grocery.error) {
                    Log.e("Grocery", Gson().toJson(grocery))
                    has_subcategory = grocery.has_subcategory
                    if (has_subcategory == 0) {
                        cat_rl.visibility = View.GONE
                    } else {
                        cat_rl.visibility = View.VISIBLE
                    }
                    if (grocery.category.size != 0) {
                        for (i in grocery.category.indices) {
                            val catObj = grocery.category[i]
                            for (j in 0 until grocery.category[i].subcategory.size) {
                                val subCatObj = catObj.subcategory[j]
                                if (grocery.category[i].subcategory[j].product.size != 0) {
                                    for (k in 0 until grocery.category[i].subcategory[j].product.size) {
                                        grocery_sub_tvViewall.text = subCatObj.subcategory_name

                                        val productListObj = subCatObj.product[k]
                                        //Log.e("Product", Gson().toJson(subCatObj.product[0]))
                                        productList!!.add(productListObj)
                                    }
                                }
                            }
                            if (GroceryNewUiActivity.product_valueUpdate == 0) {
                                rvProductList(productList!!)
                            } else {
                                viewAllSubCatAdapter!!.updateData(productList!!)
                            }

                        }

                        no_item_foundll.visibility = View.GONE
                        no_item_foundlll.visibility = View.GONE
                    } else {
                        no_item_foundll.visibility = View.VISIBLE
                        no_item_foundlll.visibility = View.VISIBLE
                    }
                }
            } else {
                val toast =
                    Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT)
                toast.show()
            }
        })

        viewmodel?.failureResponse?.observe(this, {
            dismissIOSProgress()
        })
        //end observer

        //for clear store item
        viewmodel?.clearCartResponse?.observe(this, { user ->
            // dismissIOSProgress()
            Log.e("clearCartResponse", Gson().toJson(user))
            if (tempType.equals("custom")) {

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    ""
                )
            } else {
                dismissIOSProgress()

                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = product_Id
                addCartInputModel.quantity = product_count.toString()
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
        })

        //first time add item
        viewmodel?.addToCartResponse?.observe(this, { user ->
            Log.e("addToCartResponse__", Gson().toJson(user))
            MainActivity.addCartTempList!!.clear()
            checkStatus = 1
            viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
            onresumeHandle = 1
        })

        //for plus and minus of item
        viewmodel?.addRemoveCartResponse?.observe(this, { user ->

            Log.e("cart_response", Gson().toJson(user))
            checkStatus = 1
            if (isNetworkConnected) {
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )

                onresumeHandle = 1
                //api hit
            } else {
                showInternetToast()
            }
        })

        //cartcount responce
        viewmodel?.cartCountResponse?.observe(this, { cartcount ->
            var count = cartcount.count
            var price = cartcount.price
            if (!cartcount.error) {
                if (count != "0") {
                    itemQuantity_text.text = count
                    totalprice_txt.text = "₹ " + price
                    cartitemView_LL.visibility = View.VISIBLE
                    if (total_cart_count == 0) {
                        total_cart_count = 1
                        slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
                        cartitemView_LL?.startAnimation(slide_)
                    }
                } else {
                    SessionTwiclo(this@ViewAllProducts).storeId = ""
                    cartitemView_LL.visibility = View.GONE
                    total_cart_count = 0
                    slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                    cartitemView_LL?.startAnimation(slide_)
                }
            }

        })

        backIcon.setOnClickListener {
            if (checkStatus == 0) {
                AppUtils.finishActivityLeftToRight(this);
            } else {
                checkStatus = 0
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                AppUtils.finishActivityLeftToRight(this);


            }
        }

        if (intent.getStringExtra("storeId")!!.isNotEmpty()) {
            search_onDailyNeed.setOnClickListener {
                AppUtils.startActivityForResultRightToLeft(
                    this,
                    Intent(this, NewSearchActivity::class.java)
                        .putExtra("storeId", intent.getStringExtra("storeId")),
                    SEARCH_RESULT_CODE
                )
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("search_result_coe", "$requestCode - ${SEARCH_RESULT_CODE} - $resultCode")

        if (requestCode == SEARCH_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                GroceryNewUiActivity.product_valueUpdate = 1
                showIOSProgress()
                viewmodel?.getGroceryProductsApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    intent.getStringExtra("storeId"), cat_id, sub_cat_id
                )

            }
        }
    }

    private fun rvProductList(productList: ArrayList<Product>) {
        viewAllSubCatAdapter = ViewAllSubCatAdapter(this, productList, this, store_ID, width)
        grocery_sub_cat_rvViewAll?.adapter = viewAllSubCatAdapter
    }


    //all click event here
    override fun onItemAddRemoveClick(
        productId: String?,
        count: String?,
        type: String?,
        price: String?,
        storeId: String?,
        cartId: String?,
        position: Int
    ) {

        Log.d("countcount", count!!)
        Log.d("ID__", productId!!)
        product_count = count.toInt()
        product_Id = productId
        tempType = type
        if (type.equals("Replace")) {
            SessionTwiclo(this).storeId = storeId
            Log.d("storeIdstoreId__", storeId!!)

        } else if (type.equals("add")) {

            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = productId
            addCartInputModel.quantity = product_count.toString()
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
        } else {
            viewmodel?.addRemoveCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                productId,
                "remove",
                "0",
                "",
                "",
                customIdsList!!
            )
        }
    }

    private fun showLoginDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Login") { _, _ ->
            sessionTwiclo!!.clearSession()
            startActivity(
                Intent(
                    this,
                    SplashActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        builder.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    override fun clearCart() {
        showIOSProgress()
        SessionTwiclo(this).storeId = storeID
        viewmodel?.clearCartApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken
        )
    }


    override fun onBackPressed() {
        if (checkStatus == 0) {
            AppUtils.finishActivityLeftToRight(this);
        } else {
            checkStatus = 0
            val returnIntent = Intent()
            setResult(RESULT_OK, returnIntent)
            AppUtils.finishActivityLeftToRight(this);


        }
    }

    //first item add item to cart
    override fun addtoCart(
        main_position: Int,
        productModel: Product,
        pos: Int, store_id: String,
        item_count: String, type: String
    ) {
        if (sessionTwiclo!!.isLoggedIn) {
            if (isNetworkConnected) {
                productList?.get(pos)!!.cart_quantity = item_count.toInt()
                viewAllSubCatAdapter!!.notifyDataSetChanged()
                if (type.equals("add")) {
                    val addCartInputModel = AddCartInputModel()
                    addCartInputModel.productId = productModel.product_id
                    addCartInputModel.quantity = item_count
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

                } else {
                    clearCart()
                    SessionTwiclo(this).storeId = store_id
                }

            } else {
                showInternetToast()
            }
        } else {
            if (isNetworkConnected) {
                //do work

            } else {
                showInternetToast()
            }
        }

    }

    //item plus item to cart
    override fun plusItemCart(
        main_position: Int,
        productModel: Product,
        pos: Int,
        store_id: String,
        item_count: String, type: String
    ) {
        if (sessionTwiclo!!.isLoggedIn) {
            if (isNetworkConnected) {
                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = productModel.product_id
                addCartInputModel.quantity = item_count
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
                productList?.get(pos)!!.cart_quantity = item_count.toInt()
                viewAllSubCatAdapter!!.notifyDataSetChanged()
            } else {
                showInternetToast()
            }

        } else {
            if (isNetworkConnected) {
                //do work
            } else {
                showInternetToast()
            }
        }

    }


    // item minus item to cart
    override fun minusItemCart(
        main_position: Int,
        productModel: Product,
        pos: Int,
        store_id: String,
        item_count: String, type: String
    ) {
        if (sessionTwiclo!!.isLoggedIn) {
            if (isNetworkConnected) {
                productList?.get(pos)!!.cart_quantity = item_count.toInt()
                viewAllSubCatAdapter!!.notifyDataSetChanged()

                viewmodel?.addRemoveCartDetails(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    productModel.product_id,
                    "remove",
                    "0",
                    "",
                    "",
                    customIdsList!!
                )
            } else {
                showInternetToast()
            }
        } else {
            if (isNetworkConnected) {
                //do work

            } else {
                showInternetToast()
            }
        }

    }

    override fun onClickViewAll(
        main_position: Int,
        model: Subcategory,
        pos: Int,
        store_id: String,
        item_count: String,
        type: String
    ) {

    }

    override fun onResume() {
        super.onResume()

        if (GroceryNewUiActivity.product_valueUpdate == 1) {
            showIOSProgress()
            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getGroceryProductsApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    intent.getStringExtra("storeId"), cat_id, sub_cat_id
                )
            } else {
                viewmodel?.getGroceryProductsApi(
                    "",
                    "",
                    intent.getStringExtra("storeId"), cat_id, sub_cat_id
                )
            }
        }

        viewmodel?.getCartCountApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken
        )
    }
}