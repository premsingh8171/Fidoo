package com.fidoo.user.search.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.CartActivity
import com.fidoo.user.R
import com.fidoo.user.adapter.StoreCustomItemsAdapter
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.search.adapter.ParentStoreListAdapter
import com.fidoo.user.search.model.ProductsList
import com.fidoo.user.search.model.SearchListModel
import com.fidoo.user.search.model.Store
import com.fidoo.user.search.viewmodel.SearchListViewModel
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.ui.SearchFragment
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.CommonUtils
import com.fidoo.user.viewmodels.SearchFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_item.*
import kotlinx.android.synthetic.main.activity_store_items.*
import kotlinx.android.synthetic.main.fragment_search.*
import java.lang.NumberFormatException

class SearchItemActivity : BaseActivity() , AdapterClick,
        AdapterAddRemoveClick,
        AdapterCartAddRemoveClick {
    private lateinit var parentStoreListAdapter: ParentStoreListAdapter
    val storeList: ArrayList<Store> = ArrayList()
    var viewmodel: SearchFragmentViewModel? = null
    lateinit var mProductStoreList: ArrayList<SearchModel.Store>
    lateinit var mProductsList: ArrayList<SearchModel.ProductList>
    var storeID: String = ""
    var tempType: String? = ""
    var search_value: String? = ""
    var customIdsList: ArrayList<String>? = null
    var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null

    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempCount: String? = ""
    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorGreenText)
        setContentView(R.layout.activity_search_item)
        viewmodel = ViewModelProviders.of(this).get(SearchFragmentViewModel::class.java)
        mProductStoreList= ArrayList()
        mProductsList= ArrayList()
        customIdsList= ArrayList()
        customIdsListTemp= ArrayList()



        searchEdt_new?.addTextChangedListener(object : TextWatcher {

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

                if (searchEdt_new?.text.toString().length >= 1) {
                    if (SessionTwiclo(this@SearchItemActivity).isLoggedIn) {
                    viewmodel?.getSearchApi(SessionTwiclo(this@SearchItemActivity).loggedInUserDetail.accountId,
                            SessionTwiclo(this@SearchItemActivity).loggedInUserDetail.accessToken, search_value!!)

                    }
                } else {
                    search_value=""
                    mProductStoreList.clear()
                    mProductsList.clear()
                    parentStoreListAdapter.notifyDataSetChanged()
                    total_itemTxt.text = "Items ("+mProductsList.size.toString()+")"

                }

            }
        })

        back_newsearch?.setOnClickListener {
            finish()
        }

        //Here we have called Api of getGroceryProducts
        viewmodel?.getSearchApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                ""
        )

        viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
        )


        getResponce()


        new_viewcartfromSearch.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
               startActivity( Intent(this, CartActivity::class.java).putExtra(
                        "store_id", SessionTwiclo(
                       this
                ).storeId
                )
                )
            } else {
                showLoginDialog("Please login to proceed")

            }
        }


    }

    private fun getResponce() {
        //Here we observe searchApi responce
        viewmodel?.searchResponse?.observe(this,{searchResponce->
            if (!searchResponce.error!!){


                val storeList: ArrayList<SearchModel.Store> = ArrayList()
                val productList: ArrayList<SearchModel.ProductList> = ArrayList()

                val mModelData: SearchModel = searchResponce
                for (i in 0 until mModelData.store.size) {
                    val storeModel = mModelData.store[i]
                    for (j in 0 until mModelData.store[i].list.size) {
                        val productData = mModelData.store[i].list[j]
                        productList.add(productData)
                    }

                    storeList.add(storeModel)
                }
                mProductsList=productList
                mProductStoreList=storeList

                total_itemTxt.text = "Items ("+mProductsList.size.toString()+")"
                parentStoreListAdapter = ParentStoreListAdapter(
                        this,mProductStoreList,this,storeID,this,this
                )
                search_rv?.adapter=parentStoreListAdapter



            }

        })

        //cartcount responce
        viewmodel?.cartCountResponse?.observe(this,{cartcount->
            CommonUtils.dismissIOSProgress()

            Log.d("cartCountResponse___",cartcount.toString())
            var count = cartcount.count
            var price = cartcount.price
            storeID = cartcount.store_id
            if (!cartcount.error){
                if (count!="0"){
                    new_itemQuantity_textsearch.text=count
                    new_totalprice_txtsearch.text= "₹"+price
                    new_cartitemView_LLsearch.visibility= View.VISIBLE
                    SessionTwiclo(this).storeId=cartcount.store_id
                    // setStoreId
                }else{
                    new_cartitemView_LLsearch.visibility= View.GONE
                }
            }

        })

        viewmodel?.addRemoveCartResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("cart_response", Gson().toJson(user))
            if (isNetworkConnected) {
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getSearchApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            search_value!!
                    )
                    viewmodel?.getCartCountApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                } else {
                    viewmodel?.getSearchApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            search_value!!
                    )
                    viewmodel?.getCartCountApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                }
            } else {
                showInternetToast()
            }


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        //add to cart
        viewmodel?.addToCartResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("stores response", Gson().toJson(user))
            val mModelData: com.fidoo.user.data.model.AddToCartModel = user
            if (tempType.equals("custom")) {
//                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                    //searchLay.visibility = View.GONE
//                } else {
//                    //searchLay.visibility = View.VISIBLE
//                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                }
            } else {
                MainActivity.tempProductList!!.clear()
                MainActivity.addCartTempList!!.clear()

            }
            viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
            )
            //showToast(mModelData.message)
            if (isNetworkConnected) {
                //showIOSProgress()
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getSearchApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            search_value!!
                    )
                } else {
                    viewmodel?.getSearchApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            search_value!!
                    )
                }

            } else {
                showInternetToast()
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        //clear cart all item
        viewmodel?.clearCartResponse?.observe(this, Observer { user ->
            // dismissIOSProgress()

            Log.e("stores_response", Gson().toJson(user))
            if (tempType.equals("custom")) {

                viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                )
                viewmodel?.getCartCountApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            } else {
                viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""

                )
                viewmodel?.getCartCountApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        //error
        viewmodel?.failureResponse?.observe(this, Observer { user ->
            Log.e("cart_response", Gson().toJson(user))
        })

    }

    private fun showLoginDialog(message: String){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Login") { dialogInterface, which ->
//            startActivity(
//                Intent(activity, LoginActivity::class.java)
//            )

        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
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
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    // this click to use for customize item first time add
    override fun onItemClick(productId: String?, type: String?, count: String?, offerPrice: String?, customize_count: Int?, productType: String?, cart_id: String?)
    {
        /*  if (behavior?.getState() != BottomSheetBehavior.STATE_EXPANDED) {
              behavior?.setState(BottomSheetBehavior.STATE_EXPANDED);

          } else {
              behavior?.setState(BottomSheetBehavior.STATE_COLLAPSED);

          }*/

        tempType = type
        tempCount = count
        tempProductId = productId
        mCustomizeCount = customize_count
        tempOfferPrice = offerPrice


        if (type == "custom") {
            if (mCustomizeCount == 0) {
                customIdsListTemp?.clear()

//
//                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                    //searchLay.visibility = View.GONE
//
//                } else {
//                    //searchLay.visibility = View.VISIBLE
//                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//
//                }
                //  tempProductId = productId
                //showIOSProgress()
                customIdsList!!.clear()
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
                builder.setPositiveButton("I'LL CHOOSE") { dialogInterface, which ->

//                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//                        //searchLay.visibility = View.GONE
//                    } else {
//                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                        //searchLay.visibility = View.VISIBLE
//                    }

                    customIdsList!!.clear()
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
                //showIOSProgress()

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
    }

    //for plus and minus click
    override fun onItemAddRemoveClick(productId: String?, count: String?, type: String?, price: String?, storeId: String?, cartId: String?, position: Int)
    {

        showIOSProgress()
        //SessionTwiclo(this).storeId = intent.getStringExtra("storeId")


        if (type.equals("add")) {

            if (MainActivity.tempProductList!!.size == 0) {
                //customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()
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

                if (cartId != null) {
                    viewmodel!!.addToCartApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            MainActivity.addCartTempList!!,
                            ""
                    )
                }
                MainActivity.tempProductList!!.clear()
            } else {
                var check: String = ""
                var tempPos: Int = 0

                for (i in 0 until MainActivity.tempProductList!!.size) {
                    Log.e("check1", MainActivity.tempProductList!!.get(i).productId)
                    Log.e("check2", productId!!)
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

                    viewmodel?.addRemoveCartDetails(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            productId!!,
                            "remove",
                            "0",
                            "","",
                            customIdsList!!
                    )

        }
    }

    override fun clearCart() {
        viewmodel?.clearCartApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
        )
    }

    //on add item first time
    override fun onAddItemClick(productId: String?, items: String?, offerPrice: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?)
    {
        tempOfferPrice = offerPrice
        //plusMinusPrice = 0.0
        tempPrice = 0.0
        tempProductId = productId

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Your previous customization")
        builder.setMessage(items)
        builder.setPositiveButton("I'LL CHOOSE") { _, which ->

//            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//                //searchLay.visibility = View.GONE
//            } else {
//                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                //searchLay.visibility = View.VISIBLE
//            }

            //tempProductId = productId
            //showIOSProgress()
            customIdsList!!.clear()
            viewmodel?.customizeProductApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken, productId!!
            )
        }

        //performing negative action
        builder.setNegativeButton("REPEAT") { _, which ->
            //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
            //showIOSProgress()
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

    //customize item minus click
    override fun onRemoveItemClick(productId: String?, quantity: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?) {
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

    override fun onResume() {
        super.onResume()
        //Here we have called Api of getGroceryProducts
        viewmodel?.getSearchApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                search_value!!
        )
        viewmodel?.getCartCountApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
        )

    }


        }

