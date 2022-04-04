package com.fidoo.user.search.ui

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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.CheckConnectivity
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.SearchModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.profile.ui.ProfileFragment
import com.fidoo.user.restaurants.activity.StoreItemsActivity
import com.fidoo.user.restaurants.adapter.StoreCustomItemsAdapter
import com.fidoo.user.restaurants.listener.CustomCartPlusMinusClick
import com.fidoo.user.restaurants.model.CustomCheckBoxModel
import com.fidoo.user.restaurants.model.CustomListModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.search.adapter.ParentStoreListAdapter
import com.fidoo.user.search.adapter.RecentSearchAdapter
import com.fidoo.user.search.model.Store
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.CommonUtils
import com.fidoo.user.viewmodels.SearchFragmentViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_search2.view.*
import kotlinx.android.synthetic.main.no_item_found.view.*

@Suppress("DEPRECATION")
class SearchFragment2 : Fragment(), AdapterClick,
    AdapterAddRemoveClick,
    AdapterCartAddRemoveClick {
    private lateinit var parentStoreListAdapter: ParentStoreListAdapter
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    val storeList: ArrayList<Store> = ArrayList()
    var viewmodel: SearchFragmentViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    lateinit var mProductStoreList: ArrayList<SearchModel.Store>
    lateinit var mProductsList: ArrayList<SearchModel.ProductList>
    lateinit var recentsearchArrayList: ArrayList<String>
    var lastCustomized_str: String = ""
    var product_customize_id: String = ""
    private var mModelDataTemp: CustomizeProductResponseModel? = null
    var storeID: String = ""
    var adapter: StoreCustomItemsAdapter? = null
    companion object {
        var for_click_storeID: String = ""
    }

    var tempType: String? = ""
    var search_value: String? = ""
    var customIdsList: ArrayList<String>? = null
    var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
    private var categoryy: ArrayList<CustomListModel>? = null
    var customNamesList: ArrayList<String>? = null

    var tempOfferPrice: String? = ""
    var tempPrice: Double? = 0.0
    var tempCount: String? = ""
    var tempProductId: String? = ""
    var mCustomizeCount: Int? = 0
    var checkvalidation: Int? = 0//for onresume handle
    lateinit var mView: View
    private var _progressDlg: ProgressDialog? = null
    lateinit var sessionTwiclo: SessionTwiclo
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var cartId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_search2, container, false)
        sessionTwiclo = SessionTwiclo(requireContext())
        viewmodel = ViewModelProviders.of(requireActivity()).get(SearchFragmentViewModel::class.java)
        viewmodelusertrack = ViewModelProviders.of(requireActivity()).get(UserTrackerViewModel::class.java)
        mProductStoreList = ArrayList()
        mProductsList = ArrayList()
        recentsearchArrayList = ArrayList()
        customIdsList = ArrayList()
        customIdsListTemp = ArrayList()
        customNamesList = ArrayList()
        behavior = BottomSheetBehavior.from(mView.bottom_sheet)
        mView.total_itemTxt_fgmt.text = "Items (" + mProductsList.size.toString() + ")"

        parentStoreListAdapter = ParentStoreListAdapter(requireContext(), mProductStoreList, this, storeID, this, this)

        try {
            if (!sessionTwiclo.getRecentSearchArrayList("Recent_Search").equals("")) {
                recentsearchArrayList = sessionTwiclo.getRecentSearchArrayList("Recent_Search")
                    .reversed() as ArrayList<String>
                RecentSearchRv(recentsearchArrayList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mView.searchEdt_new_fgmt?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                Log.d("afterTextChanged_",s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                search_value = s.toString()
                if (mView.searchEdt_new_fgmt?.text.toString().length >= 2) {
                    if (sessionTwiclo.isLoggedIn) {
                        //showIOSProgress()
                        viewmodel?.getSearchApi(
                            sessionTwiclo.loggedInUserDetail.accountId,
                            sessionTwiclo.loggedInUserDetail.accessToken,
                            search_value!!, ""
                        )

                    } else {
                        viewmodel?.getSearchApi(
                            "",
                            "",
                            search_value!!, ""
                        )
                    }
                } else {
                    search_value = ""
                    mProductStoreList.clear()
                    mProductsList.clear()
                    parentStoreListAdapter.notifyDataSetChanged()
                    mView.total_itemTxt_fgmt.text = "Items (" + mProductsList.size.toString() + ")"
                    if (sessionTwiclo.isLoggedIn) {
                        viewmodel?.getSearchApi(
                            sessionTwiclo.loggedInUserDetail.accountId,
                            sessionTwiclo.loggedInUserDetail.accessToken,
                            "", ""
                        )
                    } else {
                        viewmodel?.getSearchApi(
                            "",
                            "",
                            "", ""
                        )
                    }
                }
            }
        })

        if (sessionTwiclo.isLoggedIn) {
            try {
                viewmodelusertrack?.customerActivityLog(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.mobileno,
                    "HomeSearch Screen",
                    SplashActivity.appversion,
                    StoreListActivity.serive_id_,
                    SessionTwiclo(activity).deviceToken
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                viewmodel?.getCartCountApi(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        getResponse()

        mView.new_cartitemView_LLsearch_fgmt.setOnClickListener {
            if (sessionTwiclo.isLoggedIn) {
                startActivity(
                    Intent(context, CartActivity::class.java).putExtra(
                        "store_id", SessionTwiclo(
                            context
                        ).storeId
                    )
                )
            } else {
                showLoginDialog("Please login to proceed")
            }
        }

        mView.customAddBtn_search.setOnClickListener {
            if (isNetworkConnected()) {
                Log.e("addCartTempList", Gson().toJson(MainActivity.addCartTempList))
                for (i in 0 until categoryy!!.size) {
                    customIdsList!!.add(categoryy!![i].id.toString())
                    customNamesList!!.add(categoryy!![i].subCatName.toString())
                }
                var lastCustomized: String = ""
                lastCustomized = customNamesList.toString()
                val regex = "\\[|\\]"
                lastCustomized_str = lastCustomized.replace(regex.toRegex(), "")
                product_customize_id = "1"
                Log.e(
                    "customIdsList",
                    customIdsList.toString() + "\n" + StoreItemsActivity.lastCustomized_str
                )

                MainActivity.addCartTempList!!.clear()
                val addCartInputModel = AddCartInputModel()
                addCartInputModel.productId = tempProductId
                addCartInputModel.quantity = "1"
                addCartInputModel.message = "add product"
                addCartInputModel.customizeSubCatId = customIdsList!!
                addCartInputModel.isCustomize = "1"
                MainActivity.addCartTempList!!.add(0, addCartInputModel)

                storeID = for_click_storeID
                if (sessionTwiclo.storeId.equals(storeID) || sessionTwiclo.storeId.equals("")) {
                    showIOSProgress()
                    //storeID = storeID
                    // product_customize_id
                    //lastCustomized_str

                    viewmodel!!.addToCartApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        cartId
                    )
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED

                } else {
                    clearCartPopup()
                }
            }
        }

        return mView
    }

    private fun getResponse() {
        //Here we observe searchApi response
        viewmodel?.searchResponse?.observe(requireActivity(), { searchResponse ->
            //dismissIOSProgress()
            Log.e("searchResponse__", Gson().toJson(searchResponse))

            mProductStoreList.clear()
            if (!searchResponse.error!!) {

                val storeList: ArrayList<SearchModel.Store> = ArrayList()
                val productList: ArrayList<SearchModel.ProductList> = ArrayList()
                var recentArrayList: ArrayList<String> = ArrayList()
                val mModelData: SearchModel = searchResponse
               // Log.d("searchlistsize",mModelData.store.size.toString())
                if (mModelData.store.size.toString().equals("0")){ storeID="" }

                if (mModelData.store.isNotEmpty()) {
                    for (i in 0 until mModelData.store.size) {
                        if (mModelData.store.size > 0 || mModelData.store.size < 2) {
                            val storeModel = mModelData.store[i]
                            for (j in 0 until mModelData.store[i].list.size) {
                                val productData = mModelData.store[i].list[j]
                                productList.add(productData)
                            }
                            storeList.add(storeModel)
                        }
                    }
                    mView.no_itemsFound_res.visibility = View.GONE
                    mView.no_item_foundll.visibility = View.GONE

                }else{
                    if (search_value!!.isNotEmpty()) {
                        mView.no_itemsFound_res.visibility = View.VISIBLE
                        mView.no_item_foundll.visibility = View.VISIBLE
                    }else{
                        mView.no_itemsFound_res.visibility = View.GONE
                        mView.no_item_foundll.visibility = View.GONE
                    }
                }
                try {
                    recentsearchArrayList.add(productList[0].categoryName.toString())
                    val s: Set<String> = LinkedHashSet<String>(recentsearchArrayList)
                    recentsearchArrayList.clear()
                    recentsearchArrayList.addAll(0, s)
                    //val sortAsc = recentsearchArrayList.reversed() as java.util.ArrayList<String>
                    // Log.d("sortAsc____",recentsearchArrayList.toString())
                    sessionTwiclo.saveRecentSearchArrayList(recentsearchArrayList, "Recent_Search")

                    RecentSearchRv(recentsearchArrayList)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mProductsList = productList
                mProductStoreList = storeList


                try {
                    parentStoreListAdapter = ParentStoreListAdapter(requireContext(), mProductStoreList, this, storeID, this, this)
                    mView.search_rv_fgmt?.adapter = parentStoreListAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        })

        //cart count response
        viewmodel?.cartCountResponse?.observe(requireActivity(), { cartcount ->
            CommonUtils.dismissIOSProgress()
            dismissIOSProgress()
            Log.d("cartCountResponse___", cartcount.toString())
            var count = cartcount.count
            var price = cartcount.price
            if (!cartcount.error) {
                if (count != "0") {
                    storeID = cartcount.store_id

                    mView.new_itemQuantity_textsearch_fgmt.text = count
                    mView.new_totalprice_txtsearch_fgmt.text = "₹" + price
                    mView.new_cartitemView_LLsearch_fgmt.visibility = View.VISIBLE
                    //	mView.item_lay.visibility = View.VISIBLE
                    try {
                        sessionTwiclo.storeId = cartcount.store_id
                    } catch (e: Exception) {
                        e.printStackTrace()
                        sessionTwiclo.storeId = cartcount.store_id

                    }
                    // setStoreId
                } else {
                    mView.new_cartitemView_LLsearch_fgmt.visibility = View.GONE
                    mView.item_lay.visibility = View.GONE
                    mView.no_itemsFound_res.visibility = View.VISIBLE
                    mView.no_item_foundll.visibility = View.VISIBLE
                    storeID = ""
                    sessionTwiclo.storeId = ""

                }
            }

        })

        viewmodel?.addRemoveCartResponse?.observe(requireActivity(), { user ->
            dismissIOSProgress()

            Log.e("cart_response", Gson().toJson(user))
            if (sessionTwiclo.isLoggedIn) {
                viewmodel?.getSearchApi(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken,
                    search_value!!,""
                )
                viewmodel?.getCartCountApi(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken
                )
            } else {
                viewmodel?.getSearchApi(
                    "",
                   "",
                    search_value!!,""
                )
                viewmodel?.getCartCountApi(
                    "",
                   ""
                )
            }

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        //add to cart
        viewmodel?.addToCartResponse?.observe(requireActivity(), Observer { user ->
            dismissIOSProgress()
            Log.e("search_stores_response", Gson().toJson(user))
            val mModelData: com.fidoo.user.data.model.AddToCartModel = user



            if (tempType.equals("custom")) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            } else {
                MainActivity.tempProductList!!.clear()
                MainActivity.addCartTempList!!.clear()
            }
            try {
                viewmodel?.getCartCountApi(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken
                )

                if (sessionTwiclo.isLoggedIn) {
                    viewmodel?.getSearchApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken,
                        search_value!!,""
                    )
                } else {
                    viewmodel?.getSearchApi(
                        "",
                        "",
                        search_value!!,""
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        })

        //clear cart all item
        viewmodel?.clearCartResponse?.observe(requireActivity(), Observer { user ->
            // dismissIOSProgress()

            Log.e("stores_response", Gson().toJson(user))

            try {
                if (tempType.equals("custom")) {

                    viewmodel!!.addToCartApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                    )
                    viewmodel?.getCartCountApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken
                    )

                } else {
                    viewmodel!!.addToCartApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                    )
                    viewmodel?.getCartCountApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        //error
        viewmodel?.failureResponse?.observe(requireActivity(), Observer { user ->
            Log.e("cart_response", Gson().toJson(user))
        })

        viewmodel?.customizeProductResponse?.observe(requireActivity(), Observer { user ->
            dismissIOSProgress()

            Log.e("stores___esponse", Gson().toJson(user))
            mModelDataTemp = user
            categoryy = ArrayList()

            for (i in 0 until mModelDataTemp?.category?.size!!) {
                if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
                    if (mModelDataTemp?.category?.get(i)!!.isMandatory.equals("0")) {

                    } else {

                        var customListModel: CustomListModel? = CustomListModel()
                        customListModel!!.category = mModelDataTemp?.category?.get(i)!!.catId
                        customListModel!!.id =
                            mModelDataTemp?.category?.get(i)!!.subCat[0].id.toInt()
                        customListModel!!.price = mModelDataTemp?.category?.get(i)!!.subCat[0].price
                        customListModel!!.subCatName =
                            mModelDataTemp?.category?.get(i)!!.subCat[0].subCatName

                        categoryy!!.add(customListModel)

                    }
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
            } catch (e: NumberFormatException) {
                Log.e("Exception", e.toString())
            }

            Log.e("tempPriceTotal", tempPrice.toString())

            try {
                mView.customAddBtn_search.text = "Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()

                adapter = StoreCustomItemsAdapter(
                    requireContext(),
                    mModelDataTemp?.category!!,
                    object : CustomCartPlusMinusClick {
                        override fun onIdSelected(
                            productId: String?,
                            type: String?,
                            price: String?,
                            sub_cat_name: String?,
                            tempSelectionCount: Int
                        ) {
                            if (type == "select") {
                                if (productId != null) {
                                    customIdsList!!.add(productId)
                                    customNamesList!!.add(sub_cat_name!!)
                                }
                                val customCheckBoxModel =
                                    CustomCheckBoxModel()
                                customCheckBoxModel.id = productId
                                customCheckBoxModel.price = price
                                customIdsListTemp!!.add(customCheckBoxModel)


                            } else if (type == "unselect") {
                                for (i in 0 until customIdsList!!.size) {
                                    if (customIdsList!![i] == productId) {
                                        customIdsList!!.removeAt(i)
                                        customNamesList!!.removeAt(i)
                                        customIdsListTemp!!.removeAt(i)
                                        break
                                    }
                                }
                            }


                            var tempIdPrice: Double? = 0.0
                            //plusMinusPrice = 0.0
                            tempPrice = 0.0


                            for (i in 0 until customIdsListTemp!!.size) {
                                tempIdPrice =
                                    tempIdPrice!! + customIdsListTemp!![i].price.toDouble()
                            }

                            for (i in 0 until categoryy!!.size) {
                                tempIdPrice = tempIdPrice!! + categoryy!![i].price.toDouble()
                            }

                            tempPrice = tempOfferPrice!!.toDouble() + tempIdPrice!!
                            Log.e("TempPrice on ID ", tempPrice.toString())
                            //plusMinusPrice = tempIdPrice + tempPrice!!
                            //tempPrice = tempPrice?.plus(plusMinusPrice)
                            mView.customAddBtn_search.text =
                                "Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()

                        }
                    },
                    categoryy,
                    object : AdapterCustomRadioClick {
                        override fun onCustomRadioClick(checkedId: String?, position: String?) {
                            var tempCat: String? = ""
                            var tempPrice: String? = ""
                            var tempPriceSubCat_Name: String? = ""
                            for (i in 0 until mModelDataTemp?.category!!.size) {
                                for (j in 0 until mModelDataTemp?.category!!.get(i).subCat.size) {
                                    if (checkedId == mModelDataTemp?.category!!.get(i).subCat.get(j).id) {
                                        tempCat = mModelDataTemp?.category!!.get(i).catId
                                        tempPrice =
                                            mModelDataTemp?.category!!.get(i).subCat.get(j).price
                                        tempPriceSubCat_Name =
                                            mModelDataTemp?.category!!.get(i).subCat.get(j).subCatName
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
                                    categoryy!!.get(i).subCatName = tempPriceSubCat_Name
                                    break
                                }
                            }
                            if (tempAddEdit.equals("edit")) {
                                categoryy!!.get(tempAddEditId!!.toInt()).id = checkedId!!.toInt()
                                categoryy!!.get(tempAddEditId.toInt()).price = tempPrice
                                categoryy!!.get(tempAddEditId.toInt()).subCatName =
                                    tempPriceSubCat_Name
                            } else {
                                var customListModel: CustomListModel? =
                                    CustomListModel()
                                customListModel!!.category = tempCat
                                customListModel.id = checkedId!!.toInt()
                                customListModel.price = tempPrice
                                customListModel.subCatName = tempPriceSubCat_Name
                                categoryy!!.add(customListModel)
                            }
                            var tempPricee: Double? = 0.0
                            for (i in 0 until customIdsListTemp!!.size) {
                                tempPricee =
                                    tempPricee!! + customIdsListTemp!!.get(i).price.toDouble()
                            }
                            for (i in 0 until categoryy!!.size) {
                                tempPricee = tempPricee!! + categoryy!!.get(i).price.toDouble()
                            }
                            tempPricee = tempOfferPrice!!.toDouble() + tempPricee!!
                            mView.customAddBtn_search.text =
                                resources.getString(R.string.ruppee) + tempPricee.toString()
                        }
                    }
                )

                mView.customItemsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
                mView.customItemsRecyclerview.setHasFixedSize(true)
                mView.customItemsRecyclerview.adapter = adapter

            } catch (e: Exception) { }

            })

    }

    private fun RecentSearchRv(recentList: ArrayList<String>) {
        if (recentList.size>0) {
            mView.store_item_tv.text = "Recent Searches"
        }else{
            mView.store_item_tv.text = "No Recent Searches"
        }
        if (recentList.size>6){
            for (i in 0 until 2) {
                recentList.removeAt(i)
            }
        }
        recentSearchAdapter = RecentSearchAdapter(
            requireContext(),
            recentList,
            object : RecentSearchAdapter.RecentSearchItemClick {
                override fun onItemClick(pos: Int, value: String) {
                    search_value = value
                    mView.searchEdt_new_fgmt?.setText(search_value)
                    mView.searchEdt_new_fgmt?.setSelection(mView.searchEdt_new_fgmt?.text.toString().length)
                }
            })
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        mView.recentsearch_rv_fgmt.setLayoutManager(layoutManager)
        mView.recentsearch_rv_fgmt.setHasFixedSize(true)
        mView.recentsearch_rv_fgmt.setItemAnimator(DefaultItemAnimator())
        mView.recentsearch_rv_fgmt.adapter = recentSearchAdapter

    }

    private fun showLoginDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
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
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Replace cart item!")
        builder.setMessage("Do you want to discard the previous selection?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { _, _ ->
            viewmodel?.clearCartApi(
                sessionTwiclo.loggedInUserDetail.accountId,
                sessionTwiclo.loggedInUserDetail.accessToken
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
    override fun onItemClick(
        productId: String?,
        type: String?,
        count: String?,
        offerPrice: String?,
        customize_count: Int?,
        productType: String?,
        cart_id: String?
    ) {

        Log.d("onItemClick___", type!!)
        tempType = type
        tempCount = count
        tempProductId = productId
        mCustomizeCount = customize_count
        tempOfferPrice = offerPrice


        if (type == "custom") {
            if (mCustomizeCount == 0) {
                customIdsListTemp?.clear()
                customIdsList!!.clear()
                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    //searchLay.visibility = View.GONE
                } else {
                    //searchLay.visibility = View.VISIBLE
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

                }
                if (productId != null) {
                    viewmodel?.customizeProductApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken, productId
                    )
                }
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Your previous customization")
                builder.setMessage(tempCount)
                builder.setPositiveButton("I'LL CHOOSE") { dialogInterface, which ->

                    customIdsList!!.clear()
                    if (productId != null) {
                        viewmodel?.customizeProductApi(
                            sessionTwiclo.loggedInUserDetail.accountId,
                            sessionTwiclo.loggedInUserDetail.accessToken, productId
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

            try {
                if (sessionTwiclo.storeId.equals(storeID) || sessionTwiclo.storeId.equals(
                        ""
                    )
                ) {
                    sessionTwiclo.storeId = storeID
                    Log.e("  store id", sessionTwiclo.storeId.toString())
                    showIOSProgress()

                    viewmodel!!.addToCartApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        ""
                    )
                    sessionTwiclo.storeId = storeID
                } else {
                    clearCartPopup()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    //for plus and minus click
    override fun onItemAddRemoveClick(
        productId: String?,
        count: String?,
        type: String?,
        price: String?,
        storeId: String?,
        cart_id: String?,
        position: Int
    ) {
        if (cart_id != null) {
            cartId = cart_id

        }
        //showIOSProgress()
        //SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
        if (type.equals("add")) {
            storeID = storeId!!
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
                    showIOSProgress()
                    viewmodel!!.addToCartApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        cartId
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
                showIOSProgress()
                try {
                    viewmodel!!.addToCartApi(
                        sessionTwiclo.loggedInUserDetail.accountId,
                        sessionTwiclo.loggedInUserDetail.accessToken,
                        MainActivity.addCartTempList!!,
                        cartId
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        } else {
            showIOSProgress()
            try {
                viewmodel?.addRemoveCartDetails(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken,
                    productId!!,
                    "remove",
                    "0",
                    "", cartId,
                    customIdsList!!
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun clearCart() {
        viewmodel?.clearCartApi(
            sessionTwiclo.loggedInUserDetail.accountId,
            sessionTwiclo.loggedInUserDetail.accessToken
        )
    }

    //on add item first time
    override fun onAddItemClick(
        productId: String?,
        items: String?,
        offerPrice: String?,
        isCustomize: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    ) {

        if (!items.equals("") || isCustomize.equals("1")) {

            tempOfferPrice = offerPrice
            tempPrice = 0.0
            tempProductId = productId

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Your previous customization")
            builder.setMessage(items)
            builder.setPositiveButton("I'LL CHOOSE") { _, which ->
                if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }


                //tempProductId = productId
                //showIOSProgress()
                customIdsList!!.clear()
                viewmodel?.customizeProductApi(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken, productId!!
                )
            }

            //performing negative action
            builder.setNegativeButton("REPEAT") { _, which ->
                //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
                //showIOSProgress()
                viewmodel?.addRemoveCartDetails(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken,
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
        } else {
            viewmodel?.addRemoveCartDetails(
                sessionTwiclo.loggedInUserDetail.accountId,
                sessionTwiclo.loggedInUserDetail.accessToken,
                productId!!,
                "add",
                isCustomize!!,
                prodcustCustomizeId!!,
                cart_id!!,
                customIdsList!!
            )
        }
        checkvalidation=1
    }

    //customize item minus click
    override fun onRemoveItemClick(
        productId: String?,
        quantity: String?,
        isCustomize: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    ) {
        try {
            if (cart_id != null) {
                showIOSProgress()
                viewmodel?.addRemoveCartDetails(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken,
                    productId!!,
                    "remove",
                    isCustomize!!,
                    prodcustCustomizeId!!,
                    cart_id,
                    customIdsList!!
                )
            }
            checkvalidation=1
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        ProfileFragment.addManages=""
        if (checkvalidation == 1) {
            //Here we have called Api of getGroceryProducts

            if (sessionTwiclo.isLoggedIn) {
                viewmodel?.getSearchApi(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken,
                    search_value!!,""
                )
                viewmodel?.getCartCountApi(
                    sessionTwiclo.loggedInUserDetail.accountId,
                    sessionTwiclo.loggedInUserDetail.accessToken
                )
            } else {
                viewmodel?.getSearchApi(
                    "",
                    "",
                    search_value!!,""
                )
            }


        }
    }

    fun showIOSProgress() {
        closeProgress()
        _progressDlg = ProgressDialog(requireContext(), R.style.TransparentProgressDialog)
        _progressDlg!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        _progressDlg!!.setCancelable(false)
        _progressDlg!!.show()
    }

    fun closeProgress() {
        if (_progressDlg == null) {
            return
        }
        _progressDlg!!.dismiss()
        _progressDlg = null
    }

    fun dismissIOSProgress() {
        if (_progressDlg == null) {
            return
        }
        _progressDlg!!.dismiss()
        _progressDlg = null
    }

    fun isNetworkConnected(): Boolean {
        return CheckConnectivity(requireContext()).isNetworkAvailable
    }


}