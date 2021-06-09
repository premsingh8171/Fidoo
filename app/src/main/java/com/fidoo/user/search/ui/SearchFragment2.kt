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
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.fidoo.user.CartActivity
import com.fidoo.user.R
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.SearchModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.restaurants.model.CustomCheckBoxModel
import com.fidoo.user.search.adapter.ParentStoreListAdapter
import com.fidoo.user.search.adapter.RecentSearchAdapter
import com.fidoo.user.search.model.Store
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.utils.CommonUtils
import com.fidoo.user.viewmodels.SearchFragmentViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_search2.view.*


class SearchFragment2 : Fragment() , AdapterClick,
		AdapterAddRemoveClick,
		AdapterCartAddRemoveClick {
	private lateinit var parentStoreListAdapter: ParentStoreListAdapter
	private lateinit var recentSearchAdapter: RecentSearchAdapter
	val storeList: ArrayList<Store> = ArrayList()
	var viewmodel: SearchFragmentViewModel? = null
	lateinit var mProductStoreList: ArrayList<SearchModel.Store>
	lateinit var mProductsList: ArrayList<SearchModel.ProductList>
	lateinit var recentsearchArrayList: ArrayList<String>

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
	var checkvalidation: Int? = 0//for onresume handle
	lateinit var mView : View
	private var _progressDlg: ProgressDialog? = null
	lateinit var sessionTwiclo:SessionTwiclo

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mView= inflater.inflate(R.layout.fragment_search2, container, false)
		sessionTwiclo = SessionTwiclo(requireContext())
		viewmodel = ViewModelProviders.of(requireActivity()).get(SearchFragmentViewModel::class.java)
		mProductStoreList= ArrayList()
		mProductsList= ArrayList()
		recentsearchArrayList= ArrayList()
		customIdsList= ArrayList()
		customIdsListTemp= ArrayList()

		mView.total_itemTxt_fgmt.text = "Items ("+mProductsList.size.toString()+")"
		parentStoreListAdapter = ParentStoreListAdapter(
			requireContext(),mProductStoreList,this,storeID,this,this
		)

		try {
			if (!sessionTwiclo.getRecentSearchArrayList("Recent_Search").equals("")){
				recentsearchArrayList = sessionTwiclo.getRecentSearchArrayList("Recent_Search").reversed() as ArrayList<String>
				RecentSearchRv(recentsearchArrayList)
		     }
		}catch (e:Exception){
			e.printStackTrace()
		}



		mView.searchEdt_new_fgmt?.addTextChangedListener(object : TextWatcher {

			override fun afterTextChanged(s: Editable) {}

			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(
				s: CharSequence, start: Int,
				before: Int, count: Int
			) {
				search_value=s.toString()
				if (mView.searchEdt_new_fgmt?.text.toString().length >= 2) {
					if (SessionTwiclo(requireContext()).isLoggedIn) {
						//showIOSProgress()
						viewmodel?.getSearchApi(SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
							SessionTwiclo(requireContext()).loggedInUserDetail.accessToken, search_value!!)

					}
				} else {
					search_value=""
					mProductStoreList.clear()
					mProductsList.clear()
					parentStoreListAdapter.notifyDataSetChanged()
					mView.total_itemTxt_fgmt.text = "Items ("+mProductsList.size.toString()+")"

				}

			}
		})


//		//Here we have called Api of getGroceryProducts
//		viewmodel?.getSearchApi(
//				SessionTwiclo(context).loggedInUserDetail.accountId,
//				SessionTwiclo(context).loggedInUserDetail.accessToken,
//				""
//		)

		try {
			viewmodel?.getCartCountApi(
				SessionTwiclo(context).loggedInUserDetail.accountId,
				SessionTwiclo(context).loggedInUserDetail.accessToken
			)
		}catch (e:Exception){
			e.printStackTrace()
		}



		getResponce()


		mView.new_cartitemView_LLsearch_fgmt.setOnClickListener {
			if (SessionTwiclo(context).isLoggedIn) {
				startActivity( Intent(context, CartActivity::class.java).putExtra(
						"store_id", SessionTwiclo(
						context
				).storeId
				)
				)
			} else {
				showLoginDialog("Please login to proceed")

			}
		}

		return mView
	}

	private fun getResponce() {

		//Here we observe searchApi responce
		viewmodel?.searchResponse?.observe(requireActivity(),{searchResponce->
			//dismissIOSProgress()
			Log.d("ffdffddf",searchResponce.errorCode.toString()+"--"+searchResponce.error.toString())

			if (!searchResponce.error!!){


				val storeList: ArrayList<SearchModel.Store> = ArrayList()
				val productList: ArrayList<SearchModel.ProductList> = ArrayList()
				var recentArrayList: ArrayList<String> = ArrayList()

				val mModelData: SearchModel = searchResponce
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
				try {

					recentsearchArrayList.add(productList[0].categoryName.toString())
					val s: Set<String> = LinkedHashSet<String>(recentsearchArrayList)
					recentsearchArrayList.clear()
					recentsearchArrayList.addAll(0,s)
					val sortAsc = recentsearchArrayList.reversed() as java.util.ArrayList<String>

					sessionTwiclo.saveRecentSearchArrayList(sortAsc ,"Recent_Search")

					RecentSearchRv(sortAsc)

				}catch (e:Exception){
					e.printStackTrace()
				}
				mProductsList=productList
				mProductStoreList=storeList

				//mView.total_itemTxt_fgmt.text = "Items ("+mProductsList.size.toString()+")"
			try {
				parentStoreListAdapter = ParentStoreListAdapter(
					requireContext(),mProductStoreList,this,storeID,this,this
				)
			}catch (e:Exception){
				e.printStackTrace()
			}

				mView.search_rv_fgmt?.adapter=parentStoreListAdapter



			}

		})

		//cartcount responce
		viewmodel?.cartCountResponse?.observe(requireActivity(),{cartcount->
			CommonUtils.dismissIOSProgress()
			dismissIOSProgress()
			Log.d("cartCountResponse___",cartcount.toString())
			var count = cartcount.count
			var price = cartcount.price
			storeID = cartcount.store_id
			if (!cartcount.error){
				if (count!="0"){
					mView.new_itemQuantity_textsearch_fgmt.text=count
					mView.new_totalprice_txtsearch_fgmt.text= "₹"+price
					mView.new_cartitemView_LLsearch_fgmt.visibility= View.VISIBLE
				//	mView.item_lay.visibility = View.VISIBLE
					try {
						SessionTwiclo(context).storeId=cartcount.store_id
						sessionTwiclo.storeId=cartcount.store_id
					}catch (e:Exception){
						e.printStackTrace()
						sessionTwiclo.storeId=cartcount.store_id

					}
					// setStoreId
				}else{
					mView.new_cartitemView_LLsearch_fgmt.visibility= View.GONE
					mView.item_lay.visibility = View.GONE
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
						search_value!!
				)
				viewmodel?.getCartCountApi(
					sessionTwiclo.loggedInUserDetail.accountId,
					sessionTwiclo.loggedInUserDetail.accessToken
				)
			} else {
				viewmodel?.getSearchApi(
					sessionTwiclo.loggedInUserDetail.accountId,
					sessionTwiclo.loggedInUserDetail.accessToken,
						search_value!!
				)
				viewmodel?.getCartCountApi(
					sessionTwiclo.loggedInUserDetail.accountId,
					sessionTwiclo.loggedInUserDetail.accessToken
				)
			}

			//   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
		})

		//add to cart
		viewmodel?.addToCartResponse?.observe(requireActivity(), Observer { user ->
			dismissIOSProgress()
			Log.e("stores response", Gson().toJson(user))
			val mModelData: com.fidoo.user.data.model.AddToCartModel = user
			if (tempType.equals("custom")) {
			} else {
				MainActivity.tempProductList!!.clear()
				MainActivity.addCartTempList!!.clear()
			}
			try {
				viewmodel?.getCartCountApi(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken
				)

				if (SessionTwiclo(context).isLoggedIn) {
					viewmodel?.getSearchApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						search_value!!
					)
				} else {
					viewmodel?.getSearchApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						search_value!!
					)
				}

			}catch (e:Exception){
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
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						MainActivity.addCartTempList!!,
						""
					)
					viewmodel?.getCartCountApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken
					)
				} else {
					viewmodel!!.addToCartApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						MainActivity.addCartTempList!!,
						""

					)
					viewmodel?.getCartCountApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken
					)
				}
			}catch (e:Exception){
				e.printStackTrace()
			}
			//   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
		})

		//error
		viewmodel?.failureResponse?.observe(requireActivity(), Observer { user ->
			Log.e("cart_response", Gson().toJson(user))
		})

	}

	private fun RecentSearchRv(recentList: ArrayList<String>) {
		recentSearchAdapter = RecentSearchAdapter(requireContext(),recentList,object :RecentSearchAdapter.RecentSearchItemClick{
			override fun onItemClick(pos: Int,value:String) {
				search_value=value
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
		mView.recentsearch_rv_fgmt.adapter=recentSearchAdapter

	}

	private fun showLoginDialog(message: String){
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
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken
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
				customIdsList!!.clear()
				if (productId != null) {
					viewmodel?.customizeProductApi(
							SessionTwiclo(context).loggedInUserDetail.accountId,
							SessionTwiclo(context).loggedInUserDetail.accessToken, productId
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
								SessionTwiclo(context).loggedInUserDetail.accountId,
								SessionTwiclo(context).loggedInUserDetail.accessToken, productId
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
				if (SessionTwiclo(context).storeId.equals(storeID) || SessionTwiclo(context).storeId.equals("")
				) {
					SessionTwiclo(context).storeId = storeID
					Log.e("  store id", SessionTwiclo(context).storeId.toString())
					showIOSProgress()

					viewmodel!!.addToCartApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						MainActivity.addCartTempList!!,
						""
					)
					SessionTwiclo(context).storeId = storeID
				} else {
					clearCartPopup()
				}
			}catch (e:Exception){
				e.printStackTrace()
			}

		}
	}

	//for plus and minus click
	override fun onItemAddRemoveClick(productId: String?, count: String?, type: String?, price: String?, storeId: String?, cartId: String?, position: Int)
	{
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
					showIOSProgress()
					viewmodel!!.addToCartApi(
							SessionTwiclo(context).loggedInUserDetail.accountId,
							SessionTwiclo(context).loggedInUserDetail.accessToken,
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
				showIOSProgress()
				try {
					viewmodel!!.addToCartApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						MainActivity.addCartTempList!!,
						""
					)
				}catch (e:Exception){
					e.printStackTrace()
				}

			}

		} else {
			showIOSProgress()
			try {
				viewmodel?.addRemoveCartDetails(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken,
					productId!!,
					"remove",
					"0",
					"","",
					customIdsList!!
				)

			}catch (e:Exception){
				e.printStackTrace()
			}

		}
	}

	override fun clearCart() {
		viewmodel?.clearCartApi(
				SessionTwiclo(context).loggedInUserDetail.accountId,
				SessionTwiclo(context).loggedInUserDetail.accessToken
		)
	}

	//on add item first time
	override fun onAddItemClick(productId: String?, items: String?, offerPrice: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?)
	{
		tempOfferPrice = offerPrice
		//plusMinusPrice = 0.0
		tempPrice = 0.0
		tempProductId = productId

		val builder = AlertDialog.Builder(requireContext())
		builder.setTitle("Your previous customization")
		builder.setMessage(items)
		builder.setPositiveButton("I'LL CHOOSE") { _, which ->



			//tempProductId = productId
			//showIOSProgress()
			customIdsList!!.clear()
			viewmodel?.customizeProductApi(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken, productId!!
			)
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

	//customize item minus click
	override fun onRemoveItemClick(productId: String?, quantity: String?, isCustomize: String?, prodcustCustomizeId: String?, cart_id: String?) {
		try {
			if (cart_id != null) {
				showIOSProgress()
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
		}catch (e:Exception){
			e.printStackTrace()
		}

	}

	override fun onResume() {
		super.onResume()
		if(checkvalidation==0) {
			//Here we have called Api of getGroceryProducts

			if (SessionTwiclo(context).isLoggedIn){
				viewmodel?.getSearchApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						search_value!!
				)

				viewmodel?.getCartCountApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken
				)
			}else{
				viewmodel?.getSearchApi(
						"",
						"",
						search_value!!
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


}