package com.fidoo.user.grocery.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.addCartTempList
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.adapter.GroceryCategoryAdapter
import com.fidoo.user.grocery.adapter.GroceryItemAdapter
import com.fidoo.user.grocery.adapter.GrocerySubItemAdapter
import com.fidoo.user.grocery.model.getGroceryProducts.Category
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.model.getGroceryProducts.Subcategory
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase
import com.fidoo.user.grocery.viewmodel.GroceryProductsViewModel
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.google.android.gms.maps.GoogleMap
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.activity_grocery_items.backIcon
import kotlinx.android.synthetic.main.activity_grocery_items.linear_progress_indicator
import kotlinx.android.synthetic.main.activity_search_item.*
import kotlinx.android.synthetic.main.activity_store_items.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import kotlinx.android.synthetic.main.no_item_found.*
import kotlinx.android.synthetic.main.select_cat_popup.*


@Suppress("DEPRECATION")
class GroceryItemsActivity : BaseActivity(), AdapterClick,
    AdapterAddRemoveClick,
    AdapterCartAddRemoveClick {
    var viewmodel: GroceryProductsViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    lateinit var recyclerView: RecyclerView
    val catList: ArrayList<Category> = ArrayList()
    var productList: ArrayList<Product>? = null
    var productListFilter: ArrayList<Product>? = null
    val subcatList: ArrayList<Subcategory> = ArrayList()
    var catListFilter: ArrayList<Category> = ArrayList()
    var subcatListFilter: ArrayList<Subcategory> = ArrayList()
    var cat_Diolog: Dialog? = null
    lateinit var catrecyclerView: RecyclerView
    lateinit var  viewAll_txt: TextView
    var cat_id: String? = ""
    var subcat_name: String? = ""
    var selectedValue: String? = "default"
    var sub_cat_id: String? = ""
    var customIdsList: ArrayList<String>? = null
    private lateinit var groceryItemAdapter: GroceryItemAdapter
    private var slide_: Animation? = null
    var selected_cat:Int=-1
    var selected_Subcat:Int=0
    private var mMixpanel: MixpanelAPI? = null
    companion object {
        var itemPosition: Int? = 0
        var product_count: Int? = 0
        var product_Id: String? = ""
        var viewAll: Int? = 0
        var multipleclick: Int? = 0
        var onresumeHandle: Int? = 0
        var product_listCount: Int? = 0
        var has_subcategory: Int? = 1
    }

    //for pagination
    var totalItem: Int? = 20
    var table_count: Int? = 0

    private var manager: GridLayoutManager? = null
    private var currentItems = 0
    private  var totalItems:Int = 0
    private  var scrollOutItems:Int = 0
    private var isScrolling = false
    var search_value: String? = ""
    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0
    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempType: String? = ""
    var tempCount: String? = ""
    var count: Int = 1
    var total_cart_count: Int = 0
    private lateinit var mMap: GoogleMap
    var storeID: String? = null
    var sessionTwiclo: SessionTwiclo?=null

    //roomdb
    private lateinit var productsDatabase: ProductsDatabase
   // private lateinit var dao: ProductsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_grocery_items)
        viewmodel = ViewModelProviders.of(this).get(GroceryProductsViewModel::class.java)
        viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)
        sessionTwiclo = SessionTwiclo(this)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        //    grocery_item_rv
        Thread{
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }.start()

        manager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.grocery_item_rv)
        var store_id = intent.getStringExtra("storeId")
        Log.d("store_id___",store_id.toString())
        store_name.text = intent.getStringExtra("store_name")

        storeID=store_id
        MainActivity.tempProductList = ArrayList()
        MainActivity.addCartTempList = ArrayList()

        customIdsList = ArrayList()
        productList = ArrayList()
        productList!!.clear()

        cartitemView_LL.setOnClickListener {

            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(
                    Intent(this, CartActivity::class.java).putExtra("store_id", SessionTwiclo(this).storeId)
                )
            } else {
                showLoginDialog("Please login to proceed")
            }

        }

        //Here we have called Api of getGroceryProducts
        if (isNetworkConnected) {
            deleteRoomDataBase()
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
            getRoomData()
            linear_progress_indicator.visibility=View.VISIBLE
            grocery_constL.visibility=View.VISIBLE
            cat_rlFloat.visibility=View.VISIBLE
            no_internet_grocery.visibility=View.GONE
            no_internet_Ll.visibility=View.GONE
            viewmodelusertrack?.customerActivityLog(SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).mobileno,"Grocery Screen",
                SplashActivity.appversion, StoreListActivity.serive_id_,SessionTwiclo(this).deviceToken
            )
        }else{
            grocery_constL.visibility=View.GONE
            cartitemView_LL.visibility=View.GONE
            cat_rlFloat.visibility=View.GONE
            linear_progress_indicator.visibility=View.GONE
            no_internet_grocery.visibility=View.VISIBLE
            no_internet_Ll.visibility=View.VISIBLE
            dismissIOSProgress()
        }

        retry_onRefresh.setOnClickListener {
            if (isNetworkConnected) {
                deleteRoomDataBase()
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

                    viewmodelusertrack?.customerActivityLog(SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).mobileno,"Grocery Screen",
                        SplashActivity.appversion, StoreListActivity.serive_id_,SessionTwiclo(this).deviceToken
                    )

                } else {
                    viewmodel?.getGroceryProductsFun(
                        "",
                        "",
                        store_id, cat_id, sub_cat_id
                    )
                }
                getRoomData()
                linear_progress_indicator.visibility=View.VISIBLE
                linear_progress_indicator.visibility=View.VISIBLE
                grocery_constL.visibility=View.VISIBLE
                cat_rlFloat.visibility=View.VISIBLE
                no_internet_grocery.visibility=View.GONE
                no_internet_Ll.visibility=View.GONE
            }else{
                grocery_constL.visibility=View.GONE
                cartitemView_LL.visibility=View.GONE
                cat_rlFloat.visibility=View.GONE
                linear_progress_indicator.visibility=View.GONE
                no_internet_grocery.visibility=View.VISIBLE
                dismissIOSProgress()
            }
        }

        //to get data from database
        getRoomData()
        rvlistProduct(productList!!)


        //Here we have got api response from observer
        viewmodel?.GroceryProductsResponse?.observe(this, { grocery ->
            linear_progress_indicator.visibility = View.GONE
            MainActivity.tempProductList!!.clear()
            MainActivity.addCartTempList!!.clear()
            catList.clear()
            subcatList.clear()
            productList!!.clear()

            if (grocery.category.isNotEmpty()) {
                if (!grocery.error) {
                    Log.e("Grocery", Gson().toJson(grocery))
                    // val subcatList: ArrayList<Subcategory> = ArrayList()
                    has_subcategory=grocery.has_subcategory

                    if (has_subcategory==0){
                        cat_rl.visibility=View.GONE
                    }else{
                        cat_rl.visibility=View.VISIBLE
                    }

                    val productList: ArrayList<Product> = ArrayList()
                    if (grocery.category.size!=0) {
                        for (i in grocery.category.indices) {
                            val catObj = grocery.category[i]
                            // tv_categories.text = "Select category"

                            for (j in 0 until grocery.category[i].subcategory.size) {
                                val subCatObj = catObj.subcategory[j]

                                if (grocery.category[i].subcategory[j].product.size != 0) {
                                    for (k in 0 until grocery.category[i].subcategory[j].product.size) {
                                        val productListObj = subCatObj.product[k]
                                        Log.e("Product", Gson().toJson(subCatObj.product[0]))
                                        productList.add(productListObj)
                                        Thread {
//                                        Runnable {
                                            productsDatabase!!.productsDaoAccess()!!.insertProducts(
                                                Product(
                                                    productListObj.cart_quantity,
                                                    productListObj.company_name,
                                                    productListObj.image,
                                                    productListObj.in_out_of_stock_status,
                                                    productListObj.is_customize,
                                                    productListObj.is_customize_quantity,
                                                    productListObj.is_nonveg,
                                                    productListObj.is_prescription,
                                                    productListObj.offer_price,
                                                    productListObj.price,
                                                    productListObj.product_id,
                                                    productListObj.product_name,
                                                    productListObj.unit,
                                                    productListObj.weight,
                                                    productListObj.cart_id,
                                                    productListObj.product_sub_category_id,
                                                    productListObj.product_category_id
                                                )
                                            )
                                            //}
                                        }.start()
                                    }
                                } else {

//                                val toast = Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT)
//                                toast.show()

                                }

                                if (!subCatObj.subcategory_name.equals("")) {
                                    subcatList.add(subCatObj)
                                }
                            }

                            rvlistSubcategory(subcatList)

                            catList.add(catObj)
                        }

                        no_item_foundll.visibility=View.GONE
                        no_item_foundlll.visibility=View.GONE
                    }else{
                        no_item_foundll.visibility=View.VISIBLE
                        no_item_foundlll.visibility=View.VISIBLE
                    }

                   // Log.d("kb___", "" + productList.size.toString())
                    //rvlistSubcategory(subcatList)
                    //rvlistProduct(productList)

                }
            }else{
                val toast = Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT)
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
                addCartTempList!!.add(0,addCartInputModel)

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
            //dismissIOSProgress()
            Log.e("addToCartResponse__", Gson().toJson(user))
            val mModelData: com.fidoo.user.data.model.AddToCartModel = user
            addCartTempList!!.clear()

            viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
            onresumeHandle=1
            getRoomData()
        })

        //for plus and minus of item
        viewmodel?.addRemoveCartResponse?.observe(this, { user ->

            Log.e("cart_response", Gson().toJson(user))
            if (isNetworkConnected) {
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )

                onresumeHandle=1
                getRoomData()
            } else {
                showInternetToast()
            }
        })

        //cartcount responce
        viewmodel?.cartCountResponse?.observe(this,{cartcount->
            dismissIOSProgress()

            Log.d("cartCountResponse___",cartcount.toString())
            Log.d("cartCountResponse_c__",cartcount.count.toString())
            var count = cartcount.count
            var price = cartcount.price
            if (!cartcount.error){
                if (count!="0"){
                    itemQuantity_text.text=count
                    totalprice_txt.text= "₹ "+price
                    cartitemView_LL.visibility= View.VISIBLE
                    if (total_cart_count==0) {
                        total_cart_count = 1
                        slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
                        cartitemView_LL?.startAnimation(slide_)
                    }
                }else{
                    SessionTwiclo(this@GroceryItemsActivity).storeId=""
                    cartitemView_LL.visibility=View.GONE
                    total_cart_count = 0
                    slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                    cartitemView_LL?.startAnimation(slide_)
                }
            }

        })

        cat_rlFloat.setOnClickListener {
            if(multipleclick==0){
                multipleclick=1
                catPopUp()
            }
            val handler = Handler()
            handler.postDelayed(Runnable {
                multipleclick=0
            }, 600)

        }

        backIcon.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this);
        }

        searchPrdEt?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                search_value=s.toString()
                if (productList!!.isEmpty()) {
                    return;
                }
                //productListFilter= filterProducts(productList!!, search_value!!) as ArrayList<Product>
               // groceryItemAdapter.setFilter(productListFilter!!)
               // groceryItemAdapter?.notifyDataSetChanged()

           //searchquery
                searchQuery(search_value)

            }
        })

        active_dotLLall.setBackgroundResource(R.drawable.bg_green_roundborder)
        grocery_sub_tvall.setTextColor(Color.parseColor("#ffffff"))

        active_dotLLall.setOnClickListener {
            selectedValue=""
            active_dotLLall.setBackgroundResource(R.drawable.bg_green_roundborder)
            grocery_sub_tvall.setTextColor(Color.parseColor("#ffffff"))
            rvlistSubcategory(subcatList)
//            cat_id = ""
            selected_Subcat=0
            subcat_name = ""
            sub_cat_id=""
            itemPosition = 0
            totalItem=20
            showIOSProgress()
            deleteRoomDataBase()
            viewmodel?.getGroceryProductsFun(
                SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accountId,
                SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accessToken,
                storeID,cat_id,sub_cat_id
            )
            searchPrdEt.getText()!!.clear()
            getRoomData()

        }
    }

    //search query get data
    private fun searchQuery(query:String?) {
        var search_key="%$query%"
        Log.d("searchdata_",search_key.toString())
        Handler().postDelayed(
            {
                productsDatabase!!.productsDaoAccess()!!.searchQuery(search_key).observe(this, Observer {search->
                  if (!query.equals("")) {
                      productListFilter = search as ArrayList<Product>
                      groceryItemAdapter.setFilter(productListFilter!!)
                  }else {
                      groceryItemAdapter.setFilter(productList!!)
                  }
                    groceryItemAdapter?.notifyDataSetChanged()
                    Log.d("searchdata_",search.toString())

                })
            },
            10
        )
    }

    //get roomdatabase
    private fun getRoomData() {
        Handler().postDelayed(
            {
                if (searchPrdEt.getText().toString().equals("") || searchPrdEt.getText().toString().startsWith(" ")) {

                    productsDatabase!!.productsDaoAccess()!!.getTableCount().observe(this, { c ->
                        Log.d("table_count", c.toString())
                        table_count = c.toInt()
                    })

                    productsDatabase!!.productsDaoAccess()!!.getAllProducts2(totalItem.toString())
                        .observe(this, Observer { t ->
                            if (onresumeHandle == 0) {
                                productList = t as ArrayList<Product>?
                                product_listCount = productList!!.size
                                Log.d("roomdatabase_", product_listCount.toString())
                                rvlistProduct(productList!!)
                                dismissIOSProgress()
                            } else {
                                var productListUpdate: ArrayList<Product> = ArrayList()
                                productListUpdate = t as ArrayList<Product>
                                productList = productListUpdate
                                product_listCount = productListUpdate!!.size
                                groceryItemAdapter.setFilter(productList!!)
                            }
                            // onresumeHandle=0
                            dismissIOSProgress()
                        })
                    dismissIOSProgress()
                }else{
                    searchQuery(search_value)
                }

            },
            10
        )
    }

    //delete room data
    private fun deleteRoomDataBase() {
        Thread{
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            productsDatabase!!.productsDaoAccess()!!.deleteAll()

        }.start()
    }

    //update database
    private fun updateProductS(count: Int,productId:String) {
        Thread{
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            productsDatabase!!.productsDaoAccess()!!.updateProducts(count.toInt(),productId!!)

        }.start()
    }

    private fun catPopUp() {
        cat_Diolog = Dialog(this)
        cat_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        cat_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cat_Diolog?.setContentView(R.layout.select_cat_popup)
        cat_Diolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        cat_Diolog?.layout_catPopup?.startAnimation(slide_)
        // cat_Diolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        cat_Diolog?.setCanceledOnTouchOutside(true)
        cat_Diolog?.show()
        val txtError = cat_Diolog?.findViewById<TextView>(R.id.txtError)
        viewAll_txt = cat_Diolog?.findViewById<TextView>(R.id.viewAll_txt)!!
        val dismisspopUp = cat_Diolog?.findViewById<RelativeLayout>(R.id.dismisspopUp)
        catrecyclerView = cat_Diolog?.findViewById(R.id.catRecyclerview)!!

        // catRecyclerview
        txtError?.setOnClickListener(View.OnClickListener {
            slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
            cat_Diolog?.layout_catPopup?.startAnimation(slide_)
            cat_Diolog?.dismiss()
        })

        dismisspopUp?.setOnClickListener(View.OnClickListener {
            slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
            cat_Diolog?.layout_catPopup?.startAnimation(slide_)
            cat_Diolog?.dismiss()

        })
        if (selected_cat==-1){
            viewAll_txt.setTextColor(Color.parseColor("#a9a9a9"))
        }else{
            viewAll_txt.setTextColor(Color.parseColor("#000000"))
        }

        viewAll_txt?.setOnClickListener(View.OnClickListener {
            cat_Diolog?.dismiss()
            selected_cat=-1
            cat_id = ""
            subcat_name = ""
            sub_cat_id=""
            itemPosition = 0
            viewAll = 1
            tv_categories.text = "Select category"


            showIOSProgress()
            deleteRoomDataBase()
            viewmodel?.getGroceryProductsFun(
                SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accountId,
                SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accessToken,
                storeID,cat_id,sub_cat_id
            )
            searchPrdEt.getText()!!.clear()
            getRoomData()
        })

        rvCategory(catList)

    }

    //For Products list
    private fun rvlistProduct(listProduct: ArrayList<Product>) {
        productList=listProduct
    //  var store_id = intent.getStringExtra("storeId")

        groceryItemAdapter = GroceryItemAdapter(
            this,
            productList!!,
            this,
            this,
            0,
            storeID!!, ""
        )

        grocery_item_rv?.adapter = groceryItemAdapter
        grocery_item_rv.layoutManager = manager
        grocery_item_rv?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                    onresumeHandle=0
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
               if (searchPrdEt.getText().toString().equals("") || searchPrdEt.getText().toString().startsWith(" ")) {
                   currentItems = manager!!.childCount
                   totalItems = manager!!.itemCount
                   scrollOutItems = manager!!.findFirstVisibleItemPosition()
                   Log.d("value_gg_", "$dy-$currentItems---$totalItems---$scrollOutItems");

                   if (dy > 1) {
                       if (isScrolling && currentItems + scrollOutItems == totalItems) {
                           Log.d("totalItem___", table_count.toString() + "---" + product_listCount)
                           if (table_count!! > product_listCount!!) {
                               if (isScrolling == true) {
                                   totalItem = totalItem?.plus(20)
                                   onresumeHandle = 1
                                   showIOSProgress()
                                   getRoomData()
                                   isScrolling = false
                               }
                           }
                       }
                   }
               }
            }
        })

        Log.d("itemPosition_value", itemPosition!!.toString())
        if (viewAll == 1) {
            grocery_item_rv.smoothScrollToPosition(viewAll!!)
        } else {
            //itemPosition = itemPosition?.plus(1)
            grocery_item_rv.smoothScrollToPosition(itemPosition!!)
        }

    }

    //For SubCategory list Showing on top of view tab like
    private fun rvlistSubcategory(subcatList: ArrayList<Subcategory>) {
        sub_cat_rv.adapter = GrocerySubItemAdapter(
            this,
            subcatList,
            object : GrocerySubItemAdapter.SubcategoryItemClick {
                override fun onItemClick(pos: Int, subgrocery: Subcategory) {
                    itemPosition = 0
                    viewAll = 0
                    subcat_name = subgrocery.subcategory_name
                    selectedValue=subcat_name
                    sub_cat_id = subgrocery.sub_cat_id
                    selected_Subcat=pos
                    totalItem=20
                    showIOSProgress()
                    active_dotLLall.setBackgroundResource(R.drawable.black_full_rounded_empty)
                    grocery_sub_tvall.setTextColor(Color.parseColor("#818181"))
                    showIOSProgress()
                    deleteRoomDataBase()
                    viewmodel?.getGroceryProductsFun(
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accountId,
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accessToken,
                        storeID,cat_id,sub_cat_id
                    )
                    searchPrdEt.getText()!!.clear()
                    getRoomData()
                }
            },selectedValue)
        grocery_item_rv.layoutManager = manager
        sub_cat_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dx == dy) {
                    sub_cat_rv.smoothScrollToPosition(selected_Subcat)
                 //   manager?.scrollToPositionWithOffset(selected_Subcat, -10)

                }
            }
        })


    }

    //For Category list on showing popup
    private fun rvCategory(catList: ArrayList<Category>) {
        catrecyclerView.adapter = GroceryCategoryAdapter(
            this,
            selected_cat,
            catList,
            object : GroceryCategoryAdapter.CategoryItemClick {

                override fun onItemClick(pos: Int, grocery: Category) {
                    Log.d("grocery___", grocery.cat_id)
                    tv_categories.text = grocery.cat_name
                    slide_ = AnimationUtils.loadAnimation(this@GroceryItemsActivity, R.anim.slide_in_left)
                    cat_Diolog?.layout_catPopup?.startAnimation(slide_)
                    cat_Diolog?.dismiss()
                    viewAll_txt.setTextColor(Color.parseColor("#000000"))
                    selected_cat= pos
                    cat_id = grocery.cat_id
                    itemPosition = 0
                    viewAll = 0
                    sub_cat_id=""
                    totalItem=20
                    deleteRoomDataBase()
                    showIOSProgress()
                    viewmodel?.getGroceryProductsFun(
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accountId,
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accessToken,
                        storeID,cat_id,sub_cat_id
                    )
                    searchPrdEt.getText()!!.clear()
                    getRoomData()
                    //   Log.d("subcategoryAdapter__", pos.toString())
                    //  sub_cat_rv.adapter = subcategoryAdapter
                    //  subcategoryAdapter?.updateReceiptsList(pos)
                    //sub_cat_rv?.smoothScrollToPosition(pos)
                }
            })
    }

    //Filter data showing by category
    private fun filterListShowing(pos: Int, grocery: Category) {

        if (cat_id != null) {
            catListFilter = filter(catList, cat_id!!) as ArrayList<Category>
            val subcatListF: ArrayList<Subcategory> = ArrayList()
            val productListF: ArrayList<Product> = ArrayList()

            for (i in 0 until catListFilter.size) {
                var catObj = catListFilter[i]
                for (j in 0 until catListFilter[i].subcategory.size) {
                    var subCatObj = catListFilter[i].subcategory[j]

                    for (k in 0 until catListFilter[i].subcategory[j].product.size) {
                        var productListObj = catListFilter[i].subcategory[j].product[k]
                        productListF.add(productListObj)

                    }

                    subcatListF.add(subCatObj)
                }

                catListFilter.add(catObj)
            }
            rvlistSubcategory(subcatListF)
            rvlistProduct(productListF)
            grocery_item_rv.adapter!!.notifyDataSetChanged()
        } else {
            if (productList != null) {
                rvlistProduct(productList!!)
                rvlistSubcategory(subcatList)

            }
            grocery_item_rv.adapter!!.notifyDataSetChanged()
            sub_cat_rv.adapter!!.notifyDataSetChanged()
        }
    }

    //Filter data showing by Subcategory
    private fun filterListShowingSub(pos: Int, subgrocery: Subcategory) {
        Log.d("pos___pos",pos.toString())
        if (subcat_name != "") {
            subcatListFilter = filterSubCategory(subcatList, subcat_name!!) as ArrayList<Subcategory>
            val subcatListF: ArrayList<Subcategory> = ArrayList()
            for (j in 0 until subcatListFilter.size) {
                var subCatObj = subcatListFilter[j]
                val productListF: ArrayList<Product> = ArrayList()

                for (k in 0 until subcatListFilter[j].product.size) {
                    var productListObj = subcatListFilter[j].product[k]
                    productListF.add(productListObj)
                }
                subcatListF.add(subCatObj)
                rvlistProduct(productListF)
            }
            grocery_item_rv.adapter!!.notifyDataSetChanged()
        } else {
            if (productList != null) {
                rvlistProduct(productList!!)
            }
            grocery_item_rv.adapter!!.notifyDataSetChanged()
        }
    }

    //filter Item
    private fun filter(models: ArrayList<Category>, query: String): MutableList<Category> {
        var query = query
        query = query.toLowerCase()
        val filterList: MutableList<Category> = ArrayList<Category>()
        for (model in models) {
            val text: String = model.cat_id.toLowerCase()
            if (text.contains(query)) {
                filterList.add(model)
            }
        }
        return filterList
    }

    //filter Item of products
    private fun filterProducts(models: ArrayList<Product>, query: String): MutableList<Product> {
        var query = query
        query = query.toLowerCase()
        val filterList: MutableList<Product> = ArrayList<Product>()
        for (model in models) {
            val text: String = model.product_name.toLowerCase()
            if (text.contains(query)) {
                filterList.add(model)
            }
        }
        return filterList
    }

    //filter Item by subcategory
    private fun filterSubCategory(models: ArrayList<Subcategory>, query: String): MutableList<Subcategory> {
        var query = query
        query = query.toLowerCase()
        val filterList: MutableList<Subcategory> = ArrayList<Subcategory>()
        for (model in models) {
            val text: String = model.subcategory_name.toLowerCase()
            if (text.contains(query)) {
                filterList.add(model)
            }
        }
        return filterList
    }

    override fun onItemClick(productId: String?, type: String?, count: String?, offerPrice: String?, customize_count: Int?, productType: String?, cart_id: String?) {
//        Log.d("onItemClick_count", count!!)
//
//        tempType = type
//        tempCount = count
//        this.count = count.toInt()
//        tempProductId = productId
//        mCustomizeCount = customize_count
//        tempOfferPrice = offerPrice
//        countValue.text = tempCount
//
//        showIOSProgress()
//
//
//        if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(this).storeId.equals("")
//        ) {
//            Log.e("intent store id", intent.getStringExtra("storeId").toString())
//            SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
//            Log.e("  store id", SessionTwiclo(this).storeId.toString())
//            showIOSProgress()
//
//            viewmodel!!.addToCartApi(
//                SessionTwiclo(this).loggedInUserDetail.accountId,
//                SessionTwiclo(this).loggedInUserDetail.accessToken,
//                MainActivity.addCartTempList!!,
//                ""
//            )
//            SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
//        } else {
//            clearCartPopup()
//        }

    }


    //all click event here
    override fun onItemAddRemoveClick(productId: String?, count: String?, type: String?, price: String?, storeId: String?, cartId: String?, position: Int) {

        Log.d("countcount", count!!)
        Log.d("ID__", productId!!)
        product_count = count.toInt()
        product_Id=productId
        itemPosition = position
        viewAll = 0

        tempType=type
        if (type.equals("Replace")){
            SessionTwiclo(this).storeId=storeId
            Log.d("storeIdstoreId__",storeId!!)

        }else if (type.equals("add")) {

            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = productId
            addCartInputModel.quantity = product_count.toString()
            addCartInputModel.message = "add product"
            addCartInputModel.customizeSubCatId = customIdsList!!
            addCartInputModel.isCustomize = "0"
            addCartTempList!!.add(0,addCartInputModel)

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
        updateProductS(count.toInt(),productId!!)

    }

    override fun onAddItemClick(productId: String?, items: String?, offerPrice: String?, customizeid: String?, prodcustCustomizeId: String?, cart_id: String?) {}

    override fun onRemoveItemClick(productId: String?, quantity: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?
    ) {}

    private fun showLoginDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Login") { _, _ ->
            sessionTwiclo!!.clearSession()
            startActivity(Intent(this, AuthActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        builder.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    override fun clearCart() {
        showIOSProgress()
        SessionTwiclo(this).storeId=storeID
        viewmodel?.clearCartApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken
        )
    }

    override fun onResume() {
        super.onResume()

        if (onresumeHandle!=0) {
            if (isNetworkConnected) {
                showIOSProgress()
                itemPosition=0
//                if (SessionTwiclo(this).isLoggedIn) {
//                    //Here we have called Api of getGroceryProducts
//                    viewmodel?.getGroceryProductsFun(
//                        SessionTwiclo(this).loggedInUserDetail.accountId,
//                        SessionTwiclo(this).loggedInUserDetail.accessToken,
//                        intent.getStringExtra("storeId"),cat_id,sub_cat_id
//                    )
//                } else {
//                    viewmodel?.getGroceryProductsFun(
//                        SessionTwiclo(this).loggedInUserDetail.accountId,
//                        SessionTwiclo(this).loggedInUserDetail.accessToken,
//                        intent.getStringExtra("storeId"),cat_id,sub_cat_id
//                    )
//                }

                getRoomData()

                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )


            } else {
              dismissIOSProgress()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.finishActivityLeftToRight(this);
    }


}