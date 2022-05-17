package com.fidoo.user.newsearch.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.ActivityNewSearchStorelistingBinding
import com.fidoo.user.newsearch.adapter.SearchCategoryStoreAdapter
import com.fidoo.user.newsearch.model.Store
import com.fidoo.user.newsearch.viewmodel.SearchNewViewModel
import com.fidoo.user.restaurants.activity.NewDBStoreItemsActivity
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_new_search_storelisting.*

class NewSearchStoreListingActivity : BaseActivity() {
    private lateinit var binding: ActivityNewSearchStorelistingBinding
    var search_value: String? = ""
    var key_value: String? = ""
    var storeId: String? = ""
    var storeName: String? = ""
    var store_location: String? = ""
    var delivery_time: String? = ""
    var cuisine_types: String? = ""
    var coupon_desc: String? = ""
    var distance: String? = ""
    var sessionTwiclo: SessionTwiclo? = null
    var viewModel: SearchNewViewModel? = null
    var searchCategoryStoreAdapter: SearchCategoryStoreAdapter? = null

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
    var mainList: ArrayList<Store>? = null
    var latestList: ArrayList<Store>? = null
    private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSearchStorelistingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionTwiclo = SessionTwiclo(this)
        viewModel = ViewModelProvider(this).get(SearchNewViewModel::class.java)
        manager = GridLayoutManager(this, 1)
        mainList = ArrayList()
        latestList = ArrayList()

        try {
            search_value = intent.getStringExtra("search_value").toString()
            key_value = intent.getStringExtra("key_value").toString()
            Log.d("key_value__", intent.getStringExtra("delivery_time").toString())
            storeId = intent.getStringExtra("storeId").toString()
            storeName = intent.getStringExtra("storeName").toString()
            store_location = intent.getStringExtra("store_location").toString()
            delivery_time = intent.getStringExtra("delivery_time").toString()
            cuisine_types = intent.getStringExtra("cuisine_types").toString()
            coupon_desc = intent.getStringExtra("coupon_desc").toString()
            distance = intent.getStringExtra("distance").toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        onClick()

        swipeRefreshLaySearch.setOnRefreshListener {
            if (sessionInstance.isLoggedIn) {
                viewModel!!.keywordBasedSearchResultsApi(
                    sessionTwiclo!!.loggedInUserDetail.accountId,
                    sessionTwiclo!!.loggedInUserDetail.accessToken,
                    key_value!!,
                    sessionTwiclo!!.userLat,
                    sessionTwiclo!!.userLng,
                    pagecount.toString()
                )
            } else {
                viewModel!!.keywordBasedSearchResultsApi(
                    "",
                    "",
                    key_value!!,
                    sessionTwiclo!!.userLat,
                    sessionTwiclo!!.userLng,
                    pagecount.toString()
                )
            }
        }
        showIOSProgress()

        if (sessionInstance.isLoggedIn) {
            viewModel!!.keywordBasedSearchResultsApi(
                sessionTwiclo!!.loggedInUserDetail.accountId,
                sessionTwiclo!!.loggedInUserDetail.accessToken,
                key_value!!,
                sessionTwiclo!!.userLat,
                sessionTwiclo!!.userLng,
                pagecount.toString()
            )
        } else {
            viewModel!!.keywordBasedSearchResultsApi(
                "",
                "",
                key_value!!,
                sessionTwiclo!!.userLat,
                sessionTwiclo!!.userLng,
                pagecount.toString()
            )
        }

        onResponse()

    }

    private fun onResponse() {
        viewModel!!.keywordBasedSearchResultsRes!!.observe(this, {
            dismissIOSProgress()
            swipeRefreshLaySearch.isRefreshing=false
            Log.d("sdfdddfssd", Gson().toJson(it))
            if (it.error_code == 200) {
                hit = 0
                isMore = it.more_value
                latestList!!.clear()

                // binding.showingResult.text = "Showing Result (" + it.total_count.toString() + ")"

                if (pagecount > 0) {

                    latestList = it.stores as ArrayList
                    mainList!!.addAll(latestList!!)
                    searchCategoryStoreAdapter!!.updateData(mainList!!, isMore)
                    searchCategoryStoreAdapter!!.notifyDataSetChanged()

                } else {
                    mainList = it.stores as ArrayList
                    rvCategoryList(mainList!!)
                }

            }

        })
    }

    private fun rvCategoryList(arrayList: ArrayList<Store>) {
        searchCategoryStoreAdapter = SearchCategoryStoreAdapter(
            this,
            arrayList!!,
            key_value!!,
            object : SearchCategoryStoreAdapter.CategoryItemClick {
                override fun onItemClick(pos: Int, model: Store) {
                  var delivery_time=model.delivery_time.toString()

                    AppUtils.startActivityRightToLeft(
                        this@NewSearchStoreListingActivity,
                        // Intent(this@NewSearchStoreListingActivity, StoreItemsActivity::class.java)
                        //  Intent(this@NewSearchStoreListingActivity, NewStoreItemsActivity::class.java)
                        Intent(
                            this@NewSearchStoreListingActivity,
                            NewDBStoreItemsActivity::class.java
                        )
                            .putExtra("storeId", storeId)
                            .putExtra("search_value", search_value)
                            .putExtra("storeName", storeName)
                            .putExtra("store_location", model.locality)
                            .putExtra("delivery_time", delivery_time)
                            .putExtra("cuisine_types", model.cuisines.joinToString(separator = ", "))
                            .putExtra("coupon_desc", "")
                            .putExtra("distance", model.distance)
                    )
                }
            })

        binding.searchFilterRv!!.adapter = searchCategoryStoreAdapter
        binding.searchFilterRv!!.layoutManager = manager
        binding.searchFilterRv!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                    //  handleresponce = 0
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
                                        viewModel!!.keywordBasedSearchResultsApi(
                                            sessionTwiclo!!.loggedInUserDetail.accountId,
                                            sessionTwiclo!!.loggedInUserDetail.accessToken,
                                            key_value!!,
                                            sessionTwiclo!!.userLat,
                                            sessionTwiclo!!.userLng,
                                            pagecount.toString()
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
        binding.backIconStoreListing.setOnClickListener {
            AppUtils.finishActivityLeftToRight(this)
        }
    }

    override fun onBackPressed() {
        AppUtils.finishActivityLeftToRight(
            this
        )
    }
    override fun onResume() {
        super.onResume()
        deleteRoomDataBase()
    }

    //delete room data
    private fun deleteRoomDataBase() {
        Thread {
            restaurantProductsDatabase = Room.databaseBuilder(
                applicationContext,
                RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
            restaurantProductsDatabase!!.resProductsDaoAccess()!!.deleteAll()

        }.start()
    }

}