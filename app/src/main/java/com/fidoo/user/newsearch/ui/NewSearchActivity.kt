package com.fidoo.user.newsearch.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AbsListView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.dailyneed.ui.CategoryProductListActivity
import com.fidoo.user.dashboard.listener.ClickEventOfDashboard
import com.fidoo.user.dashboard.model.newmodel.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.ActivityNewSearchBinding
import com.fidoo.user.newsearch.adapter.SearchCategoryAdapter
import com.fidoo.user.newsearch.model.SuggestionX
import com.fidoo.user.newsearch.viewmodel.SearchNewViewModel
import com.fidoo.user.restaurants.activity.StoreItemsActivity
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils

class NewSearchActivity : BaseActivity(), ClickEventOfDashboard {
    private lateinit var binding: ActivityNewSearchBinding
    var search_value: String? = ""
    var key_value: String? = ""
    var service_id: String? = ""
    var sessionTwiclo: SessionTwiclo? = null
    var viewModel: SearchNewViewModel? = null
    var searchCategoryAdapter: SearchCategoryAdapter? = null
    var recentSearch: ArrayList<String>? = null

    //for pagination
    var totalItem: Int? = 200
    var table_count: Int? = 0
    private var manager: GridLayoutManager? = null
    private var currentItems = 0
    private var page_count = 0
    private var totalItems: Int = 0
    private var scrollOutItems: Int = 0
    private var isScrolling = false
    private var isMore = false
    private var hit = 0
    private var pagecount = 0

    var mainList: ArrayList<SuggestionX>? = null
    var latestList: ArrayList<SuggestionX>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionTwiclo = SessionTwiclo(this)
        viewModel = ViewModelProvider(this).get(SearchNewViewModel::class.java)
        manager = GridLayoutManager(this, 1)
        mainList = ArrayList()
        latestList = ArrayList()
        recentSearch = ArrayList()
        try {
            service_id=intent.getStringExtra("service_id")
            Log.d("service_id___",service_id!!)
        }catch (e:Exception){
            e.printStackTrace()
        }

        onClick()
        onResponse()
        showKeyboard(binding.searchKeyETxtAct)

    }

    private fun onResponse() {
        viewModel!!.keywordBasedSearchSuggestionsRes!!.observe(this, {

            if (it.error_code == 200) {
                hit = 0
                isMore = it.more_value
                latestList!!.clear()
                binding.showingResult.text = "Showing Results (" + it.total_count.toString() + ")"

                Log.d("keyword___", Gson().toJson(it))

                if (pagecount > 0) {
                    latestList = it.suggestions as ArrayList
                    mainList!!.addAll(latestList!!)
                    searchCategoryAdapter!!.updateData(mainList!!, isMore)
                    searchCategoryAdapter!!.notifyDataSetChanged()
                } else {
                    mainList = it.suggestions as ArrayList
                    rvCategoryList(mainList!!)
                }

            }
        })
    }


    private fun rvCategoryList(arrayList: ArrayList<SuggestionX>) {
        searchCategoryAdapter = SearchCategoryAdapter(
            this,
            arrayList,
            object : SearchCategoryAdapter.CategoryItemClick {
                override fun onItemClick(pos: Int, model: SuggestionX) {
                    key_value = model.name
                    Log.d(
                        "key_value__",
                        model.category_id + "--" + model.category_name + "--" + model.subcategory_id
                    )

                    if (model.type.equals("Restaurants")) {
                        AppUtils.startActivityRightToLeft(
                            this@NewSearchActivity,
                            Intent(this@NewSearchActivity, StoreItemsActivity::class.java)
                                .putExtra("storeId", model.store_id)
                                .putExtra("search_value", search_value)
                                .putExtra("storeName", model.name)
                                .putExtra("store_location", model.locality)
                                .putExtra("delivery_time", model.delivery_time)
                                .putExtra(   "cuisine_types", model.cuisines.joinToString(separator = ", "))
                                .putExtra("coupon_desc", "")
                                .putExtra("distance", model.distance)
                        )
                    } else if (model.type.equals("Dish")) {
                        AppUtils.startActivityRightToLeft(
                            this@NewSearchActivity,
                            Intent(this@NewSearchActivity, NewSearchStoreListingActivity::class.java)
                                .putExtra("storeId", model.store_id)
                                .putExtra("search_value", search_value)
                                .putExtra("key_value", key_value)
                                .putExtra("storeName", model.name)
                                .putExtra("store_location", model.locality)
                                .putExtra("delivery_time", model.delivery_time)
                                .putExtra(
                                    "cuisine_types",
                                    model.cuisines.joinToString(separator = ", ")
                                )
                                .putExtra("coupon_desc", "")
                                .putExtra("distance", model.distance)
                        )
                    } else if (model.type.equals("Grocery")||model.type.equals("Medicine")) {
                        AppUtils.startActivityRightToLeft(
                            this@NewSearchActivity,
                            Intent(this@NewSearchActivity, CategoryProductListActivity::class.java)
                                .putExtra("search_value", search_value)
                                .putExtra("category_id", model.category_id)
                                .putExtra("subCategory_id", model.subcategory_id)
                                .putExtra("category_name", model.category_name)
                        )
                    }
                }
            })

        binding.rvSearchResult!!.adapter = searchCategoryAdapter
        binding.rvSearchResult!!.layoutManager = manager
        binding.rvSearchResult!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                    // StoreItemsActivity.handleresponce = 0
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager!!.childCount
                totalItems = manager!!.itemCount
                scrollOutItems = manager!!.findFirstVisibleItemPosition()

                var firstVisibleItem = manager!!.findFirstCompletelyVisibleItemPosition()

                if (dy > 1) {
                    if (isScrolling && currentItems + scrollOutItems == totalItems) {
                        Log.d("totalItem___", table_count.toString() + "---" + firstVisibleItem)

                        if (isScrolling) {
                            if (sessionTwiclo!!.isLoggedIn) {
                                if (isMore) {
                                    if (hit == 0) {
                                        pagecount++
                                        viewModel!!.keywordBasedSearchSuggestionsApi(
                                            sessionTwiclo!!.loggedInUserDetail.accountId,
                                            sessionTwiclo!!.loggedInUserDetail.accessToken,
                                            search_value!!,
                                            sessionTwiclo!!.userLat,
                                            sessionTwiclo!!.userLng,
                                            pagecount.toString(),service_id!!
                                        )
                                        hit = 1
                                    }
                                }
                            }
                            isScrolling = false
                        }
                    }
                }

            }

        })

    }

    private fun onClick() {

        binding.searchKeyETxtAct.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                search_value = s.toString()
                //  recentSearch!!.add(search_value!!)
                //   Log.d("dsscountfff", "$start$count")

                if (count < 1) {
                    page_count = 0
                    mainList!!.clear()
                    binding.showingResult.text = "Showing Results"
                    searchCategoryAdapter!!.updateData(mainList!!, isMore)
                    searchCategoryAdapter!!.notifyDataSetChanged()
                }



                if (search_value!!.isNotEmpty()) {
                    if (sessionTwiclo!!.isLoggedIn) {
                        viewModel!!.keywordBasedSearchSuggestionsApi(
                            sessionTwiclo!!.loggedInUserDetail.accountId,
                            sessionTwiclo!!.loggedInUserDetail.accessToken,
                            search_value!!,
                            sessionTwiclo!!.userLat,
                            sessionTwiclo!!.userLng,
                            page_count.toString(), service_id!!
                        )
                    } else {
                        if (sessionTwiclo!!.userLat.isNotEmpty()) {
                            page_count = 0
                            mainList!!.clear()
                            binding.showingResult.text = "Showing Results"
//                            searchCategoryAdapter!!.updateData(mainList!!, isMore)
//                            searchCategoryAdapter!!.notifyDataSetChanged()
//
                            viewModel!!.keywordBasedSearchSuggestionsApi(
                                "",
                                "",
                                search_value!!,
                                sessionTwiclo!!.userLat,
                                sessionTwiclo!!.userLng,
                                page_count.toString(), service_id!!
                            )
                        }
                    }
                } else {
                    if (sessionTwiclo!!.isLoggedIn) {
                        viewModel!!.keywordBasedSearchSuggestionsApi(
                            sessionTwiclo!!.loggedInUserDetail.accountId,
                            sessionTwiclo!!.loggedInUserDetail.accessToken,
                            search_value!!,
                            sessionTwiclo!!.userLat,
                            sessionTwiclo!!.userLng,
                            page_count.toString(), service_id!!
                        )
                    } else {
                        if (sessionTwiclo!!.userLat.isNotEmpty()) {
                            page_count = 0
                            mainList!!.clear()
                            binding.showingResult.text = "Showing Results"
//                            searchCategoryAdapter!!.updateData(mainList!!, isMore)
//                            searchCategoryAdapter!!.notifyDataSetChanged()
//
                            viewModel!!.keywordBasedSearchSuggestionsApi(
                                "",
                                "",
                                search_value!!,
                                sessionTwiclo!!.userLat,
                                sessionTwiclo!!.userLng,
                                page_count.toString(), service_id!!
                            )
                        }

                    }
                }

                Log.d("search_value___", search_value.toString())

            }
        })

        binding.editTxtAct.setOnClickListener {
            try {
                mainList!!.clear()
                //hideKeyboard(binding.searchKeyETxtAct)
                binding.showingResult.text = "Showing Result"
                searchCategoryAdapter!!.updateData(mainList!!, isMore)
                searchCategoryAdapter!!.notifyDataSetChanged()
                binding.searchKeyETxtAct.text.clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.backIconBtn.setOnClickListener { AppUtils.finishActivityLeftToRight(this) }
    }

    override fun onExploreCatClick(outerPosition: Int?, innerPosition: Int?, model: Service) {}

    override fun onCurationCatClick(outerPosition: Int?, innerPosition: Int?, model: Curation) {}

    override fun onPackageCatClick(
        outerPosition: Int?,
        innerPosition: Int?,
        model: PackageCategory
    ) {
    }

    override fun onOfferCatClick(outerPosition: Int?, innerPosition: Int?, model: Offer) {}

    override fun onShopCatClick(outerPosition: Int?, innerPosition: Int?, model: ShopCategory) {}

    override fun onUpcomingServicesClick(
        outerPosition: Int?,
        innerPosition: Int?,
        model: UpcomingServices
    ) {}

}