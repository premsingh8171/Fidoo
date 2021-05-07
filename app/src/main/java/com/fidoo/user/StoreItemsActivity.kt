package com.fidoo.user

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.adapter.StoreCustomItemsAdapter
import com.fidoo.user.adapter.StoreItemsAdapter
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.*
import com.fidoo.user.ui.MainActivity.Companion.addCartTempList
import com.fidoo.user.ui.MainActivity.Companion.tempProductList
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.viewmodels.CartViewModel
import com.fidoo.user.viewmodels.StoreDetailsViewModel
import com.fidoo.user.viewmodels.TrackViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.activity_store_items.*
import kotlinx.android.synthetic.main.activity_store_items.backIcon
import kotlinx.android.synthetic.main.activity_store_items.linear_progress_indicator
import kotlinx.android.synthetic.main.activity_store_items.tv_deliveryTime
import kotlinx.android.synthetic.main.activity_store_items.tv_location
import kotlinx.android.synthetic.main.activity_store_items.tv_store_name
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
    var cartViewModel: CartViewModel? = null
    var viewmodel: StoreDetailsViewModel? = null
    var distanceViewModel: TrackViewModel? = null
    var customIdsList: ArrayList<String>? = null
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

    companion object {

        var customerLatitude: String = ""
        var customerLongitude: String = ""

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_items)

        distanceViewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        behavior = BottomSheetBehavior.from(bottom_sheet)

        tempProductList = ArrayList()
        addCartTempList = ArrayList()
        customIdsList = ArrayList()
        customIdsListTemp = ArrayList()

        customerLatitude = ""
        customerLongitude = ""




        viewmodel = ViewModelProviders.of(this).get(StoreDetailsViewModel::class.java)
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        tv_location.text = intent.getStringExtra("store_location")

        cartviewFromStore.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(Intent(this, CartActivity::class.java).putExtra(
                        "store_id", SessionTwiclo(
                        this
                ).storeId
                )
                )
            } else {
                showLoginDialog("Please login to proceed")

            }

        }

        cartIcon.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                startActivity(Intent(this, CartActivity::class.java).putExtra(
                        "store_id", SessionTwiclo(
                        this
                ).storeId
                )
                )
            } else {
                showLoginDialog("Please login to proceed")

            }

        }



        backIcon.setOnClickListener {
            finish()
        }

        customAddBtn.setOnClickListener {
            Log.e("addCartTempList", Gson().toJson(addCartTempList))

            for (i in 0 until categoryy!!.size) {
                customIdsList!!.add(categoryy!![i].id.toString())
            }

            Log.e("customIdsList", customIdsList.toString())

            if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(this).storeId.equals("")) {
                showIOSProgress()
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")

                /* viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                   "", countValue.text.toString(), "", "1", customIdsList!!
                )*/

                addCartTempList!!.clear()
                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = tempProductId
                addCartInputModel.quantity = countValue.text.toString()
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "1"
                addCartTempList!!.add(addCartInputModel)

                viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        addCartTempList!!,
                        ""
                )

            } else {
                clearCartPopup()
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

            }

        }

        if (isNetworkConnected) {
            if (SessionTwiclo(this).isLoggedIn) {


                viewmodel?.getStoreDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        intent.getStringExtra("storeId"),
                        "",
                        intent.getStringExtra("catId")

                )

                viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            } else {


                viewmodel?.getStoreDetails(
                        "",
                        "",
                        intent.getStringExtra("storeId"),
                        "",
                        intent.getStringExtra("catId")

                )
            }
        } else {
            showInternetToast()
        }




        //cartcount responce
        viewmodel?.cartCountResponse?.observe(this,{cartcount->
            dismissIOSProgress()

            Log.d("cartCountResponse___",cartcount.toString())
            var count = cartcount.count
            var price = cartcount.price
            if (!cartcount.error){
                if (count!="0"){
                    cartIcon.setImageResource(R.drawable.cart_icon)
                    cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
                    itemQuantity_textstore.text=count
                    totalprice_txtstore.text= "₹"+price
                    cartitemView_LLstore.visibility=View.GONE
                }else{
                    cartIcon.setImageResource(R.drawable.ic_cart)
                    cartIcon.setColorFilter(Color.argb(255, 199, 199, 199))
                    cartitemView_LLstore.visibility=View.GONE
                }
            }

        })

        viewmodel?.getStoreDetailsApi?.observe(this, Observer { storeData ->
            dismissIOSProgress()
            linear_progress_indicator.visibility = View.GONE
            tempProductList!!.clear()
            addCartTempList!!.clear()
            Log.e("store details response", Gson().toJson(storeData))


            val productList: ArrayList<StoreDetailsModel.Product> = ArrayList()
            val catList: ArrayList<StoreDetailsModel.Category> = ArrayList()


            if (storeData.category.size != 0) {

                for (i in 0 until storeData.category.size) {


                    for (j in 0 until storeData.category[i].product.size) {
//                    var catObj = storeData.category[i]
//                    catList.add(catObj)
                        val productData = storeData.category[i].product[j]
                        productList.add(productData)
                    }
                }

                var catName = catList.size


                /*for (i in 0 until storeData.category.size)
            {
                var name = catList[i]
                catList.add(name)
                Log.d("kb",""+name)

            }*/


                Log.d("kb", "" + productList.toString())


                storeItemsRecyclerview.layoutManager = LinearLayoutManager(this)
                storeItemsRecyclerview.setHasFixedSize(true)

                val adapter = storeID?.let {
                    StoreItemsAdapter(
                            this,
                            this,
                            productList,
                            catList,
                            "",
                            "",
                            "3.5",
                            "5",
                            this,
                            this,
                            0,
                            it,
                            productList[0].cartId
                    )
                }
                storeItemsRecyclerview.adapter = adapter

                /*itemsRecyclerView.layoutManager = LinearLayoutManager(this)
            itemsRecyclerView.setHasFixedSize(true)
            itemsRecyclerView.adapter = adapter*/
                if (storeData.fssai != null) {
                    if (!storeData.fssai.equals("")) {
                        //fssaiTxt.text = "Fssai: " + user.fssai
                    }
                }
                //ratingValue.text = user.rating
                tv_deliveryTime.text = intent.getStringExtra("delivery_time") + " minutes"


                /* val adapterr = PromosAdapter(this, user.offers)
             promosRecyclerView.layoutManager = LinearLayoutManager(
                 this, LinearLayoutManager.HORIZONTAL,
                 false
             )*/

                /*promosRecyclerView.setHasFixedSize(true)
            promosRecyclerView.adapter = adapterr*/
                if (storeData.categoryId != null) {
                    if (storeData.categoryId.equals("5")) {
                        //vegOnlySwitch.visibility = View.VISIBLE
                    } else {

                        //vegOnlySwitch.visibility = View.GONE
                    }
                }

                if (SessionTwiclo(this).isLoggedIn) {

                    viewmodel?.getCartCountApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                }

                //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()

                //calculateStoreCustomerDistance(user.storeLatitude+","+user.storeLongitude , SessionTwiclo(this).userLat+","+SessionTwiclo(this).userLng)
                //Log.e("DISTANCE", customerLatitude)
            }else{
                val toast = Toast.makeText(applicationContext, "No Product found", Toast.LENGTH_SHORT)
                toast.show()
            }
        })

        viewmodel?.addRemoveCartResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            if (isNetworkConnected) {
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getStoreDetails(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            intent.getStringExtra("storeId"),
                            "",
                            intent.getStringExtra("catId")

                    )
                } else {
                    viewmodel?.getStoreDetails(
                            "",
                            "",
                            intent.getStringExtra("storeId"),
                            "",
                            intent.getStringExtra("catId")

                    )
                }
            } else {
                showInternetToast()
            }


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })



        viewmodel?.failureResponse?.observe(this, { user ->
            dismissIOSProgress()
            Log.e("cart response", Gson().toJson(user))
            //showToast(user)


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

//        viewmodel?.cartCountResponse?.observe(this, { user ->
//            dismissIOSProgress()
//            if (!user.error) {
//                val mModelData: com.fidoo.user.data.model.CartCountModel = user
//                SessionTwiclo(this).storeId = mModelData.store_id
//                Log.e("Store ID", mModelData.store_id)
//                Log.e("countResponse", Gson().toJson(mModelData))
//                tempProductList
//                if (user.count.toInt() > 0) {
//                    cartIcon.setImageResource(R.drawable.cart_icon)
//                    cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
//
//                    //Toast.makeText(this,""+count,Toast.LENGTH_SHORT).show()
//
//                    //  bottomPrice=user.price.toDouble()
//                    //cartCountTxt.visibility = View.VISIBLE
//                    //ll_view_cart.visibility = View.VISIBLE
//                    //cartCountTxt.text = user.count
//                    //txt_price_.text = resources.getString(R.string.ruppee) + user.price
//                    if (user.count.equals("1")) {
//                        //txt_items_.text = user.count + " item"
//                    } else {
//                        //txt_items_.text = user.count + " items"
//                    }
//                } else {
//                    cartIcon.setImageResource(R.drawable.ic_cart)
//                    //cartCountTxt.visibility = View.GONE
//                    //ll_view_cart.visibility = View.GONE
//                }
//            } else {
//                if (user.errorCode == 101) {
//                    showAlertDialog(this)
//                }
//            }
//        })

        viewmodel?.addToCartResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("stores response", Gson().toJson(user))
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
            //showToast(mModelData.message)
            if (isNetworkConnected) {
                showIOSProgress()
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getStoreDetails(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            intent.getStringExtra("storeId"),
                            "",
                            intent.getStringExtra("catId")
                    )
                } else {
                    viewmodel?.getStoreDetails(
                            "",
                            "",
                            intent.getStringExtra("storeId"),
                            "",
                            intent.getStringExtra("catId")
                    )
                }

            } else {
                showInternetToast()
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.customizeProductResponse?.observe(this, Observer { user ->
            dismissIOSProgress()

            Log.e("stores___esponse", Gson().toJson(user))
            mModelDataTemp = user

            categoryy = ArrayList()

            for (i in 0 until mModelDataTemp?.category?.size!!) {
                if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
                    var customListModel: CustomListModel? = CustomListModel()
                    customListModel!!.category = mModelDataTemp?.category?.get(i)!!.catId
                    customListModel.id = mModelDataTemp?.category?.get(i)!!.subCat[0].id.toInt()
                    customListModel.price = mModelDataTemp?.category?.get(i)!!.subCat[0].price
                    categoryy!!.add(customListModel)
                } else {

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

        veg_switch.setOnCheckedChangeListener { _, b ->

            Log.e("b", b.toString())

            if (b) {


                if (isNetworkConnected) {
                    showIOSProgress()
                    if (SessionTwiclo(this).isLoggedIn) {
                        viewmodel?.getStoreDetails(
                                SessionTwiclo(this).loggedInUserDetail.accountId,
                                SessionTwiclo(this).loggedInUserDetail.accessToken,
                                intent.getStringExtra("storeId"),
                                "0",
                                intent.getStringExtra("catId")

                        )
                    } else {
                        viewmodel?.getStoreDetails("",
                                "",
                                intent.getStringExtra("storeId"),
                                "0",
                                intent.getStringExtra("catId")

                        )
                    }
                } else {
                    showInternetToast()
                }

            } else {

                showIOSProgress()
                viewmodel?.getStoreDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        intent.getStringExtra("storeId"),
                        "",
                        intent.getStringExtra("catId")
                )
            }
        }

    }

    override fun clearCart() {
        viewmodel?.clearCartApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken
        )
    }

    override fun onBackPressed() {

        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            super.onBackPressed()
        } else {
            //searchLay.visibility = View.VISIBLE
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

        }
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



        if (isNetworkConnected) {
            showIOSProgress()
            if (SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getStoreDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        intent.getStringExtra("storeId"),
                        "",
                        intent.getStringExtra("catId")

                )
            } else {
                viewmodel?.getStoreDetails(
                        "",
                        "",
                        intent.getStringExtra("storeId"),
                        "",
                        intent.getStringExtra("catId")

                )
            }

        } else {
            showInternetToast()
        }
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
        /*  if (behavior?.getState() != BottomSheetBehavior.STATE_EXPANDED) {
              behavior?.setState(BottomSheetBehavior.STATE_EXPANDED);

          } else {
              behavior?.setState(BottomSheetBehavior.STATE_COLLAPSED);

          }*/

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
                builder.setPositiveButton("I'LL CHOOSE") { dialogInterface, which ->

                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                        //searchLay.visibility = View.GONE
                    } else {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        //searchLay.visibility = View.VISIBLE
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
                    //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                }

                //performing negative action
                builder.setNegativeButton("REPEAT") { dialogInterface, which ->
                    //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
                    //showIOSProgress()
                    /*     viewmodel?.addRemoveCartDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        productId,
                        "add",
                        isCustomize,
                        productCustomizeId
                    )*/

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
            val customCheckBoxModel = CustomCheckBoxModel()
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

        /* if (type.equals("select")) {
             customIdsList!!.add(productId!!)
         } else {
             for (i in 0..customIdsList!!.size - 1) {
                 if (customIdsList!!.get(i).equals(productId)) {
                     customIdsList!!.removeAt(i)
                     break
                 }
             }
         }*/

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
            for (j in 0 until mModelDataTemp?.category!!.get(i).subCat.size) {
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
            var customListModel: CustomListModel? = CustomListModel()
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

                    tempProductList!![tempPos].quantity = count
                    addCartTempList!![tempPos].quantity = count

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
                tempProductList!![checkPos].quantity = count
                addCartTempList!![checkPos].quantity = count

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
            quantity: String?,
            offerPrice: String?,
            isCustomize: String?,
            prodcustCustomizeId: String?,
            cart_id: String?
    ) {
        tempOfferPrice = offerPrice
        //plusMinusPrice = 0.0
        tempPrice = 0.0
        tempProductId = productId

        Log.d("isCustomize__",isCustomize!!+"\n"+quantity)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Your previous customization")
        builder.setMessage(quantity)
        builder.setPositiveButton("I'LL CHOOSE") { _, which ->
//
//            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//                //searchLay.visibility = View.GONE
//            } else {
//                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                //searchLay.visibility = View.VISIBLE
//            }

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
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { _, which ->
            startActivity(
                    Intent(this, LoginActivity::class.java)
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

    override fun onPause() {
        super.onPause()
        if (SessionTwiclo(this).isLoggedIn) {
            /* viewmodel!!.addToCartApi(
                     SessionTwiclo(this).loggedInUserDetail.accountId,
                     SessionTwiclo(this).loggedInUserDetail.accessToken,
                     addCartTempList!!,
                     ""

             )*/
        }

    }
}