package com.fidoo.user.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.*
import com.fidoo.user.adapter.CategoryAdapter
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.FragmentHomeBinding
import com.fidoo.user.adapter.SliderAdapter
import com.fidoo.user.adapter.SliderAdapter.ClickCart
import com.fidoo.user.data.model.BannerModel
import com.fidoo.user.data.model.CartCountModel
import com.fidoo.user.data.model.HomeServicesModel
import com.fidoo.user.grocery.activity.GroceryItemsActivity
import com.fidoo.user.utils.AUTOCOMPLETE_REQUEST_CODE
import com.fidoo.user.utils.CardSliderLayoutManager
import com.fidoo.user.utils.CardSnapHelper
import com.fidoo.user.view.address.SavedAddressesActivity
import com.fidoo.user.viewmodels.HomeFragmentViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.robin.locationgetter.EasyLocation
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    lateinit var mView : View

    var viewmodel : HomeFragmentViewModel? = null

    private var _progressDlg: ProgressDialog? = null
    var fragmentHomeBinding: FragmentHomeBinding? = null
    private var currentPosition = 0
    private var layoutManger: CardSliderLayoutManager? = null

    companion object {
        var service_id:String?=""
        var service_name:String?=""
        var itemPosition:Int?=0
    }


    var categoryAdapter:CategoryAdapter?=null
    private val pics = intArrayOf(
        R.drawable.medicine,
        R.drawable.electronics,
        R.drawable.grocery,
        //  R.drawable.grocery,
        R.drawable.food,
        R.drawable.pet,
        R.drawable.meat,
        R.drawable.paan,
        R.drawable.fruits,
        R.drawable.cake,
        R.drawable.stationary
        //  R.drawable.wellness
    )

    private val sliderAdapter = SliderAdapter(
            pics, 10, object : ClickCart{
        override fun cartOnClick(view: View?) {
            //  service_name?.let { Log.d("fdfdfd", it) }
            startActivity(
                    Intent(requireActivity(), StoreListActivity::class.java).putExtra(
                            "serviceId", service_id
                    ).putExtra("serviceName", service_name)
            )
        }
    })


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_home,
                container,
                false
        )

//        activity?.statusBarTransparent()

        viewmodel = ViewModelProviders.of(requireActivity()).get(HomeFragmentViewModel::class.java)

        // to initialize the main card view slider
        initRecyclerView()


        if ((activity as MainActivity).isNetworkConnected) {

            if (SessionTwiclo(context).isLoggedIn){


                viewmodel?.getBanners(
                        SessionTwiclo(context).loggedInUserDetail.accountId,
                        SessionTwiclo(context).loggedInUserDetail.accessToken,
                        "1"
                )
            }else{
                viewmodel?.getBanners(
                        "",
                        "",
                        "1"
                )
            }


        } else {
            (activity as MainActivity).showInternetToast()
        }


        fragmentHomeBinding?.userAddress?.text = SessionTwiclo(context).userAddress

        /* fragmentHomeBinding?.userAddress?.setOnClickListener {
             onSearchCalled()
         }*/
        var sliderItem = com.fidoo.user.data.SliderItem()

        viewmodel?.failureResponse?.observe(requireActivity(), { user ->
            //dismissIOSProgress()

            if (_progressDlg != null) {
                _progressDlg!!.dismiss()
                _progressDlg = null
            }
            if (context != null) {
                Toast.makeText(context, user, Toast.LENGTH_SHORT).show()
            }
            Log.e("cart response", Gson().toJson(user))
            //showToast(user)


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.bannersResponse?.observe(requireActivity(), { user ->
            // dismissIOSProgress()

            if (!user.error) {
                val mModelData: BannerModel = user
                Log.e("bannerResponse", Gson().toJson(mModelData))
                val sliderItemList: ArrayList<com.fidoo.user.data.SliderItem> = ArrayList()
                if (mModelData.banner != null) {
                    for (i in 0 until mModelData.banner.size) {
                        sliderItem = com.fidoo.user.data.SliderItem()
                        sliderItem.imageUrl = mModelData.banner.get(i)
                        sliderItemList.add(sliderItem)
                    }

                    val adapterr = com.fidoo.user.adapter.SliderAdapterExample(activity)

                    fragmentHomeBinding?.sliderView?.setSliderAdapter(adapterr)

                    fragmentHomeBinding?.sliderView?.setIndicatorAnimation(IndicatorAnimationType.SWAP) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!

                    fragmentHomeBinding?.sliderView?.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    /*fragmentHomeBinding?.sliderView?.autoCycleDirection =
                        SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH*/
                    fragmentHomeBinding?.sliderView?.indicatorSelectedColor = Color.WHITE
                    fragmentHomeBinding?.sliderView?.indicatorUnselectedColor = Color.BLACK
                    fragmentHomeBinding?.sliderView?.scrollTimeInSec = sliderItemList.size - 1 //set scroll delay in seconds :
                    fragmentHomeBinding?.sliderView?.startAutoCycle()
                    adapterr.renewItems(sliderItemList)
                }
            } else {


            }
            //   goForVerificationScreen(VerificationActivity::class.java,mModelData.accessToken,mModelData.account.id,mobileNoEdt.text.toString())

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        fragmentHomeBinding?.addressLay?.setOnClickListener {
            if (SessionTwiclo(context).isLoggedIn){
                startActivityForResult(Intent(context, SavedAddressesActivity::class.java)
                    .putExtra("type", "address"
                ),AUTOCOMPLETE_REQUEST_CODE
                )
            }else{
                showLoginDialog("Please login to proceed")
            }

        }

        viewmodel?.homeServicesResponse?.observe(requireActivity(), Observer { user ->
            // dismissIOSProgress()
            if (_progressDlg != null) {
                _progressDlg!!.dismiss()
                _progressDlg = null
            }
            if (!user.error) {
                val mModelData: HomeServicesModel = user
                Log.e("servicesResponse", Gson().toJson(mModelData))
                if (mModelData.serviceList != null) {
                    if (activity != null) {
                        /*val adapter = CategoriesAdapter(requireActivity(), mModelData.serviceList)
                        fragmentHomeBinding?.categoriesRecyclerView?.layoutManager =
                            GridLayoutManager(
                                context,
                                2
                            )
                        fragmentHomeBinding?.categoriesRecyclerView?.setHasFixedSize(true)
                        fragmentHomeBinding?.categoriesRecyclerView?.adapter = adapter*/


                        categoryAdapter = CategoryAdapter(
                                requireActivity(),
                                mModelData.serviceList,
                                object : CategoryAdapter.ItemClick {
                                    override fun onItemClick(
                                            pos: Int,
                                            serviceList: HomeServicesModel.ServiceList
                                    ) {

                                        service_id = serviceList.id
                                        service_name=serviceList.serviceName


                                    }

                                }
                        )

                        fragmentHomeBinding?.categorySmallRecyclerview?.layoutManager =
                                GridLayoutManager(
                                        activity,
                                        4,
                                        GridLayoutManager.VERTICAL,
                                        false
                                )
                        fragmentHomeBinding?.categorySmallRecyclerview?.setHasFixedSize(true)
                        fragmentHomeBinding?.categorySmallRecyclerview?.adapter = categoryAdapter
                        categoryAdapter?.updateReceiptsList(itemPosition!!)
                        fragmentHomeBinding?.categorySmallRecyclerview?.smoothScrollToPosition(
                                itemPosition!!
                        )

                    }
                }

                if (mModelData.defaultAddress != null) {

                    // SessionTwiclo(context).userAddress = mModelData.defaultAddress.addressType
                    //SessionTwiclo(context).userAddressId = mModelData.defaultAddress.address_id
                    //SessionTwiclo(context).userLat = mModelData.defaultAddress.latitude
                    // SessionTwiclo(context).userLng = mModelData.defaultAddress.longitude
                    // fragmentHomeBinding?.userAddress?.text = SessionTwiclo(context).userAddress

                }
            } else {
                if (user.errorCode == 101) {
                    //showAlertDialog(context!!)
                }
            }
        })

        viewmodel?.cartCountResponse?.observe(requireActivity(), { user ->
            // dismissIOSProgress()
            if (_progressDlg != null) {
                _progressDlg!!.dismiss()
                _progressDlg = null
            }

            if (!user.error) {
                val mModelData: CartCountModel = user
                Log.e("countResponse", Gson().toJson(mModelData))
                Log.e("user count", user.count + "---")
                if (context != null) {
                    SessionTwiclo(context).storeId = mModelData.store_id
                }
                if (user.count != null) {
                    if (user.count.toInt() > 0) {
                        fragmentHomeBinding?.cartIcon?.setColorFilter(Color.argb(255, 53, 156, 71))
                        //fragmentHomeBinding?.cartCountTxt?.visibility = View.VISIBLE
                        //fragmentHomeBinding?.cartCountTxt?.text = user.count
                    } else {
                        fragmentHomeBinding?.cartCountTxt?.visibility = View.GONE
                        fragmentHomeBinding?.cartIcon?.setColorFilter(Color.argb(255, 199, 199, 199))
                    }
                }
            } else {
                if (user.errorCode == 101) {
                    //showAlertDialog(context!!)
                }
            }
        })

        // Inflate the layout for this fragment

        fragmentHomeBinding?.cartIcon?.setOnClickListener {
            if (SessionTwiclo(context).isLoggedIn){
                startActivity(
                        Intent(context, CartActivity::class.java).putExtra(
                                "store_id", SessionTwiclo(
                                context
                        ).storeId
                        )
                )
            }else{
                showLoginDialog("Please login to proceed")
            }

        }

        fragmentHomeBinding?.profileIconn?.setOnClickListener {
            if (SessionTwiclo(context).isLoggedIn){
                fragmentManager?.beginTransaction()
                        ?.replace(R.id.container, ProfileFragment())
                        ?.commitAllowingStateLoss()

            }else{
                showLoginDialog("Please login to proceed")
            }

        }

        fragmentHomeBinding?.btnDelivery?.setOnClickListener {
            if (SessionTwiclo(context).isLoggedIn){

                //findNavController().navigate(R.id.action_homeFragment_to_sendPacketFragment)
                startActivity(Intent(context, SendPackageActivity::class.java))
            }else{
                showLoginDialog("Please login to proceed")
            }
        }

//        fragmentHomeBinding?.homeQuestionLay?.setOnClickListener {
//            if (SessionTwiclo(context).isLoggedIn){
//                startActivity(Intent(context, ServicesActivity::class.java))
//            }else{
//                showLoginDialog("Please login to proceed")
//            }
//        }

        return fragmentHomeBinding?.root
    }


    private fun initRecyclerView() {
        //var categoryRecyclerView: RecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        fragmentHomeBinding?.categoryRecyclerView?.adapter = sliderAdapter
        fragmentHomeBinding?.categoryRecyclerView?.setHasFixedSize(true)
        fragmentHomeBinding?.categoryRecyclerView?.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val completeleyVisible: Int = layoutManger?.getActiveCardPosition()!!
                itemPosition =completeleyVisible
                fragmentHomeBinding?.categorySmallRecyclerview?.adapter = categoryAdapter
                categoryAdapter?.updateReceiptsList(itemPosition!!)
                fragmentHomeBinding?.categorySmallRecyclerview?.smoothScrollToPosition(itemPosition!!)
            }
        })
        layoutManger = fragmentHomeBinding?.categoryRecyclerView?.layoutManager as CardSliderLayoutManager
        CardSnapHelper().attachToRecyclerView(categoryRecyclerView)
    }

    private fun onActiveCardChange() {
        val pos: Int? = layoutManger?.activeCardPosition
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return
        }
        onActiveCardChange(pos!!)
    }


    private fun onActiveCardChange(pos: Int) {
        val animH = intArrayOf(R.animator.slide_in_right, R.animator.slide_out_left)
        val animV = intArrayOf(R.animator.slide_in_top, R.animator.slide_out_bottom)
        val left2right: Boolean = pos < currentPosition
        if (left2right) {
            animH[0] = R.animator.slide_in_left
            animH[1] = R.animator.slide_out_right
            animV[0] = R.animator.slide_in_bottom
            animV[1] = R.animator.slide_out_top
        }


        currentPosition = pos
    }


    fun getGeoAddressFromLatLong(
            latitude: Double,
            longitude: Double
    ): String? {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            //   String knownName = addresses.get(0).getFeatureName(); // Only if available else return
            address
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }


    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result Code is -1 send from Payumoney activity
        Log.d(
                "MainActivity",
                "request code $requestCode resultcode $resultCode"
        )

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {

//
//                    val place = Autocomplete.getPlaceFromIntent(data!!)
//                    Log.wtf(
//                            "SearchRide",
//                            "\nID: " + place.id + "\naddress:" + place.address + "\nName:" + place.name + "\nlatlong: " + place.latLng
//                    )
//                    // do query with address
//                    val addresses: List<Address>
//                    val geocoder = Geocoder(context, Locale.getDefault())
//                    //  lat=place.latLng!!.latitude
//                    // lng=place.latLng!!.latitude
//                    addresses = geocoder.getFromLocation(
//                            place.latLng!!.latitude,
//                            place.latLng!!.longitude,
//                            1
//                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                    /*   if (addressType.equals("from")) {
//                           fromLat = place.latLng!!.latitude
//                           fromLng = place.latLng!!.longitude
//                           fromGoogleAddress.text = addresses.get(0).getAddressLine(0)
//                       } else if (addressType.equals("to")) {*/
//                    //toLat = place.latLng!!.latitude
//                    // toLng = place.latLng!!.longitude
//                    userAddress.text = addresses[0].getAddressLine(0)
                    //  }
                    fragmentHomeBinding?.userAddress?.text = SessionTwiclo(context).userAddress

                }
                AutocompleteActivity.RESULT_ERROR -> { // TODO: Handle the error.
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Toast.makeText(context, "Error: " + status.statusMessage, Toast.LENGTH_LONG).show()
                    // Log.i(FragmentActivity.TAG, status.statusMessage)
                }
                Activity.RESULT_CANCELED -> { // The user canceled the operation.
                }
            }
        }
    }

    fun onSearchCalled() { // Set the fields to specify which types of place data to return.
        val fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS
        )
        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
        )
                .build(context as Activity)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }


    override fun onResume() {
        super.onResume()
        if (SessionTwiclo(context).isLoggedIn){

            Log.d("Home Resume", "RESUME")

            viewmodel?.getCartCountApi(
                    SessionTwiclo(context).loggedInUserDetail.accountId,
                    SessionTwiclo(context).loggedInUserDetail.accessToken
            )

            viewmodel?.getHomeServices(
                    SessionTwiclo(context).loggedInUserDetail.accountId,
                    SessionTwiclo(context).loggedInUserDetail.accessToken
            )

        }else{
            viewmodel?.getHomeServices(
                    "",
                    ""
            )
        }


        try {
            _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
            _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            _progressDlg!!.setCancelable(false)
            _progressDlg!!.show()
        } catch (ex: Exception) {
            Log.wtf("IOS_error_starting", ex.cause!!)
        }
        if (SessionTwiclo(context).userAddress.equals("")) {


            activity?.let {
                EasyLocation(it, object : EasyLocation.EasyLocationCallBack {
                    override fun permissionDenied() {

                        Log.e("Location", "permission  denied")


                    }

                    override fun locationSettingFailed() {

                        Log.e("Location", "setting failed")


                    }

                    override fun getLocation(location: Location) {

                        Log.e(
                                "Location_lat_lng",
                                " latitude ${location.latitude} longitude ${location.longitude}"
                        )

                        SessionTwiclo(context).userAddress = getGeoAddressFromLatLong(location.latitude, location.longitude)
                        SessionTwiclo(context).userLat = location.latitude.toString()
                        SessionTwiclo(context).userLng = location.longitude.toString()
                        userAddress?.text = SessionTwiclo(context).userAddress
                    }
                })
            }

        } else {

            userAddress?.text = SessionTwiclo(
                    context
            ).userAddress
        }
        fragmentHomeBinding?.userAddress?.text = SessionTwiclo(context).userAddress

    }

    private fun showLoginDialog(message: String){
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { _, _ ->
            startActivity(
                    Intent(activity, SplashActivity::class.java)
            )


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

    private class OnCardClickListener : View.OnClickListener {
        override fun onClick(view: View) {

        }
    }
}