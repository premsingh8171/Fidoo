package com.fidoo.user.ui

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.CartActivity
import com.fidoo.user.R
import com.fidoo.user.adapter.SearchAdapter
import com.fidoo.user.adapter.StoreCustomItemsAdapter
import com.fidoo.user.data.CheckConnectivity
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.FragmentSearchBinding
import com.fidoo.user.interfaces.*
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.viewmodels.SearchFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.vanillaplacepicker.utils.ToastUtils.showToast
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : Fragment(), AdapterClick, CustomCartAddRemoveClick, AdapterCustomRadioClick, AdapterImageClick, AdapterAddRemoveClick, AdapterCartAddRemoveClick {
    var count: Int = 1
    private var mModelDataTemp: CustomizeProductResponseModel? = null
    var viewmodel: SearchFragmentViewModel? = null
    private var _progressDlg: ProgressDialog? = null
    private var categoryy: ArrayList<CustomListModel>? = null
    var productIdTemp: String? = ""
    var tempProductId: String? = ""
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var countTemp: String? = ""
    var mCustomizeCount: Int? = 0
    var storeIdTemp: String? = ""
    var tempType: String? = ""
    var tempOfferPrice: String? = ""
    var searchbyStr: String? = ""
    var tempPrice: Double? = 0.0
    var customIdsList: ArrayList<String>? = null
    var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
    var fragmentSearchBinding: FragmentSearchBinding? = null
    var storeID: String = "" // used to pass back the store id to adapter

    lateinit var mTempData: ArrayList<SearchModel.ProductList>
    lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentSearchBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )

        //var root: View = inflater.inflate(R.layout.fragment_search, container, false)

        mTempData = ArrayList()

        adapter = SearchAdapter(requireContext(), mTempData, this, storeID, this, this)


        viewmodel = ViewModelProviders.of(requireActivity()).get(SearchFragmentViewModel::class.java)


//        if (MainActivity.searchSuggestionsList!!.size == 0) {
//            fragmentSearchBinding?.autoCompleteTextView1?.visibility = View.GONE
//            fragmentSearchBinding?.recentSearhRecyclerView?.visibility = View.GONE
//
//        } else {
//            fragmentSearchBinding?.autoCompleteTextView1?.visibility = View.VISIBLE
//            fragmentSearchBinding?.recentSearhRecyclerView?.visibility = View.VISIBLE
//
//            val adapter =
//                RecentSearchAdapter(requireContext(), MainActivity.searchSuggestionsList!!, this)
//            fragmentSearchBinding?.recentSearhRecyclerView?.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL,
//                false
//            )
//            fragmentSearchBinding?.recentSearhRecyclerView?.setHasFixedSize(true)
//            fragmentSearchBinding?.recentSearhRecyclerView?.adapter = adapter
//        }


//        fragmentSearchBinding?.profileIcon?.setOnClickListener {
//            if (SessionTwiclo(context).isLoggedIn){
//                startActivity(Intent(context, AccountDetailsActivity::class.java))
//            }else{
//                showLoginDialog("Please login to proceed")
//            }
//        }

        customIdsList = ArrayList<String>()
        customIdsListTemp = ArrayList<CustomCheckBoxModel>()
        behavior = BottomSheetBehavior.from(fragmentSearchBinding!!.bottomSheet)
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED) // Default behavior of bottom sheet


        if (SessionTwiclo(context).isLoggedIn){
            viewmodel?.getCartCountApi(
                SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
                SessionTwiclo(requireContext()).loggedInUserDetail.accessToken
            )
        }


        fragmentSearchBinding?.transLay?.setOnClickListener {
            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                fragmentSearchBinding?.searchEdt?.isEnabled = false
            } else {
                fragmentSearchBinding?.searchEdt?.isEnabled = true
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        fragmentSearchBinding?.customAddBtn?.setOnClickListener {
            Log.e("addCartTempList", Gson().toJson(MainActivity.addCartTempList))

            for (i in 0 until categoryy!!.size) {
                customIdsList!!.add(categoryy!![i].id.toString())
            }
            Log.e("customIdsList", customIdsList.toString())

            if (SessionTwiclo(context).storeId.equals(storeID) || SessionTwiclo(context).storeId.equals("")) {

                try {
                    _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
                    _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    _progressDlg!!.setCancelable(false)
                    _progressDlg!!.show()
                } catch (ex: Exception) {
                    Log.wtf("IOS_error_starting", ex.cause!!)
                }
                MainActivity.addCartTempList!!.clear()
                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = productIdTemp
                addCartInputModel.quantity = countValue.text.toString()
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "1"
                MainActivity.addCartTempList!!.add(addCartInputModel)

                //SessionTwiclo(context).storeId = storeID

                viewmodel!!.addToCartApi(
                        SessionTwiclo(context).loggedInUserDetail.accountId,
                        SessionTwiclo(context).loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                )
            } else {
                clearCartPopup()
            }
        }

        viewmodel?.addToCartResponse?.observe(requireActivity(), Observer { user ->

            if (_progressDlg != null) {
                _progressDlg!!.dismiss()
                _progressDlg = null
            }

            if (storeID != null) {
                //SessionTwiclo(context).storeId = storeID
            }
            Log.e("stores_response", Gson().toJson(user))
            //val mModelData: AddToCartModel = user
            if (tempType.equals("custom")) {

                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    fragmentSearchBinding?.searchEdt?.isEnabled = false

                } else {
                    fragmentSearchBinding?.searchEdt?.isEnabled = true
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

                }
            } else {
                MainActivity.tempProductList!!.clear()
                MainActivity.addCartTempList!!.clear()
            }

            viewmodel?.getCartCountApi(
                SessionTwiclo(context).loggedInUserDetail.accountId,
                SessionTwiclo(context).loggedInUserDetail.accessToken
            )

        })

        viewmodel?.customizeProductResponse?.observe(requireActivity(), Observer { user ->
            if (_progressDlg != null) {
                _progressDlg!!.dismiss()
                _progressDlg = null
            }

            Log.e("stores response", Gson().toJson(user))
            mModelDataTemp = user

            categoryy = ArrayList()

            for (i in 0 until mModelDataTemp?.category?.size!!) {
                if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
                    var customListModel: CustomListModel? = CustomListModel()
                    customListModel!!.category = mModelDataTemp?.category?.get(i)!!.catId
                    customListModel.id = mModelDataTemp?.category?.get(i)!!.subCat.get(0).id.toInt()
                    customListModel.price = mModelDataTemp?.category?.get(i)!!.subCat.get(0).price
                    categoryy!!.add(customListModel)
                } else {

                }
            }

            var tempPrice: Double? = 0.0
            for (i in 0 until customIdsListTemp!!.size) {
                tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
            }

            for (i in 0 until categoryy!!.size) {
                if (categoryy!!.get(i).price != null) {
                    tempPrice = tempPrice!! + categoryy!!.get(i).price.toDouble()
                }
            }
            if (!tempOfferPrice.equals("")) {
                tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
            }
            view?.customAddBtn?.text = resources.getString(R.string.ruppee) + tempPrice.toString()

            val adapter = context?.let {
                StoreCustomItemsAdapter(
                    it,
                    mModelDataTemp?.category!!,
                    this,
                    categoryy,
                    this
                )
            }
            view?.customItemsRecyclerview?.layoutManager = LinearLayoutManager(context)
            view?.customItemsRecyclerview?.setHasFixedSize(true)
            view?.customItemsRecyclerview?.adapter = adapter
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.failureResponse?.observe(requireActivity(), Observer { user ->
            // dismissIOSProgress()
            if (_progressDlg != null) {

                _progressDlg!!.dismiss()
                _progressDlg = null
            }


            Log.e("cart response", Gson().toJson(user))
            //showToast(user)
            if (context != null) {
                Toast.makeText(context, user, Toast.LENGTH_SHORT).show()
            }

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

//        fragmentSearchBinding?.searchEdt?.setOnEditorActionListener(object :
//            TextView.OnEditorActionListener {
//            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//
//                    try {
//                        _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
//                        _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                        _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//
//                        _progressDlg!!.setCancelable(false)
//                        _progressDlg!!.show()
//                    } catch (ex: Exception) {
//                        Log.wtf("IOS_error_starting", ex.cause!!)
//                    }
//                    if (SessionTwiclo(context).isLoggedIn){
//                        viewmodel?.getSearchApi(
//                            SessionTwiclo(activity).loggedInUserDetail.accountId,
//                            SessionTwiclo(activity).loggedInUserDetail.accessToken,
//                            fragmentSearchBinding?.searchEdt?.text.toString()
//                        )
//                    }else{
//                        viewmodel?.getSearchApi(
//                            "",
//                            "",
//                            fragmentSearchBinding?.searchEdt?.text.toString()
//                        )
//                    }
//
//                    return true
//                }
//                return false
//            }
//        })


//        fragmentSearchBinding?.addressUser?.text = SessionTwiclo(context).userAddress
//
//        fragmentSearchBinding?.cartIcon?.setOnClickListener {
//            if (SessionTwiclo(context).isLoggedIn){
//                startActivity(Intent(context, CartActivity::class.java))
//            }else{
//                showLoginDialog("Please login to proceed")
//            }
//        }
//
//
//        fragmentSearchBinding?.llViewCart?.setOnClickListener {
//            startActivity(
//                Intent(context, CartActivity::class.java).putExtra("store_id", SessionTwiclo(context).storeId
//                )
//            )
//        }


        //cartcount responce
        viewmodel?.cartCountResponse?.observe(requireActivity(), { cartcount ->
            dismissIOSProgress()

            Log.d("cartCountResponse___", cartcount.toString())
            val count = cartcount.count
            val price = cartcount.price
            storeID = cartcount.store_id
            if (!cartcount.error) {
                if (count != "0") {
                    fragmentSearchBinding?.itemQuantityTextsearch?.text = count
                    fragmentSearchBinding?.totalpriceTxtsearch?.text = "₹" + price
                    fragmentSearchBinding?.cartitemViewLLsearch?.visibility = View.VISIBLE
                } else {
                    fragmentSearchBinding?.cartitemViewLLsearch?.visibility = View.GONE
                }
            }

        })

        fragmentSearchBinding?.searchEdt?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                //}
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                searchbyStr = s.toString()
                if (fragmentSearchBinding?.searchEdt?.text.toString().length >= 2) {

                    if (SessionTwiclo(context).isLoggedIn) {
                        viewmodel?.getSearchApi(
                            SessionTwiclo(activity).loggedInUserDetail.accountId,
                            SessionTwiclo(activity).loggedInUserDetail.accessToken,
                            searchbyStr!!
                        )
                    }else{
                        viewmodel?.getSearchApi(
                            "",
                            "",
                            searchbyStr!!
                        )
                    }
                } else {
                    searchbyStr = ""
                    mTempData.clear()
                    adapter.notifyDataSetChanged()
                }
//                else{
//                    viewmodel?.getSearchApi(
//                        "",
//                        "",
//                        fragmentSearchBinding?.searchEdt?.text.toString()
//                    )
//                }
            }
        })


//        viewmodel?.cartCountResponse?.observe(requireActivity(), Observer { user ->
//
//            if (_progressDlg != null) {
//                _progressDlg!!.dismiss()
//                _progressDlg = null
//            }
//
//            if (!user.error) {
//                val mModelData: CartCountModel = user
//                //SessionTwiclo(context).storeId = mModelData.store_id
//                Log.e("countResponse", Gson().toJson(mModelData))
//                if (user.store_id != null) {
//                    storeID = user.store_id
//                }
//                /*if (user.count.toInt()>0){
//                    cartCountTxt.visibility = View.VISIBLE
//                    fragmentSearchBinding?.llViewCart?.visibility = View.VISIBLE
//                    cartCountTxt.text = user.count
//                    fragmentSearchBinding?.txtPrice?.text = resources.getString(R.string.ruppee) + user.price
//                    if (user.count.equals("1")) {
//                        fragmentSearchBinding?.txtItems?.text = user.count + " item"
//                    } else {
//                        fragmentSearchBinding?.txtItems?.text = user.count + " items"
//                    }
//
//                }else{
//                    cartCountTxt.visibility = View.GONE
//                    fragmentSearchBinding?.llViewCart?.visibility = View.GONE
//                }*/
//                if (activity != null) {
//                    if (user.store_id != null) {
//                        storeID = user.store_id
//                        SessionTwiclo(requireContext()).storeId = mModelData.store_id
//                    }
//
//                }
//                /*if (user.count != null) {
//                    if (user.count.toInt() > 0) {
//                        //cartCountTxt?.text = user.count
//                        fragmentSearchBinding?.llViewCart?.visibility = View.VISIBLE
//                        fragmentSearchBinding?.txtPrice?.text = resources.getString(R.string.ruppee) + user.price
//                        if (user.count.equals("1")) {
//                            fragmentSearchBinding?.txtItems?.text = user.count + " item"
//                        } else {
//                            fragmentSearchBinding?.txtItems?.text = user.count + " items"
//                        }
//                    }
//                }*/
//            } else {
//                if (user.errorCode == 101) {
//                    showAlertDialog(requireContext())
//                }
//            }
//        })

        fragmentSearchBinding?.viewcartfromSearch?.setOnClickListener {
            if (SessionTwiclo(requireActivity()).isLoggedIn) {
                requireActivity().startActivity(Intent(requireActivity(),
                    CartActivity::class.java).putExtra("store_id", SessionTwiclo(requireActivity()).storeId))
            } else {
                showLoginDialog("Please login to proceed")

            }

        }
        fragmentSearchBinding?.plusLay?.setOnClickListener {
            count++
            fragmentSearchBinding?.countValue?.text = count.toString()
        }

        fragmentSearchBinding?.minusLay?.setOnClickListener {
            if (mCustomizeCount!! > 1) {
                val builder = AlertDialog.Builder(requireContext())
                //set title for alert dialog
                builder.setTitle("Remove item from cart")
                //set message for alert dialog
                builder.setMessage("This item has multiple customizations added. Proceed to cart to remove item?")
                // builder.setIcon(android.R.drawable.ic_dialog_alert)
                //performing positive action
                builder.setPositiveButton("NO") { _, _ ->
                    //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                }

                //performing negative action
                builder.setNegativeButton("YES") { _, _ ->
                    //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
                    startActivity(Intent(requireContext(), CartActivity::class.java))
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(true)
                alertDialog.show()
            } else {
                if (count > 1) {
                    count--
                    fragmentSearchBinding?.countValue?.text = count.toString()
                }
            }
        }

        viewmodel?.clearCartResponse?.observe(requireActivity(), Observer { user ->
            // dismissIOSProgress()
            if (_progressDlg != null) {
                _progressDlg!!.dismiss()
                _progressDlg = null
            }

            val mModelData: ClearCartModel = user
            Log.e("countResponse", Gson().toJson(mModelData))
            /*viewmodel!!.addToCartApi(
                SessionTwiclo(context).loggedInUserDetail.accountId,
                SessionTwiclo(context).loggedInUserDetail.accessToken,
                HomeActivity.addCartTempList!!
            )*/


        })

        viewmodel?.searchResponse?.observe(requireActivity(), Observer { user ->
            // dismissIOSProgress()
            if (_progressDlg != null) {

                _progressDlg!!.dismiss()
                _progressDlg = null
            }

            val storeList: ArrayList<SearchModel.Store> = ArrayList()

            val productList: ArrayList<SearchModel.ProductList> = ArrayList()

            val mModelData: SearchModel = user
            for (i in 0 until mModelData.store.size) {

                for (j in 0 until mModelData.store[i].list.size) {
                    val productData = mModelData.store[i].list[j]
                    productList.add(productData)
                }
            }

            Log.e("searchResponse", Gson().toJson(mModelData))

            if (mModelData.store.size == 0) {
                fragmentSearchBinding?.emptyIcon?.visibility = View.VISIBLE
                fragmentSearchBinding?.emptyTitleTxt?.visibility = View.VISIBLE
                fragmentSearchBinding?.emptyDescTxt?.visibility = View.VISIBLE
                fragmentSearchBinding?.searchRecyclerView?.visibility = View.GONE
                if (activity != null) {
                    Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                }
            } else {

                /*if(MainActivity.searchSuggestionsList?.contains(fragmentSearchBinding?.searchEdt?.text.toString())!!)
                {

                }
                else {
                    MainActivity.searchSuggestionsList?.add(fragmentSearchBinding?.searchEdt?.text.toString())
                }*/
                fragmentSearchBinding?.emptyIcon?.visibility = View.GONE
                fragmentSearchBinding?.emptyTitleTxt?.visibility = View.GONE
                fragmentSearchBinding?.emptyDescTxt?.visibility = View.GONE
                fragmentSearchBinding?.searchRecyclerView?.visibility = View.VISIBLE
                if (context != null) {
                    mTempData = productList
                    adapter = SearchAdapter(requireContext(), mTempData, this, storeID, this, this)

//                    fragmentSearchBinding?.searchRecyclerView?.layoutManager = GridLayoutManager(
//                        context,
//                        2
//                    )
                    fragmentSearchBinding?.searchRecyclerView?.layoutManager = LinearLayoutManager(
                            requireContext()
                    )
                    fragmentSearchBinding?.searchRecyclerView?.setHasFixedSize(true)
                    fragmentSearchBinding?.searchRecyclerView?.adapter = adapter
                }
            }


        })
        return fragmentSearchBinding?.root


    }


    override fun onItemClick(
        productId: String?,
        typee: String?,
        count: String?,
        offerPrice: String?,
        customizeCount: Int?,
        productType: String?,
        cart_id: String?
    ) {

        //storeIdTemp = storeId
        productIdTemp = productId
        tempType = typee
        this.count = count!!.toInt()
        //tempType = storeIdTemp
        tempOfferPrice = offerPrice
        mCustomizeCount = customizeCount
        //this.count = count!!.toInt()
        fragmentSearchBinding?.countValue?.text = count.toString()


        if (typee.equals("custom")) {
            if (mCustomizeCount == 0) {
                customIdsListTemp?.clear()

                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    fragmentSearchBinding?.searchEdt?.isEnabled = false

                } else {
                    fragmentSearchBinding?.searchEdt?.isEnabled = true
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
                //  tempProductId = productId
                try {
                    _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
                    _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    _progressDlg!!.setCancelable(false)
                    _progressDlg!!.show()
                } catch (ex: Exception) {
                    Log.wtf("IOS_error_starting", ex.cause!!)
                }
                customIdsList!!.clear()
                viewmodel?.customizeProductApi(
                    SessionTwiclo(context).loggedInUserDetail.accountId,
                    SessionTwiclo(context).loggedInUserDetail.accessToken, productId!!
                )

            } else {
                val builder = AlertDialog.Builder(requireContext())
                //set title for alert dialog
                builder.setTitle("Your previous customization")
                //set message for alert dialog
                builder.setMessage(count)
                // builder.setIcon(android.R.drawable.ic_dialog_alert)

                //performing positive action
                builder.setPositiveButton("I'LL CHOOSE") { dialogInterface, which ->

                    if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                        searchLay.visibility = View.GONE
                    } else {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        searchLay.visibility = View.VISIBLE
                    }

                    //  tempProductId = productId

                    //customIdsList!!.clear()
                    if (productId != null) {
                        viewmodel?.customizeProductApi(
                            SessionTwiclo(context).loggedInUserDetail.accountId,
                            SessionTwiclo(context).loggedInUserDetail.accessToken, productId
                        )
                    }
                    //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                }

                //performing negative action
                builder.setNegativeButton("REPEAT") { dialogInterface, which ->
                    //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()

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
                //productIdTemp = productId
                //countTemp = count
            }


        } else {

            if (SessionTwiclo(context).storeId.equals(typee) || SessionTwiclo(context).storeId.equals("")) {

                try {
                    _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
                    _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    _progressDlg!!.setCancelable(false)
                    _progressDlg!!.show()
                } catch (ex: Exception) {
                    Log.wtf("IOS_error_starting", ex.cause!!)
                }
                MainActivity.addCartTempList!!.clear()
                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = productId
                addCartInputModel.quantity = count
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "0"
                MainActivity.addCartTempList!!.add(addCartInputModel)
                viewmodel!!.addToCartApi(
                    SessionTwiclo(context).loggedInUserDetail.accountId,
                    SessionTwiclo(context).loggedInUserDetail.accessToken,
                    MainActivity.addCartTempList!!,
                    cart_id!!
                )
            } else {
                //productIdTemp = productId
                //countTemp = count
                clearCartPopup()
            }
        }
    }

    private fun clearCartPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Replace cart item!")
        builder.setMessage("Do you want to discard the previous selection?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            try {
                _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
                _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

                _progressDlg!!.setCancelable(false)
                _progressDlg!!.show()
            } catch (ex: Exception) {
                Log.wtf("IOS_error_starting", ex.cause!!)
            }
            //  showIOSProgress()
            viewmodel!!.clearCartApi(
                    SessionTwiclo(context).loggedInUserDetail.accountId,
                    SessionTwiclo(context).loggedInUserDetail.accessToken
            )
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (SessionTwiclo(context).storeId == null) {
            storeID = ""
        }

        if (SessionTwiclo(context).isLoggedIn) {
            viewmodel?.getCartCountApi(
                    SessionTwiclo(activity).loggedInUserDetail.accountId,
                    SessionTwiclo(activity).loggedInUserDetail.accessToken
            )
        }


    }

    override fun onIdSelected(productId: String, type: String, price: String, maxSelectionCount: Int) {
        if (type == "select") {
            customIdsList!!.add(productId)
            val customCheckBoxModel = CustomCheckBoxModel()
            customCheckBoxModel.id = productId
            customCheckBoxModel.price = price
            customIdsListTemp!!.add(customCheckBoxModel)
        } else {
            for (i in 0 until customIdsList!!.size) {
                if (customIdsList!!.get(i).equals(productId)) {
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

        var tempPrice: Double? = 0.0
        for (i in 0 until customIdsListTemp!!.size) {
            tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
        }

        for (i in 0 until categoryy!!.size) {
            tempPrice = tempPrice!! + categoryy!!.get(i).price.toDouble()
        }

        tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
        customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()

    }

    override fun onCustomRadioClick(checkedId: String?, position: String?) {
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
            var customListModel: CustomListModel? = CustomListModel()
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

    override fun onSelectedImageClick(position: Int) {

        /*fragmentSearchBinding?.searchEdt?.setText(
            //MainActivity.searchSuggestionsList!!.get(position).toString()
        )*/
        try {
            _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
            _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

            _progressDlg!!.setCancelable(false)
            _progressDlg!!.show()

        } catch (ex: Exception) {
            Log.wtf("IOS_error_starting", ex.cause!!)
        }

        /*viewmodel?.getSearchApi(
            SessionTwiclo(activity).loggedInUserDetail.accountId,
            SessionTwiclo(activity).loggedInUserDetail.accessToken,
            MainActivity.searchSuggestionsList!![position]
        )*/
    }

    override fun onAddItemClick(productId: String?, quantity: String?, offerPrice: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?) {
        tempOfferPrice = offerPrice
        //plusMinusPrice = 0.0
        tempPrice = 0.0

        tempProductId = productId


        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle("Your previous customization")
        //set message for alert dialog
        builder.setMessage(quantity)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("I'LL CHOOSE") { _, which ->

            if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                //searchLay.visibility = View.GONE
            } else {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                //searchLay.visibility = View.VISIBLE
            }

            //tempProductId = productId
            //showIOSProgress()
            customIdsList!!.clear()
            viewmodel?.customizeProductApi(
                    SessionTwiclo(context).loggedInUserDetail.accountId,
                    SessionTwiclo(context).loggedInUserDetail.accessToken, productId!!
            )
            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
        }

        //performing negative action
        builder.setNegativeButton("REPEAT") { _, which ->
            //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
            //showIOSProgress()
            viewmodel?.addRemoveCartDetails(
                    SessionTwiclo(context).loggedInUserDetail.accountId,
                    SessionTwiclo(context).loggedInUserDetail.accessToken,
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

    override fun onRemoveItemClick(productId: String?, quantity: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?) {
        if (!isNetworkConnected()) {
            showToast(resources.getString(R.string.provide_internet))

        } else {
            //showIOSProgress()
            if (cart_id != null) {
                viewmodel?.addRemoveCartDetails(
                        SessionTwiclo(context).loggedInUserDetail.accountId,
                        SessionTwiclo(context).loggedInUserDetail.accessToken,
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

    override fun onItemAddRemoveClick(
            productId: String?,
            count: String?,
            type: String?,
            price: String?,
            sid: String?,
            cartId: String?,
            position: Int
    ) {


        if (type.equals("add")) {

            //showIOSProgress()
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
                                SessionTwiclo(context).loggedInUserDetail.accountId,
                                SessionTwiclo(context).loggedInUserDetail.accessToken,
                                MainActivity.addCartTempList!!,
                                ""
                        )
                    }
                    MainActivity.tempProductList!!.clear()


                    //ll_view_cart.visibility = View.VISIBLE // to show bottom cart bar if add is clicked for the first time in non-customized items
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

                        viewmodel!!.addToCartApi(
                                SessionTwiclo(context).loggedInUserDetail.accountId,
                                SessionTwiclo(context).loggedInUserDetail.accessToken,
                                MainActivity.addCartTempList!!,
                                ""
                        )
                    }
                }


            } else {
                var check = "edit"
                var checkPos = 0
                Log.e("check1", Gson().toJson(MainActivity.tempProductList!!))
                for (i in 0 until MainActivity.tempProductList!!.size) {
                    if (MainActivity.tempProductList!![i].productId.equals(productId)) {
                        if (count.equals("0")) {
                            check = "remove"
                            checkPos = i
                            break
                            //  addCartTempList!!.removeAt(i)
                            //   tempProductList!!.removeAt(i)

                        } /*else {
                    tempProductList!!.get(i).quantity = count
                    addCartTempList!!.get(i).quantity = count
                }*/
                    }
                }

                if (check == "remove") {
                    //ll_view_cart.visibility = View.GONE // to hide the bottom cart bar if non-customized item quantity becomes zero
                    MainActivity.addCartTempList!!.removeAt(checkPos)
                    MainActivity.tempProductList!!.removeAt(checkPos)

                    if (cartId != null) {
                        if (productId != null) {
                            viewmodel?.addRemoveCartDetails(
                                    SessionTwiclo(context).loggedInUserDetail.accountId,
                                    SessionTwiclo(context).loggedInUserDetail.accessToken,
                                    productId,
                                    "remove",
                                    "0",
                                    "",
                                    cartId,
                                    customIdsList!!
                            )
                        }
                    }

                } else {
                    MainActivity.tempProductList!![checkPos].quantity = count
                    MainActivity.addCartTempList!![checkPos].quantity = count

                    if (cartId != null) {
                        if (productId != null) {
                            viewmodel?.addRemoveCartDetails(
                                    SessionTwiclo(context).loggedInUserDetail.accountId,
                                    SessionTwiclo(context).loggedInUserDetail.accessToken,
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

            }
            //plusMinusPrice = 0.0
            tempPrice = 0.0
            Log.d("check1", Gson().toJson(MainActivity.tempProductList!!))
            var bottomPrice: Double? = 0.0
            var bottomCount: Int? = 0
            for (i in 0 until MainActivity.tempProductList!!.size) {
                bottomPrice = bottomPrice!! + (MainActivity.tempProductList!!.get(i).price.toDouble() * MainActivity.tempProductList!!.get(
                        i
                ).quantity.toInt())
                bottomCount = bottomCount!! + MainActivity.tempProductList!!.get(i).quantity.toInt()
            }


        }










    }

    fun isNetworkConnected(): Boolean {
        return CheckConnectivity(context).isNetworkAvailable
    }

    fun showToast(toast_string: String?) {
        Toast.makeText(context, toast_string, Toast.LENGTH_SHORT).show()
    }
    override fun clearCart() {
        viewmodel?.clearCartApi(
                SessionTwiclo(context).loggedInUserDetail.accountId,
                SessionTwiclo(context).loggedInUserDetail.accessToken
        )
    }

    private fun showLoginDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
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
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}