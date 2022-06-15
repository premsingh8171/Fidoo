package com.fidoo.user.newsearch.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.android.datatransport.runtime.ExecutionModule_ExecutorFactory.executor
import android.widget.AbsListView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.R
import com.fidoo.user.dailyneed.ui.CategoryProductListActivity
import com.fidoo.user.dashboard.listener.ClickEventOfDashboard
import com.fidoo.user.dashboard.model.newmodel.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.ActivityNewSearchBinding
import com.fidoo.user.newsearch.adapter.SearchCategoryAdapter
import com.fidoo.user.newsearch.model.SuggestionX
import com.fidoo.user.newsearch.viewmodel.SearchNewViewModel
import com.fidoo.user.restaurants.activity.NewDBStoreItemsActivity
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_new_search.*
import kotlinx.android.synthetic.main.fragment_search_new.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log


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
	var text_count=0
	var textchane= false
	private var timer: CountDownTimer? = null
	private var manager: GridLayoutManager? = null
	private var currentItems = 0
	private var page_count = 0
	private var totalItems: Int = 0
	private var scrollOutItems: Int = 0
	private var isScrolling = false
	private var isMore = false
	private var hit = 0
	private var textclear= false
	private var pagecount = 0
	var changeLable1: Long = 2
	var changeLable2: Long = 4

	var mainList: ArrayList<SuggestionX>? = null
	var nonavailable_mainList: ArrayList<SuggestionX>? = null
	var latestList: ArrayList<SuggestionX>? = null
	var latestList2: ArrayList<SuggestionX>? = null
	var latestList3: ArrayList<SuggestionX>? = null
	var latestList4: ArrayList<SuggestionX>? = null
	private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityNewSearchBinding.inflate(layoutInflater)
		setContentView(binding.root)
		sessionTwiclo = SessionTwiclo(this)
		viewModel = ViewModelProvider(this).get(SearchNewViewModel::class.java)
		manager = GridLayoutManager(this, 1)
		mainList = ArrayList()
		nonavailable_mainList = ArrayList()
		latestList = ArrayList()
		latestList2 = ArrayList()
		latestList3 = ArrayList()
		latestList4 = ArrayList()
		recentSearch = ArrayList()


		try {
			service_id = intent.getStringExtra("service_id")
			Log.d("service_id______", service_id!!)
		} catch (e: Exception) {
			e.printStackTrace()
		}

		timer = object : CountDownTimer(6000, 1000) {
			override fun onTick(millisUntilFinished: Long) {
				Log.e("_Timer", "seconds remaining: " + millisUntilFinished / 1000)
				if (changeLable1 == (millisUntilFinished / 1000)) {
					binding.searchKeyETxtAct.hint = "Search Products"
				} else if (changeLable2 == (millisUntilFinished / 1000)) {
					binding.searchKeyETxtAct.hint = "Search Restaurants"
				} else {
					binding.searchKeyETxtAct.hint = "Search Dishes"
				}
			}

			override fun onFinish() {
				timer!!.start()
			}
		}

		timer!!.start()


		binding.searchKeyETxtAct.isCursorVisible= true
		showKeyboard(binding.searchKeyETxtAct)
		onClick()

		onResponse()

//		binding.searchKeyETxtAct.setOnKeyListener( object :View.OnKeyListener{
//			override fun onKey(p0: View?, p1: Int, keyEvent: KeyEvent?): Boolean {
//				if (p1 == KeyEvent.ACTION_DOWN ) {
//
//
//					textclear = true
//
//					if (!(intent.getStringExtra("type").equals("Restaurent"))){
//						binding.xyz.noItemFoundll.visibility= View.GONE
//						binding!!.xyz!!.root.visibility= View.GONE
//						binding.showingResult.visibility= View.VISIBLE
//						binding.rvSearchResult.visibility= View.VISIBLE
//					}else if ((intent.getStringExtra("type").equals("Restaurent"))){
//
//						binding!!.xyz2!!.root.visibility= View.GONE
//						binding.showingResult.visibility= View.VISIBLE
//						binding.rvSearchResult.visibility= View.VISIBLE
//					}
//
//
//				}
//				return false
//			}
//
//
//		})


//		binding.searchKeyETxtAct.setOnKeyListener { v, i, keyEvent ->
//
//			if (i == KeyEvent.KEYCODE_DEL){
//
//			}
//
//
//			true
//		}




		binding.searchKeyETxtAct.setOnEditorActionListener { v, actionId, event ->

			Log.d("actoon","${actionId}")

			if (actionId == EditorInfo.IME_ACTION_DONE-1){

				search_value= binding.searchKeyETxtAct.text.toString()
				textchane= false
				searchCategoryAdapter!!.updateData(mainList!!, isMore)
				hideKeyboard(binding.searchKeyETxtAct)
			}

			false
		}
	}




	private fun onResponse() {
		viewModel!!.keywordBasedSearchSuggestionsRes!!.observe(this) {

			if (it.error_code == 200) {
				hit = 0
				isMore = it.more_value
				latestList!!.clear()
//				latestList4= it.suggestions as ArrayList<SuggestionX>
//				latestList4!!.forEach {
//					Log.d("checkresp", "${it.name + it.available}")
//				}

//				mainList!!.clear()
//				latestList2!!.clear()
				Log.d("keyword___", Gson().toJson(it))

				if (search_value!!.isNotEmpty()) {
					binding.rvSearchResult.visibility = View.VISIBLE
					binding.showingResult.visibility = View.VISIBLE
					if (pagecount > 0) {

						latestList = it.suggestions as ArrayList

//
						if (textchane) {
							mainList!!.clear()
							textchane= false
						}
						if (intent.getStringExtra("type").equals("Restaurent")){
							for (i in 0 until latestList!!.size) {
								if (latestList!![i].available.equals("1") && latestList!![i].type.equals("Restaurants")) {
									mainList!!.add(latestList!![i])
								}

							}
						}else {

							for (i in 0 until latestList!!.size) {
								if (latestList!![i].available.equals("1")) {
									mainList!!.add(latestList!![i])
								}

							}
						}

						//here we remove duplicate item
						val s: Set<SuggestionX> = LinkedHashSet<SuggestionX>(mainList)


					//	mainList!!.clear()
						mainList!!.addAll(s)


							if (mainList.isNullOrEmpty()){

								   if((intent.getStringExtra("type").equals("Restaurent"))){

									   binding!!.xyz2!!.root.visibility= View.VISIBLE
									   binding.showingResult.visibility= View.GONE
									   binding.rvSearchResult.visibility= View.GONE
								   }else {
									   binding.xyz.noItemFoundll.visibility = View.VISIBLE
									   binding!!.xyz!!.root.visibility = View.VISIBLE
									   binding.showingResult.visibility = View.GONE
									   binding.rvSearchResult.visibility = View.GONE
								   }

							}else {
								binding.xyz.root.visibility = View.GONE
								binding.xyz.noItemFoundll.visibility = View.GONE
								binding.xyz2.root.visibility = View.GONE
							}

						searchCategoryAdapter!!.updateData(mainList!!, isMore)
						searchCategoryAdapter!!.notifyDataSetChanged()
					} else {

						latestList2 = it.suggestions as ArrayList
						mainList!!.clear()
						if (intent.getStringExtra("type").equals("Restaurent")){
							for (i in 0 until latestList2!!.size) {
								if (latestList2!![i].available.equals("1") && latestList2!![i].type.equals("Restaurants")) {
									mainList!!.add(latestList2!![i])
								}

							}
						}else {

							for (i in 0 until latestList2!!.size) {
								if (latestList2!![i].available.equals("1")) {
									mainList!!.add(latestList2!![i])
								}

							}
						}

						//here we remove duplicate item
						val s: Set<SuggestionX> = LinkedHashSet<SuggestionX>(mainList)

						mainList!!.clear()
						mainList!!.addAll(s)
						if (mainList.isNullOrEmpty()){

							if((intent.getStringExtra("type").equals("Restaurent"))){

								binding!!.xyz2!!.root.visibility= View.VISIBLE
								binding.showingResult.visibility= View.GONE
								binding.rvSearchResult.visibility= View.GONE
							}else {
								binding.xyz.noItemFoundll.visibility = View.VISIBLE
								binding!!.xyz!!.root.visibility = View.VISIBLE
								binding.showingResult.visibility = View.GONE
								binding.rvSearchResult.visibility = View.GONE
							}

						}else {
							binding.xyz.root.visibility = View.GONE
							binding.xyz.noItemFoundll.visibility = View.GONE
							binding.xyz2.root.visibility = View.GONE
						}

						if (text_count==0){
							rvCategoryList(mainList!!)
							text_count++
						}else{
							searchCategoryAdapter!!.updateData(mainList!!, isMore)
							searchCategoryAdapter!!.notifyDataSetChanged()
						}
					}
					binding.showingResult.text =
						"Showing Results (" + mainList!!.size.toString() + ")"

				} else {
					binding.rvSearchResult!!.visibility = View.GONE
					binding.showingResult!!.visibility = View.GONE
				}
			}
		}
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
							//Intent(this@NewSearchActivity, StoreItemsActivity::class.java)
							//Intent(this@NewSearchActivity, NewStoreItemsActivity::class.java)
							Intent(this@NewSearchActivity, NewDBStoreItemsActivity::class.java)
								.putExtra("storeId", model.store_id)
								.putExtra("search_value", search_value)
								.putExtra("storeName", model.name)
								.putExtra("store_location", model.locality)
								.putExtra("delivery_time", model.delivery_time)
								.putExtra("cuisine_types", model.cuisines.joinToString(separator = ", "))
								.putExtra("coupon_desc", "")
								.putExtra("distance", model.distance)
						)
					} else if (model.type.equals("Dish")) {
						AppUtils.startActivityRightToLeft(
							this@NewSearchActivity,
							Intent(
								this@NewSearchActivity,
								NewSearchStoreListingActivity::class.java
							)
								.putExtra("storeId", model.store_id)
								.putExtra("search_value", search_value)
								.putExtra("key_value", key_value)
								.putExtra("storeName", model.name)
								.putExtra("store_location", model.locality)
								.putExtra("delivery_time", model.delivery_time)
								.putExtra("cuisine_types", model.cuisines.joinToString(separator = ", "))
								.putExtra("coupon_desc", "")
								.putExtra("distance", model.distance)
						)
					} else if (model.type.equals("Grocery") || model.type.equals("Medicine")|| model.type.equals("Pet Essentials")) {
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
					textchane= false
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
											pagecount.toString(), service_id!!
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

			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				binding.editTxtAct.setTextColor(resources.getColor(R.color.colorTextGray))

				if (after<count){
					if (after%3==0) {
						textclear = true
					}
				}else{
					textclear= true
				}

				binding!!.xyz2!!.root.visibility = View.GONE
				binding.xyz.noItemFoundll.visibility = View.GONE
				binding!!.xyz!!.root.visibility = View.GONE

				if (!(intent.getStringExtra("type").equals("Restaurent"))) {

					if (!(binding.xyz2.root.isVisible)) {
						binding.showingResult.visibility = View.VISIBLE
						binding.rvSearchResult.visibility = View.VISIBLE
					}else{
						binding!!.xyz2!!.root.visibility = View.GONE
						binding.xyz.noItemFoundll.visibility = View.GONE
						binding!!.xyz!!.root.visibility = View.GONE
					}
				}else{
					if (!(binding.xyz.root.isVisible)) {
						binding.showingResult.visibility = View.VISIBLE
						binding.rvSearchResult.visibility = View.VISIBLE
					}else{
						binding!!.xyz2!!.root.visibility = View.GONE
						binding.xyz.noItemFoundll.visibility = View.GONE
						binding!!.xyz!!.root.visibility = View.GONE
					}
				}


			}

			@SuppressLint("NotifyDataSetChanged")
			override fun onTextChanged(
				s: CharSequence, start: Int,
				before: Int, count: Int
			) {



				if (textclear){


						textchane= true

						search_value = s.toString()
						//binding.editTxtAct.setTextColor(resources.getColor(R.color.black))
						//  recentSearch!!.add(search_value!!)
						//   Log.d("dsscountfff", "$start$count")

						if (count==0){
							binding.editTxtAct.setTextColor(resources.getColor(R.color.colorTextGray))
						}else{
							binding.editTxtAct.setTextColor(resources.getColor(R.color.black))
						}
						if (count < 1) {
							page_count = 0
							mainList!!.clear()
							binding.showingResult.text = "Showing Results"
							searchCategoryAdapter!!.updateData(mainList!!, isMore)
							searchCategoryAdapter!!.notifyDataSetChanged()


						}



						if (search_value!!.isNotEmpty()) {
							binding.rvSearchResult!!.visibility=View.VISIBLE
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
							binding.rvSearchResult!!.visibility=View.GONE

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

						textclear=false

						Log.d("search_value___", search_value.toString())




				}

//				else if () {
//
//					textchane = true
//
//					search_value = s.toString()
//					//binding.editTxtAct.setTextColor(resources.getColor(R.color.black))
//					//  recentSearch!!.add(search_value!!)
//					//   Log.d("dsscountfff", "$start$count")
//
//					if (count == 0) {
//						binding.editTxtAct.setTextColor(resources.getColor(R.color.colorTextGray))
//					} else {
//						binding.editTxtAct.setTextColor(resources.getColor(R.color.black))
//					}
//					if (count < 1) {
//						page_count = 0
//						mainList!!.clear()
//						binding.showingResult.text = "Showing Results"
//						searchCategoryAdapter!!.updateData(mainList!!, isMore)
//						searchCategoryAdapter!!.notifyDataSetChanged()
//
//
//					}
//
//
//
//					if (search_value!!.isNotEmpty()) {
//						binding.rvSearchResult!!.visibility = View.VISIBLE
//						if (sessionTwiclo!!.isLoggedIn) {
//							viewModel!!.keywordBasedSearchSuggestionsApi(
//								sessionTwiclo!!.loggedInUserDetail.accountId,
//								sessionTwiclo!!.loggedInUserDetail.accessToken,
//								search_value!!,
//								sessionTwiclo!!.userLat,
//								sessionTwiclo!!.userLng,
//								page_count.toString(), service_id!!
//							)
//						} else {
//							if (sessionTwiclo!!.userLat.isNotEmpty()) {
//								page_count = 0
//								mainList!!.clear()
//								binding.showingResult.text = "Showing Results"
////                            searchCategoryAdapter!!.updateData(mainList!!, isMore)
////                            searchCategoryAdapter!!.notifyDataSetChanged()
////
//								viewModel!!.keywordBasedSearchSuggestionsApi(
//									"",
//									"",
//									search_value!!,
//									sessionTwiclo!!.userLat,
//									sessionTwiclo!!.userLng,
//									page_count.toString(), service_id!!
//								)
//							}
//						}
//					} else {
//						binding.rvSearchResult!!.visibility = View.GONE
//
//						if (sessionTwiclo!!.isLoggedIn) {
//							viewModel!!.keywordBasedSearchSuggestionsApi(
//								sessionTwiclo!!.loggedInUserDetail.accountId,
//								sessionTwiclo!!.loggedInUserDetail.accessToken,
//								search_value!!,
//								sessionTwiclo!!.userLat,
//								sessionTwiclo!!.userLng,
//								page_count.toString(), service_id!!
//							)
//						} else {
//							if (sessionTwiclo!!.userLat.isNotEmpty()) {
//								page_count = 0
//								mainList!!.clear()
//								binding.showingResult.text = "Showing Results"
////                            searchCategoryAdapter!!.updateData(mainList!!, isMore)
////                            searchCategoryAdapter!!.notifyDataSetChanged()
////
//								viewModel!!.keywordBasedSearchSuggestionsApi(
//									"",
//									"",
//									search_value!!,
//									sessionTwiclo!!.userLat,
//									sessionTwiclo!!.userLng,
//									page_count.toString(), service_id!!
//								)
//							}
//
//						}
//					}
//
//					Log.d("search_value___", search_value.toString())
//				}

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
				binding.editTxtAct.setTextColor(resources.getColor(R.color.colorTextGray))
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
	) {
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