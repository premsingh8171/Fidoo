package com.fidoo.user.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fidoo.user.CartActivity
import com.fidoo.user.R
import com.fidoo.user.adapter.ItemImagesAdapter
import com.fidoo.user.adapter.SliderAdapterExample
import com.fidoo.user.adapter.SpecificationsAdapter
import com.fidoo.user.data.SliderItem
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterImageClick
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.utils.statusBarTransparent
import com.fidoo.user.viewmodels.ProductDetailsViewModel
import com.google.gson.Gson
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_single_product.*
import kotlinx.android.synthetic.main.activity_single_product.backIcon
import kotlinx.android.synthetic.main.activity_single_product.cartCountTxt
import kotlinx.android.synthetic.main.activity_store_items.cartIcon

class SingleProductActivity : BaseActivity(), AdapterImageClick {
    var viewmodel: ProductDetailsViewModel? = null
    var count: Int = 0
    var savings: Int = 0
    var type: String? = ""
    var tempStoreId: String? = ""
    var sliderItem = SliderItem()
    var mModelData: ProductDetailsModel? = null

    var specificationList = ArrayList<Specification>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_product)
        statusBarTransparent()
        viewmodel = ViewModelProviders.of(this).get(ProductDetailsViewModel::class.java)
        backIcon.setOnClickListener {
            finish()
        }
        showIOSProgress()

        if (SessionTwiclo(this).isLoggedIn) {
            viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
            )
        } else {

        }




        viewmodel?.cartCountResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            if (!user.error) {
                val mModelData: CartCountModel = user

                Log.e("countResponse", Gson().toJson(mModelData))
                if (user.count.toInt() > 0) {
                    cartCountTxt.visibility = View.VISIBLE
                    cartCountTxt.text = user.count
                } else {
                    cartCountTxt.visibility = View.GONE
                }
            } else {
                if (user.errorCode == 101) {
                    showAlertDialog(this)
                }
            }
        })
        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            showToast(user)


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })


        addToCartTxtView.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                if (SessionTwiclo(this).storeId.equals(mModelData!!.storeId) || SessionTwiclo(this).storeId.equals(
                                ""
                        )
                ) {
                    showIOSProgress()
                    type = "cart"
                    var customIdsList = ArrayList<String>()
                    MainActivity.addCartTempList!!.clear()
                    val addCartInputModel = AddCartInputModel()
                    addCartInputModel.productId = mModelData!!.productId
                    addCartInputModel.quantity = countValuee.text.toString()
                    addCartInputModel.message = "add product"
                    addCartInputModel.customizeSubCatId = customIdsList
                    addCartInputModel.isCustomize = "0"
                    MainActivity.addCartTempList!!.add(addCartInputModel)
                    viewmodel!!.addToCartApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            MainActivity.addCartTempList!!
                    )

                } else {
                    val builder = AlertDialog.Builder(this)
                    //set title for alert dialog
                    builder.setTitle("Replace cart item!")
                    //set message for alert dialog
                    builder.setMessage("Do you want to discard the previous selection?")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)

                    //performing positive action
                    builder.setPositiveButton("Yes") { dialogInterface, which ->

                        showIOSProgress()
                        viewmodel!!.clearCartApi(
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
            } else {
                showLoginDialog("Please login to proceed")
            }

        }

        buyNowTxt.setOnClickListener {
            Log.e("intent store id", mModelData!!.storeId.toString())
            //  SessionTwiclo(this).storeId=intent.getStringExtra("storeId")
            Log.e("  store id", SessionTwiclo(this).storeId.toString())
            if (SessionTwiclo(this).storeId.equals(mModelData!!.storeId)) {

                showIOSProgress()
                type = "buy"
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
                viewmodel!!.addToCartApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!
                )

            } else {
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle("Replace cart item!")
                //set message for alert dialog
                builder.setMessage("Do you want to discard the previous selection?")
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                //performing positive action
                builder.setPositiveButton("Yes") { dialogInterface, which ->

                    showIOSProgress()
                    viewmodel!!.clearCartApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                    /* viewmodel?.logoutapi(
                         SessionTwiclo(this).loggedInUserDetail.accountId,
                         SessionTwiclo(this).loggedInUserDetail.accessToken
                     )*/

                    //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                }
                /*   //performing cancel action
                   builder.setNeutralButton("Cancel"){dialogInterface , which ->
                       Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
                   }*/
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

        }


        if (isNetworkConnected) {
            if (SessionTwiclo(this).isLoggedIn) {
                showIOSProgress()
                viewmodel!!.getProductDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        intent.getStringExtra("productId")
                )
            } else {
                showInternetToast()
            }
        } else {
            showLoginDialog("Please login to proceed")
        }

        // offerPrice.setPaintFlags(offerPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        viewmodel?.getProductDetailsResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("stores response", Gson().toJson(user))


            if (intent.hasExtra("service_id")) {
                if (intent.getStringExtra("service_id").equals("2")) {
                    ll_techinal.visibility = View.VISIBLE
                } else {
                    ll_techinal.visibility = View.GONE
                }


            }



            specificationList.clear()
            mModelData = user
//----------------------------------
            val sliderItemList: ArrayList<SliderItem> = ArrayList()
            for (i in 0..mModelData!!.images.size - 1) {
                sliderItem = SliderItem()
                sliderItem.imageUrl = mModelData!!.images.get(i)
                sliderItemList.add(sliderItem)
            }

            /* val textView: TextView = root.findViewById(R.id.text_home)
                     homeViewModel.text.observe(viewLifecycleOwner, Observer {
                         textView.text = it
                     })*/


            /*   sliderItem = SliderItem()
                       sliderItem.imageUrl = R.drawable.pd_1
                       sliderItemList.add(sliderItem)
                       sliderItem = SliderItem()
                       sliderItem.imageUrl = R.drawable.pd_2
                       sliderItemList.add(sliderItem)*/
            val adapterr = SliderAdapterExample(this)

            sliderView!!.setSliderAdapter(adapterr)


            sliderView?.setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!

            sliderView?.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            sliderView?.autoCycleDirection =
                    SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            sliderView?.indicatorSelectedColor = Color.WHITE
            sliderView?.indicatorUnselectedColor = Color.BLACK
            sliderView?.scrollTimeInSec =
                    sliderItemList.size - 1 //set scroll delay in seconds :

            //sliderView?.startAutoCycle()
            adapterr.renewItems(sliderItemList)

            //-------------------------------
            itemName.text = mModelData!!.productName
            storeName.text = intent.getStringExtra("productCompany")
            tv_fullfilledby.text = mModelData!!.storeName
            aboutValue.text = mModelData!!.productDescription



            if (mModelData?.price != null && mModelData?.offerPrice != null) {
                savings = mModelData?.price?.toInt()!! - mModelData?.offerPrice!!.toInt()
            }

            offerPrice.text = resources.getString(R.string.ruppee) + "" +
                    mModelData!!.price + "/Savings: " + resources.getString(R.string.ruppee) + savings
            priceAfterDiscount.text =
                    resources.getString(R.string.ruppee) + "" + mModelData!!.offerPrice
            Glide.with(this)
                    .load(mModelData!!.images.get(0))
                    .fitCenter()
                    .into(selectedImage)
            val adapter = ItemImagesAdapter(this, mModelData!!.images, this)
            imagesRecyclerView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
            )
            imagesRecyclerView.setHasFixedSize(true)
            imagesRecyclerView.adapter = adapter


            if (mModelData!!.cartQuantity == 0) {

            } else {
                count = mModelData!!.cartQuantity
                countValuee.text = count.toString()
            }
            countValuee.text = count.toString()
            if (countValuee.text.toString().toInt() == 0) {
                addToCartTxtView.visibility = View.GONE
            } else if (countValuee.text.toString().toInt() > 0) {
                addToCartTxtView.visibility = View.VISIBLE
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()


            //set SpecificationsAdapter


            specificationList.addAll(mModelData!!.specifications)
            var specificationsAdapter: SpecificationsAdapter =
                    SpecificationsAdapter(this, specificationList)

            var linearLayoutManager = LinearLayoutManager(this)
            specifiations_rv.layoutManager = linearLayoutManager


            specifiations_rv.adapter = specificationsAdapter
        })

        cartIcon.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        viewmodel?.addToCartResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("stores response", Gson().toJson(user))
            val mModellData: AddToCartModel = user
            viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
            )

            SessionTwiclo(this).storeId = mModelData!!.storeId
            if (type.equals("buy")) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                showToast(mModellData.message)
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })
        viewmodel?.clearCartResponse?.observe(this, Observer { user ->
            //  dismissIOSProgress()
            Log.e("clear  response", Gson().toJson(user))
            val mModellData: ClearCartModel = user
            var customIdsList = ArrayList<String>()
            MainActivity.addCartTempList!!.clear()
            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = mModelData!!.productId
            addCartInputModel.quantity = countValuee.text.toString()
            addCartInputModel.message = "add product"
            addCartInputModel.customizeSubCatId = customIdsList
            addCartInputModel.isCustomize = "0"
            MainActivity.addCartTempList!!.add(addCartInputModel)
            viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!
            )
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        plusLayy.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                count++
                countValuee.text = count.toString()
                addToCartTxtView.visibility = View.VISIBLE
            } else {
                showLoginDialog("Please Login to proceed")
            }


        }
        minusLayy.setOnClickListener {
            if (SessionTwiclo(this).isLoggedIn) {
                if (count >= 1) {
                    count--
                    countValuee.text = count.toString()
                    if (count == 0) {
                        addToCartTxtView.visibility = View.GONE
                    }
                }
            } else {
                showLoginDialog("Please Login to proceed")
            }

        }
    }

    override fun onResume() {
        super.onResume()
        showIOSProgress()
        if (SessionTwiclo(this).isLoggedIn) {
            if (isNetworkConnected) {
                showIOSProgress()
                viewmodel!!.getProductDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        intent.getStringExtra("productId")
                )
            } else {
                showInternetToast()
            }

            viewmodel?.getCartCountApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken
            )

        } else {
            showLoginDialog("Please Login to proceed")
        }


    }

    override fun onSelectedImageClick(position: Int) {
        if (mModelData != null) {

            sliderView.currentPagePosition = position
            Glide.with(this)
                    .load(mModelData!!.images.get(position))
                    .fitCenter()
                    .into(selectedImage)
        }
    }

    private fun showLoginDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { dialogInterface, which ->
//            startActivity(
//                Intent(this, LoginActivity::class.java)
//            )


        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}