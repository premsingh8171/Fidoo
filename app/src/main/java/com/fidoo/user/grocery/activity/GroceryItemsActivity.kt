package com.fidoo.user.grocery.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.CartActivity
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.SplashActivity
import com.fidoo.user.adapter.CategoryAdapter
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.adapter.GroceryCategoryAdapter
import com.fidoo.user.grocery.adapter.GroceryItemAdapter
import com.fidoo.user.grocery.adapter.GrocerySubItemAdapter
import com.fidoo.user.grocery.model.getGroceryProducts.Category
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.model.getGroceryProducts.Subcategory
import com.fidoo.user.grocery.viewmodel.GroceryProductsViewModel
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.activity_grocery_items.backIcon
import kotlinx.android.synthetic.main.activity_grocery_items.linear_progress_indicator
import kotlinx.android.synthetic.main.activity_store_items.*
import kotlinx.android.synthetic.main.select_cat_popup.*
import java.lang.Character.toLowerCase

class GroceryItemsActivity : BaseActivity(), AdapterClick,
    AdapterAddRemoveClick,
    AdapterCartAddRemoveClick {
    var viewmodel: GroceryProductsViewModel? = null

    lateinit var recyclerView: RecyclerView
    val catList: ArrayList<Category> = ArrayList()
    val productList: ArrayList<Product>? = null
    val subcatList: ArrayList<Subcategory> = ArrayList()
    var catListFilter: ArrayList<Category> = ArrayList()
    var subcatListFilter: ArrayList<Subcategory> = ArrayList()
    var selectAreaDiolog:Dialog?=null
    lateinit var catrecyclerView: RecyclerView
    var cat_id:String?=""
    var subcat_name:String?=""

    var customIdsList: ArrayList<String>? = null
    private lateinit var groceryItemAdapter: GroceryItemAdapter
    companion object {
        var itemPosition:Int?=0
    }
    private var layoutManger: LinearLayoutManager? = null

    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0
    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempType: String? = ""
    var tempCount: String? = ""
    var count: Int = 1
    private lateinit var mMap: GoogleMap
    var cartId: String = ""
    var storeID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_items)
        viewmodel = ViewModelProviders.of(this).get(GroceryProductsViewModel::class.java)
        layoutManger= LinearLayoutManager(this)
        recyclerView = findViewById(R.id.grocery_item_rv)
        val store_id = intent.getStringExtra("storeId")

        MainActivity.tempProductList = ArrayList()
        MainActivity.addCartTempList = ArrayList()

        customIdsList = ArrayList()

        cartIcon_grocery.setOnClickListener {
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
        viewmodel?.getGroceryProductsFun(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                store_id
        )

        //Here we have got api response from observer
        viewmodel?.GroceryProductsResponse?.observe(this, { grocery ->
            dismissIOSProgress()
            linear_progress_indicator.visibility = View.GONE
            MainActivity.tempProductList!!.clear()
            MainActivity.addCartTempList!!.clear()

            if (!grocery.error) {
                Log.e("Grocery", Gson().toJson(grocery))
                // val subcatList: ArrayList<Subcategory> = ArrayList()
                val productList: ArrayList<Product> = ArrayList()

                for (i in grocery.category.indices) {
                    val catObj = grocery.category[i]
                    tv_categories.text = "Select category"

                    for (j in 0 until grocery.category[i].subcategory.size) {
                        val subCatObj = catObj.subcategory[j]

                        for (k in 0 until grocery.category[i].subcategory[j].product.size) {
                            val productListObj = subCatObj.product[k]
                            Log.e("Product", Gson().toJson(subCatObj.product[0]))
                            productList.add(productListObj)
                        }

                        subcatList.add(subCatObj)
                    }

                    catList.add(catObj)
                }



                Log.d("kb___", "" + productList[0].cart_quantity)

                rvlistSubcategory(subcatList)
                rvlistProduct(productList)

            }

        })

        viewmodel?.failureResponse?.observe(this, {

        })
        //end observer

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

        viewmodel?.addToCartResponse?.observe(this, { user ->
            //dismissIOSProgress()
            Log.e("stores response", Gson().toJson(user))
            val mModelData: com.fidoo.user.data.model.AddToCartModel = user

            viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )
            //showToast(mModelData.message)
            if (isNetworkConnected) {
                //showIOSProgress()
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getGroceryProductsFun(
                        SessionTwiclo(this).loggedInUserDetail.accountId, SessionTwiclo(
                            this
                        ).loggedInUserDetail.accessToken, store_id
                    )
                } else {
                    viewmodel?.getGroceryProductsFun(
                        "",
                        "",
                        store_id
                    )
                }

            } else {
                showInternetToast()
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })


        viewmodel?.addRemoveCartResponse?.observe(this, { user ->


            Log.e("cart response", Gson().toJson(user))
            if (isNetworkConnected) {
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getGroceryProductsFun(
                        SessionTwiclo(this).loggedInUserDetail.accountId, SessionTwiclo(
                            this
                        ).loggedInUserDetail.accessToken, store_id
                    )
                } else {
                    viewmodel?.getGroceryProductsFun(
                        "", "", store_id
                    )
                }
            } else {
                showInternetToast()
            }


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        cat_rl.setOnClickListener {
            catPopUp();
        }

        backIcon.setOnClickListener {
            finish()
        }
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
        selectAreaDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        selectAreaDiolog?.setCanceledOnTouchOutside(true)
        selectAreaDiolog?.show()
        val txtError =  selectAreaDiolog?.findViewById<TextView>(R.id.txtError)
        val viewAll_txt =  selectAreaDiolog?.findViewById<TextView>(R.id.viewAll_txt)
        catrecyclerView = selectAreaDiolog?.findViewById(R.id.catRecyclerview)!!

        // catRecyclerview
        txtError?.setOnClickListener(View.OnClickListener {
            selectAreaDiolog?.dismiss()
        })

        viewAll_txt?.setOnClickListener(View.OnClickListener {
            selectAreaDiolog?.dismiss()
            cat_id=""
            subcat_name=""
            itemPosition=0
            tv_categories.text = "Select category"
            for (i in 0 until catList.size) {
                filterListShowing(0, catList[i])
            }
        })

        rvCategory(catList);

    }

    //For Products list
    private fun rvlistProduct(listProduct: ArrayList<Product>) {
        var store_id=intent.getStringExtra("storeId");

//        grocery_item_rv.adapter = intent.getStringExtra("storeId")?.let {
//            GroceryItemAdapter(
//                this,
//                listProduct,
//                this,
//                this,
//                0, it,
//                "",
//                )
//        }

        groceryItemAdapter =  GroceryItemAdapter(
            this,
            listProduct,
            this,
            this,
            0,
            store_id!!,""
        )
        grocery_item_rv?.adapter = groceryItemAdapter
        itemPosition= itemPosition?.plus(1)
        grocery_item_rv.smoothScrollToPosition(itemPosition!!)
    }



    //For SubCategory list Showing on left side of Activity view
    private fun rvlistSubcategory(subcatList: ArrayList<Subcategory>) {
        sub_cat_rv.adapter = GrocerySubItemAdapter(
            this,
            subcatList,
            object : GrocerySubItemAdapter.SubcategoryItemClick {
                override fun onItemClick(pos: Int, subgrocery: Subcategory) {
                    Log.d("grocery___", subgrocery.subcategory_name)
                    itemPosition=0
                    subcat_name=subgrocery.subcategory_name
                    filterListShowingSub(pos,subgrocery)

                }
            })

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
                    selectAreaDiolog?.dismiss()
                    cat_id= grocery.cat_id;
                    itemPosition=0
                    filterListShowing(pos ,grocery)

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
            rvlistProduct(productListF)
            grocery_item_rv.adapter!!.notifyDataSetChanged()
        }else{
            if (productList != null) {
                rvlistProduct(productList)
            }
            grocery_item_rv.adapter!!.notifyDataSetChanged()
        }
    }

    //Filter data showing by category
    private fun filterListShowingSub(pos: Int, subgrocery: Subcategory) {
        if (subcat_name != "") {
            subcatListFilter = filterSubCategory(subcatList, subcat_name!!) as ArrayList<Subcategory>
            val subcatListF: ArrayList<Subcategory> = ArrayList()
            val productListF: ArrayList<Product> = ArrayList()
            for (j in 0 until subcatListFilter.size) {
                var subCatObj = subcatListFilter[j]

                for (k in 0 until subcatListFilter[j].product.size) {
                    var productListObj = subcatListFilter[j].product[k]
                    productListF.add(productListObj)
                }
                subcatListF.add(subCatObj)
            }
            rvlistProduct(productListF)
            grocery_item_rv.adapter!!.notifyDataSetChanged()
        }else{
            if (productList != null) {
                rvlistProduct(productList)
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

    override fun onItemAddRemoveClick(
        productId: String?,
        count: String?,
        type: String?,
        price: String?,
        storeId: String?,
        cartId: String?,
        position: Int) {

        Log.d("count", count!!)
        Log.d("ID", productId!!)
        itemPosition=position
        showIOSProgress()

        //showIOSProgress()
        //SessionTwiclo(this).storeId = intent.getStringExtra("storeId")


        if (type.equals("add")) {
            //cartIcon_grocery.setImageResource(R.drawable.cart_icon)

            if (MainActivity.tempProductList!!.size == 0) {
                //customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()
                val tempProductListModel = TempProductListModel()
                tempProductListModel.productId = productId
                tempProductListModel.quantity = count
                tempProductListModel.price = price
                MainActivity.tempProductList!!.add(tempProductListModel)

                Log.e("TEMP", Gson().toJson(MainActivity.tempProductList))

                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = productId
                addCartInputModel.quantity = count
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "0"
                MainActivity.addCartTempList!!.add(addCartInputModel)

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    ""
                )
                MainActivity.tempProductList!!.clear()
            } else {
                var check: String = ""
                var tempPos: Int = 0

                for (i in 0 until MainActivity.tempProductList!!.size) {
                    Log.e("check1", MainActivity.tempProductList!![i].productId)
                    Log.e("check2", productId)
                    if (MainActivity.tempProductList!![i].productId.equals(productId)) {
                        check = "edit"
                        tempPos = i
                        //tempProductList!!.get(i).quantity = count
                        break
                    }
                }
                if (check == "edit") {

                    MainActivity.tempProductList!![tempPos].quantity = count
                    MainActivity.addCartTempList!![tempPos].quantity = count

                } else {

                    val tempProductListModel = TempProductListModel()
                    tempProductListModel.productId = productId
                    tempProductListModel.quantity = count
                    tempProductListModel.price = price
                    MainActivity.tempProductList!!.add(tempProductListModel)

                    val addCartInputModel = AddCartInputModel()
                    addCartInputModel.productId = productId
                    addCartInputModel.quantity = count
                    addCartInputModel.message = "add product"
                    addCartInputModel.customizeSubCatId = customIdsList!!
                    addCartInputModel.isCustomize = "0"
                    MainActivity.addCartTempList!!.add(addCartInputModel)
                }

                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    ""
                )
            }


        } else {
            //cartIcon_grocery.setImageResource(R.drawable.ic_cart)
            var check = "edit"
            var checkPos = 0
            Log.e("check1", Gson().toJson(MainActivity.tempProductList!!))
            for (i in 0 until MainActivity.tempProductList!!.size) {
                if (MainActivity.tempProductList!![i].productId.equals(productId)) {
                    if (count == "0") {
                        check = "remove"
                        checkPos = i

                        break
                        //  addCartTempList!!.removeAt(i)
                        //   tempProductList!!.removeAt(i)

                    }

                    /*else {
                    tempProductList!!.get(i).quantity = count
                    addCartTempList!!.get(i).quantity = count
                }*/
                }
            }
            Log.d("checkpos", checkPos.toString())
            if (check == "remove") {
                //cartIcon_grocery.setImageResource(R.drawable.ic_cart)
                //ll_view_cart.visibility = View.GONE // to hide the bottom cart bar if non-customized item quantity becomes zero
                MainActivity.addCartTempList!!.removeAt(checkPos)
                MainActivity.tempProductList!!.removeAt(checkPos)
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
                MainActivity.tempProductList!![checkPos].quantity = count
                MainActivity.addCartTempList!![checkPos].quantity = count

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
        Log.d("check1", Gson().toJson(MainActivity.tempProductList!!))
        var bottomPrice: Double? = 0.0
        var bottomCount: Int? = 0
        for (i in 0 until MainActivity.tempProductList!!.size) {
            bottomPrice = bottomPrice!! + (MainActivity.tempProductList!!.get(i).price.toDouble() * MainActivity.tempProductList!!.get(i).quantity.toInt())
            bottomCount = bottomCount!! + MainActivity.tempProductList!!.get(i).quantity.toInt()
        }
        //txt_price_.text = resources.getString(R.string.ruppee) + bottomPrice.toString()
        //cartCountTxt.text = bottomCount.toString()
        //cartIcon_grocery.setImageResource(R.drawable.cart_icon)
        //cartIcon_grocery.setColorFilter(Color.argb(255, 53, 156, 71))
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
        customizeid: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    ) {
        // TO be Implemented
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

        if (isNetworkConnected) {
            showIOSProgress()
            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getGroceryProductsFun(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    intent.getStringExtra("storeId")
                )
            } else {
                viewmodel?.getGroceryProductsFun(
                    "",
                    "",
                    intent.getStringExtra("storeId")
                )
            }

        } else {
            showInternetToast()
        }
    }


    /*object : GroceryItemAdapter.GroceryItemClick {

                override fun onItemClick(pos: Int, grocery: Product) {
                    TODO("Not yet implemented")
                }

                override fun onItemAdd(pos: Int, itemcount: Int, grocery: Product) {
                    TODO("Not yet implemented")
                }

                override fun onItemSub(pos: Int, itemcount: Int, grocery: Product) {
                    TODO("Not yet implemented")
                }
            })*/
}