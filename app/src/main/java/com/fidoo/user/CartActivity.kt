package com.fidoo.user

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.adapter.CartItemsAdapter
import com.fidoo.user.adapter.StoreCustomItemsAdapter
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.interfaces.CustomCartAddRemoveClick
import com.fidoo.user.ui.MainActivity.Companion.addCartTempList
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.view.TrackOrderActivity
import com.fidoo.user.view.address.SavedAddressesActivity
import com.fidoo.user.viewmodels.CartViewModel
import com.fidoo.user.viewmodels.StoreDetailsViewModel
import com.fidoo.user.viewmodels.TrackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_cart.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

class CartActivity : BaseActivity(),
        AdapterCartAddRemoveClick,
        AdapterClick,
        CustomCartAddRemoveClick,
        AdapterCustomRadioClick, PaymentResultListener {

    var viewmodel: CartViewModel? = null
    var totalAmount: Double = 0.0
    var storeViewModel: StoreDetailsViewModel? = null
    var finalPrice: Double = 0.0
    var storeId: String = ""
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    private var categoryy: ArrayList<CustomListModel>? = null
    private var mModelDataTemp: CustomizeProductResponseModel? = null
    var customIdsList: ArrayList<String>? = null
    var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
    var totalAmountWithouttax: Double = 0.0
    var noOfItems: Int = 0
    var tempOrderId: String = ""
    var tempProductId: String? = ""
    var isLessContactClicked: Boolean = true
    var tempOfferPrice: String? = ""
    var filePathTemp: String = ""
    var count: Int = 1
    var isPrescriptionRequire: String = ""
    var distanceViewModel: TrackViewModel? = null
    private val co = Checkout()
    var storelocation: ArrayList<StoreDetailsModel>? = null
    var storeCustomerDistance = ""
    var isSelected: String = ""



    companion object {
        var selectedAddressId: String = ""
        var selectedAddressName: String = ""
        var selectedCouponId: String = ""
        var selectedCouponName: String = ""
        var userLat: String = ""
        var userLong: String = ""
        //var storeCustomerDistance: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        storeId = intent.getStringExtra("store_id").toString()

        viewmodel = ViewModelProviders.of(this).get(CartViewModel::class.java)
        storeViewModel = ViewModelProviders.of(this).get(StoreDetailsViewModel::class.java)

        behavior = BottomSheetBehavior.from(bottom_sheet)
        selectedAddressId = ""
        selectedAddressName = ""
        selectedCouponName = ""
        selectedCouponId = ""
        storeCustomerDistance = ""
        userLat = SessionTwiclo(this).userLat
        userLong = SessionTwiclo(this).userLng

        var deliveryOption: String = "contact"

        customIdsList = ArrayList<String>()
        customIdsListTemp = ArrayList<CustomCheckBoxModel>()
        addCartTempList = ArrayList<AddCartInputModel>()

        //For Faster checkout of RazorPay
        Checkout.preload(applicationContext)
        co.setKeyID("rzp_live_iceNLz5pb15jtP")
        storeId = intent.getStringExtra("store_id").toString()


        storeViewModel?.getStoreDetails(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            storeId,
            "",
            intent.getStringExtra("catId")
        )

        backIcon.setOnClickListener {
            finish()
        }

        cb_no_contact_delivery.setOnCheckedChangeListener { _, b ->
            deliveryOption = if(b) {
                "contactless"
            } else {
                "contact"
            }
        }

        storeViewModel?.getStoreDetailsApi?.observe(this, Observer {
            // calculateStoreCustomerDistance(it.storeLatitude+","+it.storeLongitude, SessionTwiclo(this).userLat+","+SessionTwiclo(this).userLng)

            viewmodel?.getCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                userLat,
                userLong
            )
        })

        viewmodel?.addToCartResponse?.observe(this, { user ->

            linear_progress_indicator.visibility = View.GONE
            dismissIOSProgress()
            Log.e("stores response", Gson().toJson(user))
            val mModelData: AddToCartModel = user
            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }

            storeViewModel?.getStoreDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                intent.getStringExtra("storeId"),
                "",
                intent.getStringExtra("catId")

            )

            storeViewModel?.getStoreDetailsApi?.observe(this, Observer {


            })

            viewmodel?.getCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                userLat,
                userLong
            )




            //Log.e("DISTANCE1",storeCustomerDistance)

            showToast(mModelData.message)
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.customizeProductResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("stores response", Gson().toJson(user))
            mModelDataTemp = user

            categoryy = ArrayList()

            for (i in 0..mModelDataTemp?.category?.size!! - 1) {
                if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
                    var customListModel: CustomListModel? =
                        CustomListModel()
                    customListModel!!.category = mModelDataTemp?.category?.get(i)!!.catId
                    customListModel.id =
                        mModelDataTemp?.category?.get(i)!!.subCat.get(0).id.toInt()
                    customListModel.price = mModelDataTemp?.category?.get(i)!!.subCat.get(0).price
                    categoryy!!.add(customListModel)
                } else {

                }
            }

            var tempPrice: Double? = 0.0
            for (i in 0..customIdsListTemp!!.size - 1) {
                tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
            }

            for (i in 0..categoryy!!.size - 1) {
                tempPrice = tempPrice!! + categoryy!!.get(i).price.toDouble()
            }
            tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!

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
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        delivery_address_lay.setOnClickListener {
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))

            } else {
                startActivity(
                    Intent(this, SavedAddressesActivity::class.java).putExtra(
                        "type",
                        "order"
                    )
                )
            }
        }

        cash_lay.setOnClickListener {
            isSelected = "cod"
            cash_lay.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            img_cash.setColorFilter(resources.getColor(R.color.colorPrimary))
            tv_cash.setTextColor(resources.getColor(R.color.white))

            online_lay.background = ResourcesCompat.getDrawable(resources, R.drawable.black_rounded_solid, null)
            tv_online.setTextColor(resources.getColor(R.color.grey))
            img_online.setImageResource(R.drawable.online_pay_grey)
        }



        online_lay.setOnClickListener {
            isSelected = "online"
            online_lay.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            img_online.setColorFilter(resources.getColor(R.color.colorPrimary))
            tv_online.setTextColor(resources.getColor(R.color.white))
            cash_lay.background = ResourcesCompat.getDrawable(resources, R.drawable.black_rounded_solid, null)
            tv_cash.setTextColor(resources.getColor(R.color.grey))
            img_cash.setImageResource(R.drawable.cash_icon_grey)
        }

        delivery_address_lay.setOnClickListener {
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))

            } else {
                startActivity(
                    Intent(this, SavedAddressesActivity::class.java).putExtra(
                        "type",
                        "order"
                    )
                )
            }
        }

        customAddBtn.setOnClickListener {
            if (isNetworkConnected) {
                var mCartId: String? = null
                for (i in 0..categoryy!!.size - 1) {
                    customIdsList!!.add(categoryy!!.get(i).id.toString())
                }

                viewmodel?.getCartDetailsResponse?.observe(this , Observer { user->
                    val mCartModelData : CartModel = user
                    for (i in 0 until user.cart.size) {
                        mCartId = mCartModelData.cart[i].cart_id
                    }


                })
                Log.e("customIdsList", customIdsList.toString())
                showIOSProgress()
                SessionTwiclo(this).storeId = intent.getStringExtra("storeId")

                addCartTempList!!.clear()
                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = tempProductId
                addCartInputModel.quantity = countValuee.text.toString()
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "1"
                addCartTempList!!.add(addCartInputModel)
                viewmodel!!.addToCartApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    addCartTempList!!,
                    mCartId!!

                )
            } else {
                showInternetToast()
            }


        }


        viewmodel?.addRemoveCartResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            viewmodel?.getCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                userLat,
                userLong
            )


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.deleteCartResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            Toast.makeText(this, user.message, Toast.LENGTH_LONG).show()
            viewmodel?.getCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                userLat,
                userLong
            )
        })

        viewmodel?.failureResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            showToast(user)


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.getCartDetailsResponse?.observe(this, { user ->
            dismissIOSProgress()
            linear_progress_indicator.visibility = View.GONE
            if (!user.error) {
                if (user.cart != null) {
                    Log.e("cart details response", Gson().toJson(user))

                    isPrescriptionRequire = user.cart[0].isPrescription
                    if (user.cart[0].isPrescription.equals("1")) {
                        //prescriptionLay.visibility = View.VISIBLE

                    } else {
                        //prescriptionLay.visibility = View.GONE
                    }

                    if (user.cart[0].cod.equals("1")) {
                        isSelected = "cod"

                        cash_lay.visibility = View.VISIBLE
                        //cashOnDeliveryRadioBtn.visibility = View.VISIBLE
                    } else {
                        cash_lay.visibility = View.GONE
                        //cashOnDeliveryRadioBtn.isChecked = false
                        //cashOnDeliveryRadioBtn.visibility = View.GONE
                    }

                    if (user.cart[0].online.equals("1")) {
                        online_lay.visibility = View.VISIBLE
                        isSelected = "online"
                        online_lay.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                        tv_online.setTextColor(resources.getColor(R.color.white))
                        img_online.setColorFilter(Color.rgb(255, 255, 255))
                    } else {
                        //onlineRadioBtn.isChecked = false
                        online_lay.visibility = View.GONE

                    }




                    if (user.cart[0].online.equals("1") && user.cart.get(0).cod.equals("1")) {
                        //cashOnDeliveryRadioBtn.isChecked = false
                        //onlineRadioBtn.isChecked = true
                    }
                    if (user.cart[0].online.equals("0") && user.cart.get(0).cod.equals("1")) {
                        //cashOnDeliveryRadioBtn.isChecked = true
                        //onlineRadioBtn.isChecked = false
                    }
                    if (user.cart[0].online.equals("1") && user.cart.get(0).cod.equals("0")) {
                        //cashOnDeliveryRadioBtn.isChecked = false
                        //onlineRadioBtn.isChecked = true
                    }

                    val mModelData: CartModel = user
                    val adapter = CartItemsAdapter(this, mModelData.cart, this, this)
                    cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
                    cartItemsRecyclerView.setHasFixedSize(true)
                    cartItemsRecyclerView.adapter = adapter
                    var items: String? = ""
                    totalAmount = 0.0
                    totalAmountWithouttax = 0.0
                    noOfItems = 0
                    //nameLabel.text = user.cart.get(0).storeName
                    for (i in 0 until mModelData.cart.size) {
                        noOfItems += mModelData.cart[i].quantity.toString().toInt()
                    }
                    var tempp: Double? = 0.0
                    for (i in 0 until mModelData.cart.size) {
                        tempp = 0.0
                        for (j in 0 until mModelData.cart[i].customizeItem.size) {
                            tempp = tempp!! + mModelData.cart[i].customizeItem.get(j).price.toDouble()

                        }
                        tempp = tempp!! + mModelData.cart[i].offerPrice.toDouble()

                        totalAmount += (mModelData.cart[i].quantity.toString().toDouble() * tempp)
                        //showToast(""+totalAmount)
                    }


                    val deliveryChargeWithTax = mModelData.deliveryCharge + mModelData.tax
                    //showToast(""+deliveryChargeWithTax)


                    //showToast(totalAmount.toString())
                    //totalAmountWithouttax = totalAmount
                    //showToast(totalAmountWithouttax.toString())
                    //- mModelData.deliveryCharge.toString().toDouble() - mModelData.tax.toString().toDouble()

                    // Item Total
                    tv_subtotal.text = resources.getString(R.string.ruppee) + totalAmount


                    totalAmount = totalAmount + deliveryChargeWithTax
                    //showToast(totalAmount.toString())

                    totalAmount -= mModelData.deliveryDiscount
                    finalPrice = totalAmount


                    //numberOfItemsValue.text = noOfItems.toString() + " Items"

                    tv_place_order.text = resources.getString(R.string.ruppee) + finalPrice.toFloat().toString()
                    tv_grand_total.text = resources.getString(R.string.ruppee) + finalPrice.toFloat().toString()

                    Log.e("Bottom Price", tv_place_order.text.toString())
                    Log.e("Grand Total", tv_grand_total.text.toString())
                    //showToast(mModelData.deliveryDiscount)
                    //showToast(finalPrice.toString())
                    tv_delivery_charges.text = resources.getString(R.string.ruppee) + deliveryChargeWithTax

                    /*delivery_breakout_info.setOnClickListener {
                        val balloon = createBalloon(baseContext) {
                            setArrowSize(10)
                            setWidthRatio(0.5f)
                            setHeight(65)
                            setArrowPosition(0.1f)
                            setCornerRadius(4f)
                            setAlpha(1.0f)
                            setText("Delivery Charges + Taxes")
                            setTextColorResource(R.color.white)
                            setBackgroundColorResource(R.color.primary_color)
                            setBalloonAnimation(BalloonAnimation.FADE)
                            setLifecycleOwner(lifecycleOwner)
                            setAutoDismissDuration(2000)

                        }
                        delivery_breakout_info.showAlignTop(balloon)
                    }*/
                    if (mModelData.deliveryDiscount == 0){
                        //delivery_coupon_name.visibility = View.GONE
                        //delivery_coupon_value.visibility = View.GONE
                    }
                    /* delivery_coupon_name.text = "Delivery Discount ( -" +mModelData.delivery_coupon_name +")"
                    delivery_coupon_value.text = resources.getString(R.string.ruppee) + mModelData.deliveryDiscount
                    discountValue.text = resources.getString(R.string.ruppee) + mModelData.discount_amount.toString()
                    discountLabel.text = "Cart Discount (" + mModelData.coupon_name +")"*/

                    //totalAmountBottom.text = resources.getString(R.string.ruppee) + finalPrice.toString()

                    if (!mModelData.coupon_id.equals("")) {


                        /*appliedpromoValue.visibility = View.VISIBLE
                        appliedpromoDesc.visibility = View.VISIBLE
                        removeBtn.visibility = View.VISIBLE
                        view333.visibility = View.VISIBLE
                        discountLabel.visibility = View.VISIBLE
                        discountValue.visibility = View.VISIBLE
                        promoValue.visibility = View.GONE
                        applyBtn.visibility = View.GONE
                        getOfferTxt.visibility = View.GONE
                        offerIcon.visibility = View.GONE
                        appliedpromoValue.text = mModelData.coupon_name
                        totalAmount = totalAmount - mModelData.discount_amount.toDouble()
                        totalAmountBottom.text = resources.getString(R.string.ruppee) + totalAmount.toString()
                        grandPrice.text = resources.getString(R.string.ruppee) + totalAmount.toString()*/
                        //showToast("Offer applied successfully")
                    } else {
                        //discountLabel.visibility = View.GONE
                        //discountValue.visibility = View.GONE
                    }


                    noItemsTxt.visibility = View.GONE
                } else {
                    noItemsTxt.visibility = View.VISIBLE

                    //showToast("No items")
                    //finish()
                }
            } else {
                if (user.errorCode == 101) {
                    showAlertDialog(this)
                }
            }


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.addRemoveCartResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            viewmodel?.getCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                userLat,
                userLong
            )


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.paymentResponse?.observe(this, { user ->
            dismissIOSProgress()
            tempOrderId = user.orderId
            SessionTwiclo(this).storeId = ""
            /*   SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                   .setTitleText("Order Placed Successfully!")
                   .setContentText("Won't be able to recover this file!")
                   .setConfirmText("Go to Home")
                   .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                   }
                   .show()*/
            Log.e("cart response", Gson().toJson(user))
            showToast("Order placed successfully")

            startActivity(
                Intent(this, TrackOrderActivity::class.java).putExtra(
                    "orderId",
                    user.orderId
                ).putExtra(
                    "delivery_boy_name",
                    ""
                ).putExtra(
                    "delivery_boy_mobile",
                    ""
                ).putExtra(
                    "type",
                    ""
                )
            )
            finishAffinity()
        })

        viewmodel?.uploadPrescriptionResponse?.observe(this, { user ->

            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))

            showToast(user.message)
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.appplyPromoResponse?.observe(this, { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            viewmodel?.getCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                userLat,
                userLong
            )
            if (user.couponApply.equals("2")) {


                /*appliedpromoValue.visibility = View.VISIBLE
                appliedpromoDesc.visibility = View.VISIBLE
                removeBtn.visibility = View.VISIBLE
                view333.visibility = View.VISIBLE
                promoValue.visibility = View.GONE
                applyBtn.visibility = View.GONE
                getOfferTxt.visibility = View.GONE
                offerIcon.visibility = View.GONE
                appliedpromoValue.text = promoValue.text.toString()
                discountValue.text = resources.getString(R.string.ruppee) + user.discountAmount*/

                totalAmount = totalAmount - user.discountAmount.toDouble()

                tv_place_order.text = resources.getString(R.string.ruppee) + finalPrice.toFloat().toString()
                tv_grand_total.text = resources.getString(R.string.ruppee) + finalPrice.toFloat().toString()
                Log.e("Grand Total after promo", tv_grand_total.text.toString())
                Log.e("Final Price after promo", tv_place_order.text.toString())
                //discountLabel.visibility = View.VISIBLE
                //discountValue.visibility = View.VISIBLE
                //showToast("Offer applied successfully")
            } else {

                /*appliedpromoValue.visibility = View.GONE
                appliedpromoDesc.visibility = View.GONE
                removeBtn.visibility = View.GONE
                view333.visibility = View.GONE
                promoValue.visibility = View.VISIBLE
                // applyBtn.visibility = View.VISIBLE
                getOfferTxt.visibility = View.VISIBLE
                offerIcon.visibility = View.VISIBLE*/
                showToast(user.message)
            }

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        tv_place_order.setOnClickListener {
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))
            } else {

                if (SessionTwiclo(this).userAddressId.equals("")) {
                    showToast("Please select your address")
                } else if (isPrescriptionRequire == "1") {
                    if (filePathTemp.equals("")) {
                        showToast("Please upload prescription")

                    } else {
                        if (isNetworkConnected) {
                            showIOSProgress()
                            viewmodel?.orderPlaceApi(SessionTwiclo(this).loggedInUserDetail.accountId,
                                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                                    finalPrice.toFloat().toString(),
                                    deliveryOption,
                                    SessionTwiclo(this).userAddressId,
                                    "",
                                    ed_delivery_instructions.text.toString(),
                                    isSelected
                            )

                        } else {
                            showInternetToast()
                        }
                    }

                } else {
                    showIOSProgress()
                    viewmodel?.orderPlaceApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            finalPrice.toFloat().toString(),
                            deliveryOption,
                            SessionTwiclo(this).userAddressId,
                            "",
                            ed_delivery_instructions.text.toString(),
                            isSelected
                    )
                }
            }
        }

        viewmodel?.orderPlaceResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            if (isSelected == "online") {
                if (user.error.equals(true)){
                    if(user.storeOffline == 1){
                        showToast(user.message)
                    }
                    if (user.productOutOfStock == 1){
                        showToast(user.message)
                    }
                    if (user.storeOffline == 1 && user.productOutOfStock == 1){
                        showToast("This Store/Item is not available at this moment")
                    }
                }else{
                    startPayment()
                    tempOrderId = user.orderId
                }
                //launchPayUMoneyFlow()


            } else {
                if (isNetworkConnected) {
                    viewmodel?.paymentApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            user.orderId,
                            "",
                            "",
                            "cash"
                    )

                } else {
                    showInternetToast()
                }

            }
            Log.e("cart response", Gson().toJson(user))
        })


    }

    private fun startPayment() {

        /*
         * Instantiate Checkout
         */
        val activity: Activity = this
        val co = Checkout()
        //var razorpayId = "qwerty"


        val razorpayOrderId = OrderPlaceModel().razorPayOrderId

        try {
            val options = JSONObject()
            options.put("name", "FIDOO")
            options.put("description", "Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://fidoo.in/include/assets/front/img/logo_sticky.svg")
            options.put("theme.color", "#339347");
            options.put("currency", "INR")
            options.put("order_id", razorpayOrderId)
            //Log.e("RAZORPAY", "")
            var amount = 0.0f
            try {
                Log.e("totalAmount", totalAmount.toString())
                amount = totalAmount.toFloat()
                //showToast(""+amount)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            options.put("amount", amount*100)

            val prefill = JSONObject()
            prefill.put("email", SessionTwiclo(this).profileDetail.account.emailid)
            prefill.put("contact", SessionTwiclo(this).profileDetail.account.country_code+ SessionTwiclo(this).profileDetail.account.userName)

            options.put("prefill", prefill)
            co.open(activity, options)
        }catch (e: java.lang.Exception){
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }



    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
        }catch (e: java.lang.Exception){
            Log.e("onError", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
            viewmodel?.paymentApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    tempOrderId,
                    razorpayPaymentId!!,
                    "",
                    "online"
            )



        }catch (e: java.lang.Exception){
            Log.e("onSuccess", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onAddItemClick(productId: String, quantity: String, offerPrice: String, isCustomize: String, productCustomizeId: String, cart_id: String) {

        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))

        } else {


            if (!quantity.equals("")) {
                tempOfferPrice = offerPrice
                tempProductId = productId
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle("Your previous customization")
                //set message for alert dialog
                builder.setMessage(quantity)
                // builder.setIcon(android.R.drawable.ic_dialog_alert)

                //performing positive action
                builder.setPositiveButton("I'LL CHOOSE") { dialogInterface, which ->

                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }

                    //  tempProductId = productId
                    showIOSProgress()
                    customIdsList!!.clear()
                    if (productId != null) {
                        viewmodel?.customizeProductApi(
                            SessionTwiclo(
                                this
                            ).loggedInUserDetail.accountId,
                            SessionTwiclo(
                                this
                            ).loggedInUserDetail.accessToken, productId
                        )
                    }
                    //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                }

                //performing negative action
                builder.setNegativeButton("REPEAT") { dialogInterface, which ->
                    //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
                    showIOSProgress()
                    viewmodel?.addRemoveCartDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        productId,
                        "add",
                        isCustomize,
                        productCustomizeId,
                        cart_id,
                        customIdsList!!
                    )

                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(true)
                alertDialog.show()
            } else {
                showIOSProgress()
                viewmodel?.addRemoveCartDetails(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    productId,
                    "add",
                    isCustomize,
                    productCustomizeId,
                    cart_id!!,
                    customIdsList!!
                )

            }

        }
    }

    override fun onRemoveItemClick(productId: String, quantity: String, isCustomize: String, productCustomizeId: String, cart_id: String) {
        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))

        } else {
            showIOSProgress()
            viewmodel?.addRemoveCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                productId,
                "remove",
                isCustomize,
                productCustomizeId,
                cart_id,
                customIdsList!!
            )
        }


    }

    override fun onResume() {
        super.onResume()

        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))

        } else {
            showIOSProgress()
            storeViewModel?.getStoreDetailsApi?.observe(this, Observer {
                val store_latitude = it.storeLatitude
                val store_longitude = it.storeLongitude
                //calculateStoreCustomerDistance(it.storeLatitude+","+it.storeLongitude, SessionTwiclo(this).userLat+","+SessionTwiclo(this).userLng)

            })

            viewmodel?.getCartDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                userLat,
                userLong
            )



        }
//        selectedAddressValue.text = selectedAddressName
        if (SessionTwiclo(this).userAddressId != "") {
            tv_delivery_address.text = SessionTwiclo(
                this
            ).userAddress
        }


        if (!selectedCouponName.equals("")) {
            tv_coupon.setText(selectedCouponName)
            viewmodel?.applyPromoApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                selectedCouponName
            )
        }

    }

    override fun onItemClick(productId: String?, type: String?, count: String?, offerPrice: String?, customize_count: Int?, productType: String?, cart_id: String?) {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Remove")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to remove this item?")
        builder.setIcon(R.drawable.about_icon)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->


            if (isNetworkConnected) {
                showIOSProgress()
                if (cart_id != null) {
                    viewmodel?.deleteCartDetails(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken, productId!!,
                        cart_id
                    )
                }
            } else {
                showInternetToast()
            }

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

    fun uplaodGallaryImage(mImagePth: String?) {
        /* if (!isNetworkConnected()) {
             showToast(resources.getString(R.string.provide_internet))
             return
         }*/
        Log.e("mImagePth", mImagePth.toString())
        Log.e("accountId", SessionTwiclo(
            this
        ).loggedInUserDetail.accountId.toString())
        Log.e("accessToken", SessionTwiclo(
            this
        ).loggedInUserDetail.accessToken.toString())
        var mImageParts: MultipartBody.Part? = null
        // creating image format for upload
        mImageParts = if (!TextUtils.isEmpty(mImagePth)) {
            // Pass it like this
            val file = File(mImagePth)
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("document", file.name, requestFile)
        } else {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            MultipartBody.Part.createFormData("document", "", requestFile)
        }


        // Parameter request body
        val accountId =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                SessionTwiclo(this).loggedInUserDetail.accountId
            )  // Parameter request body
        val accessToken =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                SessionTwiclo(this).loggedInUserDetail.accessToken
            )  // Parameter request body
        /* val orderId =
             RequestBody.create(MediaType.parse("text/plain"), orderId)*/
        // dismissIOSProgress()
        showIOSProgress()
        viewmodel?.uploadPrescriptionImage(
            accountId,
            accessToken, mImageParts
        )


    }

    override fun onIdSelected(productId: String?, type: String?, price: String?) {
        if (type.equals("select")) {

            if (productId != null) {
                customIdsList!!.add(productId)
            }
            var customCheckBoxModel: CustomCheckBoxModel =
                CustomCheckBoxModel()
            customCheckBoxModel.id = productId
            customCheckBoxModel.price = price

            customIdsListTemp!!.add(customCheckBoxModel)
        } else {

            for (i in 0..customIdsList!!.size - 1) {
                if (customIdsList!!.get(i).equals(productId)) {
                    customIdsList!!.removeAt(i)
                    customIdsListTemp!!.removeAt(i)
                    break
                }
            }


        }

        var tempPrice: Double? = 0.0
        for (i in 0..customIdsListTemp!!.size - 1) {
            tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
        }

        for (i in 0..categoryy!!.size - 1) {
            tempPrice = tempPrice!! + categoryy!!.get(i).price.toDouble()
        }

        tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
        customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()

    }

    override fun onCustomRadioClick(checkedId: String?, position: String?)  {

        var tempCat: String? = ""
        var tempPrice: String? = ""
        for (i in 0..mModelDataTemp?.category!!.size - 1) {

            for (j in 0..mModelDataTemp?.category!!.get(i).subCat.size - 1) {

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
        for (i in 0..categoryy!!.size - 1) {

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
        for (i in 0..customIdsListTemp!!.size - 1) {
            tempPricee = tempPricee!! + customIdsListTemp!!.get(i).price.toDouble()
        }

        for (i in 0..categoryy!!.size - 1) {
            tempPricee = tempPricee!! + categoryy!!.get(i).price.toDouble()
        }
        tempPricee = tempOfferPrice!!.toDouble() + tempPricee!!

        customAddBtn.text = resources.getString(R.string.ruppee) + tempPricee.toString()


    }
}