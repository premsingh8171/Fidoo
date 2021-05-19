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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.CartActivity
import com.fidoo.user.R
import com.fidoo.user.SplashActivity
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.adapter.GroceryCategoryAdapter
import com.fidoo.user.grocery.adapter.GroceryItemAdapter
import com.fidoo.user.grocery.adapter.GrocerySubItemAdapter
import com.fidoo.user.grocery.model.getGroceryProducts.Category
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.model.getGroceryProducts.Subcategory
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase
import com.fidoo.user.grocery.roomdatabase.database.ProductsEntitiy
import com.fidoo.user.grocery.viewmodel.GroceryProductsViewModel
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.ui.MainActivity.Companion.addCartTempList
import com.fidoo.user.utils.BaseActivity
import com.google.android.gms.maps.GoogleMap
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.activity_grocery_items.backIcon
import kotlinx.android.synthetic.main.activity_grocery_items.linear_progress_indicator
import kotlinx.android.synthetic.main.activity_search_item.*
import kotlinx.android.synthetic.main.activity_store_items.*
import kotlinx.android.synthetic.main.select_cat_popup.*


class GroceryItemsActivity : BaseActivity(), AdapterClick,
    AdapterAddRemoveClick,
    AdapterCartAddRemoveClick {
    var viewmodel: GroceryProductsViewModel? = null

    lateinit var recyclerView: RecyclerView
    val catList: ArrayList<Category> = ArrayList()
    var productList: ArrayList<Product>? = null
    var productListFilter: ArrayList<Product>? = null
    val subcatList: ArrayList<Subcategory> = ArrayList()
    var catListFilter: ArrayList<Category> = ArrayList()
    var subcatListFilter: ArrayList<Subcategory> = ArrayList()
    var selectAreaDiolog: Dialog? = null
    lateinit var catrecyclerView: RecyclerView
    var cat_id: String? = ""
    var subcat_name: String? = ""
    var selectedValue: String? = "default"
    var sub_cat_id: String? = ""

    var customIdsList: ArrayList<String>? = null
    private lateinit var groceryItemAdapter: GroceryItemAdapter
    private var slide_: Animation? = null

    companion object {
        var itemPosition: Int? = 0
        var product_count: Int? = 0
        var viewAll: Int? = 0
        var multipleclick: Int? = 0
        var onresumeHandle: Int? = 0

    }

    private var layoutManger: LinearLayoutManager? = null

    var search_value: String? = ""
    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0
    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempType: String? = ""
    var tempCount: String? = ""
    var count: Int = 1
    private lateinit var mMap: GoogleMap
    var storeID: String? = null
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

        Thread{
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()

        }.start()


        layoutManger = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.grocery_item_rv)
        val store_id = intent.getStringExtra("storeId")
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

        //Here we have called Api of getGroceryProducts
        if (SessionTwiclo(this).isLoggedIn){
            viewmodel?.getGroceryProductsFun(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                store_id,cat_id,sub_cat_id
            )

            viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
        }else{
            viewmodel?.getGroceryProductsFun(
                "",
                "",
                store_id,cat_id,sub_cat_id
            )
        }

        //to get data from database
       getRoomData()

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
                    val productList: ArrayList<Product> = ArrayList()
                    for (i in grocery.category.indices) {
                        val catObj = grocery.category[i]
                        // tv_categories.text = "Select category"

                        for (j in 0 until grocery.category[i].subcategory.size) {
                            val subCatObj = catObj.subcategory[j]

                            if (grocery.category[i].subcategory[j].product.size!=0) {
                                for (k in 0 until grocery.category[i].subcategory[j].product.size) {
                                    val productListObj = subCatObj.product[k]
                                    Log.e("Product", Gson().toJson(subCatObj.product[0]))
                                    productList.add(productListObj)

                                    Thread{
//                                        Runnable {
                                            productsDatabase!!.productsDaoAccess()!!.insertProducts(
                                                Product(productListObj.cart_quantity,
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
                            }else{
//                                val toast = Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT)
//                                toast.show()
                            }

                            subcatList.add(subCatObj)

                        }
                        catList.add(catObj)
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

        viewmodel?.failureResponse?.observe(this, {})
        //end observer

        //for clear store item
        viewmodel?.clearCartResponse?.observe(this, { user ->
            // dismissIOSProgress()

            Log.e("stores response", Gson().toJson(user))
            if (tempType.equals("custom")) {

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    ""
                )
            } else {
                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    ""

                )
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        //first time add item
        viewmodel?.addToCartResponse?.observe(this, { user ->
            //dismissIOSProgress()
            Log.e("stores_response", Gson().toJson(user))
            val mModelData: com.fidoo.user.data.model.AddToCartModel = user
            addCartTempList!!.clear()

            viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )


            //showToast(mModelData.message)
            if (isNetworkConnected) {
                //showIOSProgress()
                if (SessionTwiclo(this).isLoggedIn) {
                    var product=Product(product_count!!,productList!![itemPosition!!].company_name,
                        productList!![itemPosition!!].image, productList!![itemPosition!!].in_out_of_stock_status, productList!![itemPosition!!].is_customize,
                        productList!![itemPosition!!].is_customize_quantity,productList!![itemPosition!!].is_nonveg, productList!![itemPosition!!].is_prescription,
                        productList!![itemPosition!!].offer_price, productList!![itemPosition!!].price, productList!![itemPosition!!].product_id,
                        productList!![itemPosition!!].product_name, productList!![itemPosition!!].unit, productList!![itemPosition!!].weight,
                        productList!![itemPosition!!].cart_id
                    )


                    if (product != null) {
                        productList?.set(itemPosition!!,product!!)
                        groceryItemAdapter.notifyItemChanged(itemPosition!!);

                    }

                } else {
                    var product=Product(product_count!!,productList!![itemPosition!!].company_name,
                        productList!![itemPosition!!].image, productList!![itemPosition!!].in_out_of_stock_status, productList!![itemPosition!!].is_customize,
                        productList!![itemPosition!!].is_customize_quantity,productList!![itemPosition!!].is_nonveg, productList!![itemPosition!!].is_prescription,
                        productList!![itemPosition!!].offer_price, productList!![itemPosition!!].price, productList!![itemPosition!!].product_id,
                        productList!![itemPosition!!].product_name, productList!![itemPosition!!].unit, productList!![itemPosition!!].weight,
                        productList!![itemPosition!!].cart_id
                    )


                    if (product != null) {
                        productList?.set(itemPosition!!,product!!)
                        groceryItemAdapter.notifyItemChanged(itemPosition!!);
                    }

                }

            } else {
                showInternetToast()
            }
        })

        //for plus and minus of item
        viewmodel?.addRemoveCartResponse?.observe(this, { user ->

            Log.e("cart_response", Gson().toJson(user))
            if (isNetworkConnected) {
                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
                if (SessionTwiclo(this).isLoggedIn) {
                    var product=Product(product_count!!,productList!![itemPosition!!].company_name,
                        productList!![itemPosition!!].image, productList!![itemPosition!!].in_out_of_stock_status, productList!![itemPosition!!].is_customize,
                        productList!![itemPosition!!].is_customize_quantity,productList!![itemPosition!!].is_nonveg, productList!![itemPosition!!].is_prescription,
                        productList!![itemPosition!!].offer_price, productList!![itemPosition!!].price, productList!![itemPosition!!].product_id,
                        productList!![itemPosition!!].product_name, productList!![itemPosition!!].unit, productList!![itemPosition!!].weight,
                        productList!![itemPosition!!].cart_id
                    )


                    if (product != null) {
                        productList?.set(itemPosition!!,product!!)
                        groceryItemAdapter.notifyItemChanged(itemPosition!!);

                    }

                } else {
                    val product=Product(product_count!!,productList!![itemPosition!!].company_name,
                        productList!![itemPosition!!].image, productList!![itemPosition!!].in_out_of_stock_status, productList!![itemPosition!!].is_customize,
                        productList!![itemPosition!!].is_customize_quantity,productList!![itemPosition!!].is_nonveg, productList!![itemPosition!!].is_prescription,
                        productList!![itemPosition!!].offer_price, productList!![itemPosition!!].price, productList!![itemPosition!!].product_id,
                        productList!![itemPosition!!].product_name, productList!![itemPosition!!].unit, productList!![itemPosition!!].weight,
                        productList!![itemPosition!!].cart_id
                    )


                    if (product != null) {
                        productList?.set(itemPosition!!,product!!)
                        groceryItemAdapter.notifyItemChanged(itemPosition!!)

                    }
                }
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
                    totalprice_txt.text= "₹"+price
                    cartitemView_LL.visibility=View.VISIBLE
                }else{
                    cartitemView_LL.visibility=View.GONE
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
                productListFilter= filterProducts(productList!!, search_value!!) as ArrayList<Product>
                groceryItemAdapter.setFilter(productListFilter!!)
                groceryItemAdapter?.notifyDataSetChanged()



            }
        })
    }

    //get roomdatabase
    private fun getRoomData() {
        Handler().postDelayed(
            {
                productsDatabase!!.productsDaoAccess()!!.getAllProducts().observe(this, Observer {t ->
                 if(onresumeHandle==0) {
                     productList = t as ArrayList<Product>?
                     Log.d("roomdatabase_", productList!!.size.toString())

                     rvlistProduct(productList!!)
                     rvlistSubcategory(subcatList)

                     dismissIOSProgress()
                 }else{
                     var productListUpdate: ArrayList<Product> = ArrayList()
                     productListUpdate = t as ArrayList<Product>
                     Log.d("productListUpdate", productListUpdate!!.size.toString())
                     groceryItemAdapter.setFilter(productListUpdate)
                    // groceryItemAdapter?.notifyDataSetChanged()
                 }

                })
            },
            100
        )
    }

    private fun catPopUp() {
        selectAreaDiolog = Dialog(this)
        selectAreaDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectAreaDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selectAreaDiolog?.setContentView(R.layout.select_cat_popup)
        selectAreaDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        selectAreaDiolog?.layout_catPopup?.startAnimation(slide_)
        // selectAreaDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        selectAreaDiolog?.setCanceledOnTouchOutside(true)
        selectAreaDiolog?.show()
        val txtError = selectAreaDiolog?.findViewById<TextView>(R.id.txtError)
        val viewAll_txt = selectAreaDiolog?.findViewById<TextView>(R.id.viewAll_txt)
        val dismisspopUp = selectAreaDiolog?.findViewById<ImageView>(R.id.dismisspopUp)
        catrecyclerView = selectAreaDiolog?.findViewById(R.id.catRecyclerview)!!

        // catRecyclerview
        txtError?.setOnClickListener(View.OnClickListener {
            slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
            selectAreaDiolog?.layout_catPopup?.startAnimation(slide_)
            selectAreaDiolog?.dismiss()
        })

        dismisspopUp?.setOnClickListener(View.OnClickListener {
            slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
            selectAreaDiolog?.layout_catPopup?.startAnimation(slide_)
            selectAreaDiolog?.dismiss()

        })

        viewAll_txt?.setOnClickListener(View.OnClickListener {
            selectAreaDiolog?.dismiss()
            cat_id = ""
            subcat_name = ""
            sub_cat_id=""
            itemPosition = 0
            viewAll = 1
            tv_categories.text = "Select category"
//            for (i in 0 until catList.size) {
//                filterListShowing(0, catList[i])
//            }
            showIOSProgress()
            viewmodel?.getGroceryProductsFun(
                SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accountId,
                SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accessToken,
                storeID,cat_id,sub_cat_id
            )
        })

        rvCategory(catList)

    }

    //For Products list
    private fun rvlistProduct(listProduct: ArrayList<Product>) {
        productList=listProduct
        Log.d("productList_value", productList?.size.toString())
        var store_id = intent.getStringExtra("storeId")

        groceryItemAdapter = GroceryItemAdapter(
            this,
            productList!!,
            this,
            this,
            0,
            store_id!!, ""
        )

        grocery_item_rv?.adapter = groceryItemAdapter
        Log.d("itemPosition_value", itemPosition!!.toString())
        if (viewAll == 1) {
            grocery_item_rv.smoothScrollToPosition(viewAll!!)
        } else {
            //itemPosition = itemPosition?.plus(1)
            grocery_item_rv.smoothScrollToPosition(itemPosition!!)
        }

    }

    //For SubCategory list Showing on left side of Activity view
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
                    Log.d("grocery___", subgrocery.sub_cat_id)
                    showIOSProgress()
                    // filterListShowingSub(pos, subgrocery)
                    viewmodel?.getGroceryProductsFun(
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accountId,
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accessToken,
                        storeID,cat_id,sub_cat_id
                    )
                }
            },selectedValue)


    }

    //For Category list on showing popup
    private fun rvCategory(catList: ArrayList<Category>) {
        catrecyclerView.adapter = GroceryCategoryAdapter(
            this,
            catList,
            object : GroceryCategoryAdapter.CategoryItemClick {

                override fun onItemClick(pos: Int, grocery: Category) {
                    Log.d("grocery___", grocery.cat_id)
                    tv_categories.text = grocery.cat_name
                    slide_ = AnimationUtils.loadAnimation(this@GroceryItemsActivity, R.anim.slide_in_left)
                    selectAreaDiolog?.layout_catPopup?.startAnimation(slide_)
                    selectAreaDiolog?.dismiss()
                    cat_id = grocery.cat_id
                    itemPosition = 0
                    viewAll = 0
                    sub_cat_id=""
                    //  filterListShowing(pos, grocery)
                    showIOSProgress()
                    viewmodel?.getGroceryProductsFun(
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accountId,
                        SessionTwiclo(this@GroceryItemsActivity).loggedInUserDetail.accessToken,
                        storeID,cat_id,sub_cat_id
                    )

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

    override fun onItemClick(productId: String?, type: String?, count: String?, offerPrice: String?, customize_count: Int?, productType: String?, cart_id: String?
    ) {
        Log.d("onItemClick_count", count!!)

        tempType = type
        tempCount = count
        this.count = count.toInt()
        tempProductId = productId
        mCustomizeCount = customize_count
        tempOfferPrice = offerPrice
        countValue.text = tempCount

        showIOSProgress()


        if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(this).storeId.equals("")
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
        } else {
            clearCartPopup()
        }

    }

    override fun onItemAddRemoveClick(productId: String?, count: String?, type: String?, price: String?, storeId: String?, cartId: String?, position: Int) {

        Log.d("countcount", count!!)
        Log.d("ID__", productId!!)
        product_count = count.toInt()
        itemPosition = position
        viewAll = 0

        Thread{
            productsDatabase = Room.databaseBuilder(
                applicationContext,
                ProductsDatabase::class.java, ProductsDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            productsDatabase!!.productsDaoAccess()!!.updateProducts(count.toInt(),productId!!)

        }.start()

        if (type.equals("add")) {

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
        }
        else {
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

    override fun onAddItemClick(productId: String?, items: String?, offerPrice: String?, customizeid: String?, prodcustCustomizeId: String?, cart_id: String?
    ) {
        // TO be Implemented
    }

    override fun onRemoveItemClick(productId: String?, quantity: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?
    ) {
//        Log.d("onRemoveItemClick_count", quantity!!)
//
//        if (!isNetworkConnected) {
//            showToast(resources.getString(R.string.provide_internet))
//
//        } else {
//            //showIOSProgress()
//            if (cart_id != null) {
//                viewmodel?.addRemoveCartDetails(
//                        SessionTwiclo(this).loggedInUserDetail.accountId,
//                        SessionTwiclo(this).loggedInUserDetail.accessToken,
//                        productId!!,
//                        "remove",
//                        isCustomize!!,
//                        prodcustCustomizeId!!,
//                        cart_id,
//                        customIdsList!!
//                )
//            }
//        }
    }

    private fun showLoginDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { _, _ ->
            startActivity(
                Intent(this, SplashActivity::class.java)
            )


        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog`
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
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

    override fun clearCart() {
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
                onresumeHandle=0

            } else {
                showInternetToast()
            }
        }
    }

}