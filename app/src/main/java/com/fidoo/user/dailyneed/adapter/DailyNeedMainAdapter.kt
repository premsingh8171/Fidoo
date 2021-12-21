package com.fidoo.user.dailyneed.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.dailyneed.model.*
import com.fidoo.user.dailyneed.onclicklistener.ItemOnClickListener
import com.fidoo.user.dashboard.adapter.SliderAdapterExample
import com.fidoo.user.data.SliderItem
import kotlinx.android.synthetic.main.item_main_daily_need_ui.view.*
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class DailyNeedMainAdapter(
    val con: Context,
    var dataList: ArrayList<Data>,
    var itemClick: ItemOnClickListener,
    var itemSubCatClick: ItemClick,
    var width: Int
) :
    RecyclerView.Adapter<DailyNeedMainAdapter.UserViewHolder>() {
    var index = 0
    var catIconWidth = 0
    var catIconWidth2 = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 8000 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 7000 // time in milliseconds between successive task executions.
    var currentPage = 0
    var height = 0

    private var layoutManger: GridLayoutManager? = null
    var browseDailyEssentialAdapter: BrowseDailyEssentialAdapter? = null
    var breakfastCerealsAdapter: BreakfastCerealsAdapter? = null
    var healthWellnessAdapter: HealthWellnessAdapter? = null
    var householdCleanersAdapter: HouseholdCleanersAdapter? = null
    var readyToCookAdapter: ReadyToCookAdapter? = null
    var readyToCook2Adapter: ReadyToCook2Adapter? = null
    var iceCreamListAdapter: IceCreamListAdapter? = null
    var iceCreamList2Adapter: IceCreamList2Adapter? = null
    var brandAdapter: BrandAdapter? = null
    var hotspotZoneAdapter: HotspotZoneAdapter? = null

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_main_daily_need_ui, parent, false)
    )

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: UserViewHolder, @SuppressLint("RecyclerView") position: Int) {

        //Browse daily essentials view
        try {
            if (position == 0) {
                holder.itemView.rv_list.visibility = View.GONE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                holder.itemView.dailyNeed2_rv.visibility = View.GONE
                holder.itemView.banner_with_marque_catll.visibility = View.GONE
                holder.itemView.dividerOfCatView.visibility = View.GONE
                holder.itemView.headerTxtViewAll.visibility = View.GONE
                holder.itemView.iceCream_subCat_rv.visibility = View.VISIBLE
                holder.itemView.headerTxt.text = dataList[position].category_heading
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.black))
                holder.itemView.header_bgLL.setBackgroundResource(R.color.lightGray)
                catIconWidth = (width - 100) / 2
                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    width,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 10, 25, 25)
                holder.itemView.iceCream_subCat_rv.layoutParams = params

                try {
                    if (dataList[position].categories.isNotEmpty()) {
                        browseDailyEssentialAdapter =
                            BrowseDailyEssentialAdapter(
                                con, dataList[position].categories as ArrayList,
                                object : BrowseDailyEssentialAdapter.ItemClickShop {
                                    override fun onItemClick(pos: Int, model: Category) {
                                        itemClick.onClickCatView(0, pos, model)

                                    }
                                }, catIconWidth
                            )
                        holder.itemView.iceCream_subCat_rv?.adapter = browseDailyEssentialAdapter
                        layoutManger = GridLayoutManager(con, 2)
                        layoutManger!!.orientation = LinearLayoutManager.VERTICAL
                        holder.itemView.iceCream_subCat_rv?.layoutManager = layoutManger
                    } else {

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //breakfast cereals view
        try {
            if (position == 1) {
                holder.itemView.rv_list.visibility = View.VISIBLE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                holder.itemView.headerTxtViewAll.visibility = View.VISIBLE
                holder.itemView.dailyNeed2_rv.visibility = View.GONE
                holder.itemView.iceCream_subCat_rv.visibility = View.GONE
                holder.itemView.banner_with_marque_catll.visibility = View.GONE
                holder.itemView.dividerOfCatView.visibility = View.GONE
                holder.itemView.headerTxt.text = dataList[position].heading
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.black))
                holder.itemView.header_bgLL.setBackgroundResource(R.color.lightGray)

                catIconWidth = (width - 100) / 4

                try {
                    if (dataList[position].products.isNotEmpty()) {
                        breakfastCerealsAdapter =
                            BreakfastCerealsAdapter(
                                con, dataList[position].products as ArrayList,
                                object : ItemOnClickListener {
                                    override fun addtoCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.addtoCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        breakfastCerealsAdapter?.notifyDataSetChanged()
                                    }

                                    override fun minusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.minusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        breakfastCerealsAdapter?.notifyDataSetChanged()
                                    }

                                    override fun onClickCatView(
                                        main_position: Int,
                                        pos: Int,
                                        model: Category
                                    ) {
                                    }

                                    override fun onClickViewAll(main_position: Int, model: Data) {}

                                    override fun plusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.plusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                    }
                                }, catIconWidth
                            )
                        holder.itemView.dailyNeed_Cat_rv?.adapter = breakfastCerealsAdapter
                        layoutManger = GridLayoutManager(con, 1)
                        layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                        holder.itemView.dailyNeed_Cat_rv?.layoutManager = layoutManger
                    } else {

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }



            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //HandWash $ sanitizer view
        try {
            if (position == 2) {
                holder.itemView.rv_list.visibility = View.VISIBLE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                holder.itemView.headerTxtViewAll.visibility = View.VISIBLE
                holder.itemView.dailyNeed2_rv.visibility = View.GONE
                holder.itemView.iceCream_subCat_rv.visibility = View.GONE
                holder.itemView.banner_with_marque_catll.visibility = View.GONE
                holder.itemView.dividerOfCatView.visibility = View.GONE
                holder.itemView.headerTxt.text = dataList[position].subcategory_two
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.black))
                holder.itemView.header_bgLL.setBackgroundResource(R.color.lightGray)
                catIconWidth = (width - 100) / 3

                try {
                    if (dataList[position].products.isNotEmpty()) {

                        healthWellnessAdapter =
                            HealthWellnessAdapter(
                                con, dataList[position].products as ArrayList,
                                object : ItemOnClickListener {
                                    override fun addtoCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.addtoCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        healthWellnessAdapter?.notifyDataSetChanged()
                                    }

                                    override fun plusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.plusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        healthWellnessAdapter?.notifyDataSetChanged()
                                    }

                                    override fun minusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.minusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        healthWellnessAdapter?.notifyDataSetChanged()
                                    }

                                    override fun onClickCatView(
                                        main_position: Int,
                                        pos: Int,
                                        model: Category
                                    ) {

                                    }

                                    override fun onClickViewAll(main_position: Int, model: Data) {

                                    }


                                }, catIconWidth
                            )
                        holder.itemView.dailyNeed_Cat_rv?.adapter = healthWellnessAdapter
                        layoutManger = GridLayoutManager(con, 1)
                        layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                        holder.itemView.dailyNeed_Cat_rv?.layoutManager = layoutManger
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Household Cleaners view
        try {
            if (position == 3) {
                var sliderItem = SliderItem()

                holder.itemView.rv_list.visibility = View.GONE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.GONE
                holder.itemView.dailyNeed2_rv.visibility = View.GONE
                holder.itemView.iceCream_subCat_rv.visibility = View.GONE
                holder.itemView.banner_with_marque_catll.visibility = View.VISIBLE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.headerTxtViewAll.visibility = View.VISIBLE
                holder.itemView.dividerOfCatView.visibility = View.VISIBLE
                holder.itemView.headerTxt.text = dataList[position].subcategory_three
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.black))
                holder.itemView.header_bgLL.setBackgroundResource(R.color.lightGray)
                catIconWidth = (width - 100) / 4

                try {
                    //start banner code
                    if (dataList[position].banners!!.isNotEmpty()) {
                        height = Math.round(width * 0.49).toInt()
                        holder.itemView.house_n_essentials_banner!!.layoutParams =
                            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                        holder.itemView.house_n_essentials_banner!!.setClipToPadding(false)

                        val sliderItemList: ArrayList<SliderItem> = ArrayList()

                        for (i in 0 until dataList[position].banners!!.size) {
                            sliderItem = SliderItem()
                            sliderItem.imageUrl = dataList[position].banners!!.get(i)
                            sliderItemList.add(sliderItem)
                        }

                        val adapterr = SliderAdapterExample(con){
                            itemClick.onClickViewAll(position, dataList[position])
                        }

                        adapterr.renewItems(sliderItemList)
                        holder.itemView.house_n_essentials_banner.adapter = adapterr

                        val handler = Handler()
                        val Update = Runnable {
                            if (currentPage == sliderItemList.size) {
                                currentPage = 0
                            } else {
                                currentPage++
                            }

                            holder.itemView.house_n_essentials_banner.setCurrentItem(currentPage, true)

                            holder.itemView.house_n_essentials_banner

                        }

                        timer = Timer()
                        timer!!.schedule(object : TimerTask() {
                            override fun run() {
                                handler.post(Update)
                            }
                        }, DELAY_MS, PERIOD_MS)

                    } else {
                        holder.itemView.house_n_essentials_banner.visibility = View.GONE
                    }

                    //end banner code
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    if (dataList[position].products.isNotEmpty()) {

                        householdCleanersAdapter =
                            HouseholdCleanersAdapter(
                                con, dataList[position].products as ArrayList,
                                object : ItemOnClickListener {
                                    override fun addtoCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.addtoCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        householdCleanersAdapter?.notifyDataSetChanged()
                                    }

                                    override fun minusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.minusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        householdCleanersAdapter?.notifyDataSetChanged()
                                    }

                                    override fun plusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.plusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        householdCleanersAdapter?.notifyDataSetChanged()
                                    }

                                    override fun onClickCatView(
                                        main_position: Int,
                                        pos: Int,
                                        model: Category
                                    ) {

                                    }

                                    override fun onClickViewAll(main_position: Int, model: Data) {

                                    }
                                }, catIconWidth
                            )
                        holder.itemView.house_n_essentials_rv?.adapter = householdCleanersAdapter
                        layoutManger = GridLayoutManager(con, 1)
                        layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                        holder.itemView.house_n_essentials_rv?.layoutManager = layoutManger
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Ready to cook view
        try {
            if (position == 4) {
                holder.itemView.rv_list.visibility = View.VISIBLE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                holder.itemView.headerTxtViewAll.visibility = View.VISIBLE
                holder.itemView.dailyNeed2_rv.visibility = View.VISIBLE
                holder.itemView.banner_with_marque_catll.visibility = View.GONE
                holder.itemView.dividerOfCatView.visibility = View.GONE
                holder.itemView.iceCream_subCat_rv.visibility = View.GONE
                holder.itemView.headerTxt.text = dataList[position].category_one
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.black))
                holder.itemView.header_bgLL.setBackgroundResource(R.color.lightGray)
                catIconWidth = (width - 100) / 3
                catIconWidth2 = (width - 110) / 2


                try {
                    if (dataList[position].products.isNotEmpty()) {

                        readyToCookAdapter =
                            ReadyToCookAdapter(
                                con, dataList[position].products as ArrayList,
                                object : ItemOnClickListener {
                                    override fun addtoCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.addtoCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        readyToCookAdapter?.notifyDataSetChanged()
                                    }

                                    override fun minusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.minusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        readyToCookAdapter?.notifyDataSetChanged()
                                    }

                                    override fun plusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.plusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        readyToCookAdapter?.notifyDataSetChanged()
                                    }

                                    override fun onClickCatView(
                                        main_position: Int,
                                        pos: Int,
                                        model: Category
                                    ) {
                                    }

                                    override fun onClickViewAll(main_position: Int, model: Data) {}

                                }, catIconWidth
                            )
                        holder.itemView.dailyNeed_Cat_rv?.adapter = readyToCookAdapter
                        layoutManger = GridLayoutManager(con, 1)
                        layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                        holder.itemView.dailyNeed_Cat_rv?.layoutManager = layoutManger
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    if (dataList[position].subcategories.isNotEmpty()) {

                        readyToCook2Adapter =
                            ReadyToCook2Adapter(
                                con, dataList[position].subcategories as ArrayList,
                                object : ReadyToCook2Adapter.ItemClickShop {
                                    override fun onItemClick(pos: Int, model: Subcategory) {
                                        itemSubCatClick.onItemClick(
                                            holder.adapterPosition,
                                            pos,
                                            dataList[position].category_id,
                                            model
                                        )
                                    }
                                }, catIconWidth2
                            )
                        holder.itemView.dailyNeed2_rv?.adapter = readyToCook2Adapter
                        layoutManger = GridLayoutManager(con, 1)
                        layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                        holder.itemView.dailyNeed2_rv?.layoutManager = layoutManger
                        holder.itemView.dailyNeed2_rv?.isNestedScrollingEnabled = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Ice-Cream & chocolate view
        try {
            if (position == 5) {
                holder.itemView.rv_list.visibility = View.VISIBLE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                holder.itemView.headerTxtViewAll.visibility = View.VISIBLE
                holder.itemView.dailyNeed2_rv.visibility = View.VISIBLE
                holder.itemView.iceCream_subCat_rv.visibility = View.VISIBLE
                holder.itemView.banner_with_marque_catll.visibility = View.GONE
                holder.itemView.dividerOfCatView.visibility = View.GONE
                holder.itemView.headerTxt.text = dataList[position].category_two
                holder.itemView.header_bgLL.setBackgroundResource(R.drawable.gradient_blue_color)
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.white))
                holder.itemView.headerTxtViewAll.setTextColor(
                    con.getResources().getColor(R.color.white)
                )
                catIconWidth = (width - 100) / 4
                catIconWidth2 = (width - 100) / 3

                Log.d("", dataList[position].category_two)
                if (dataList[position].category_two.isNotEmpty()) {
                    holder.itemView.rv_list.visibility = View.VISIBLE
                    holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                    holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                    holder.itemView.headerTxtViewAll.visibility = View.VISIBLE
                    holder.itemView.dailyNeed2_rv.visibility = View.VISIBLE
                    holder.itemView.iceCream_subCat_rv.visibility = View.VISIBLE
                } else {
                    holder.itemView.rv_list.visibility = View.GONE
                    holder.itemView.mainHeaderDailyneddll.visibility = View.GONE
                    holder.itemView.dailyNeed_Cat_rv.visibility = View.GONE
                    holder.itemView.headerTxtViewAll.visibility = View.GONE
                    holder.itemView.dailyNeed2_rv.visibility = View.GONE
                    holder.itemView.iceCream_subCat_rv.visibility = View.GONE
                }

                try {
                    if (dataList[position].subcategories.isNotEmpty()) {

                        iceCreamListAdapter =
                            IceCreamListAdapter(
                                con, dataList[position].subcategories as ArrayList,
                                object : IceCreamListAdapter.ItemClickShop {
                                    override fun onItemClick(pos: Int, model: Subcategory) {
                                        itemSubCatClick.onItemClick(
                                            holder.adapterPosition,
                                            pos,
                                            dataList[position].category_id,
                                            model
                                        )
                                    }
                                }, catIconWidth
                            )
                        holder.itemView.iceCream_subCat_rv?.adapter = iceCreamListAdapter
                        layoutManger = GridLayoutManager(con, 1)
                        layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                        holder.itemView.iceCream_subCat_rv?.layoutManager = layoutManger


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    if (dataList[position].products.isNotEmpty()) {

                        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 70, 0, 40)
                        holder.itemView.dailyNeed_Cat_rv.layoutParams = params

                        iceCreamList2Adapter =
                            IceCreamList2Adapter(
                                con, dataList[position].products as ArrayList,
                                object : ItemOnClickListener {
                                    override fun addtoCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.addtoCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        iceCreamList2Adapter?.notifyDataSetChanged()
                                    }

                                    override fun minusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.minusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        iceCreamList2Adapter?.notifyDataSetChanged()
                                    }

                                    override fun plusItemCart(
                                        main_position: Int,
                                        productModel: Product,
                                        pos: Int,
                                        store_id: String,
                                        item_count: String,
                                        type: String
                                    ) {
                                        itemClick.plusItemCart(
                                            holder.adapterPosition,
                                            productModel,
                                            pos,
                                            store_id,
                                            item_count,
                                            type
                                        )
                                        dataList[position].products[pos].cart_quantity = item_count
                                        iceCreamList2Adapter?.notifyDataSetChanged()
                                    }

                                    override fun onClickCatView(
                                        main_position: Int,
                                        pos: Int,
                                        model: Category
                                    ) {
                                    }

                                    override fun onClickViewAll(main_position: Int, model: Data) {}

                                }, catIconWidth
                            )
                        holder.itemView.dailyNeed_Cat_rv?.adapter = iceCreamList2Adapter
                        layoutManger = GridLayoutManager(con, 1)
                        layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                        holder.itemView.dailyNeed_Cat_rv?.layoutManager = layoutManger
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //brand
        try {
            if (position == 6) {
                holder.itemView.rv_list.visibility = View.VISIBLE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                holder.itemView.dailyNeed2_rv.visibility = View.VISIBLE
                holder.itemView.banner_with_marque_catll.visibility = View.GONE
                holder.itemView.dividerOfCatView.visibility = View.GONE
                holder.itemView.headerTxt.text = dataList[position].brand_heading
                holder.itemView.header_bgLL.setBackgroundResource(R.drawable.gradient_blue_color)
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.white))
                holder.itemView.headerTxtViewAll.setTextColor(
                    con.getResources().getColor(R.color.white)
                )

                catIconWidth = (width - 100) / 4
                catIconWidth2 = (width - 100) / 3

                Log.d("", dataList[position].brand_heading)
                if (dataList[position].brand_heading.isNotEmpty()) {
                    holder.itemView.rv_list.visibility = View.VISIBLE
                    holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                    holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                    holder.itemView.headerTxtViewAll.visibility = View.GONE
                    holder.itemView.dailyNeed2_rv.visibility = View.VISIBLE
                    holder.itemView.iceCream_subCat_rv.visibility = View.VISIBLE
                } else {
                    holder.itemView.rv_list.visibility = View.GONE
                    holder.itemView.mainHeaderDailyneddll.visibility = View.GONE
                    holder.itemView.dailyNeed_Cat_rv.visibility = View.GONE
                    holder.itemView.headerTxtViewAll.visibility = View.GONE
                    holder.itemView.dailyNeed2_rv.visibility = View.GONE
                    holder.itemView.iceCream_subCat_rv.visibility = View.GONE
                }

                try {
                    brandAdapter = BrandAdapter(
                        con,
                        dataList[position].brands as ArrayList,
                        object : BrandAdapter.ItemClickBrand {
                            override fun onItemClick(pos: Int, model: Brand) {
                            }
                        },
                        catIconWidth2
                    )
                    holder.itemView.iceCream_subCat_rv?.adapter = brandAdapter
                    layoutManger = GridLayoutManager(con, 3)
                    layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                    holder.itemView.iceCream_subCat_rv?.layoutManager = layoutManger

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Hotspot Zone view
        try {
            if (position == 7) {
                holder.itemView.dividerOfCatView.visibility = View.VISIBLE
                holder.itemView.rv_list.visibility = View.GONE
                holder.itemView.mainHeaderDailyneddll.visibility = View.VISIBLE
                holder.itemView.dailyNeed_Cat_rv.visibility = View.VISIBLE
                holder.itemView.headerTxtViewAll.visibility = View.VISIBLE
                holder.itemView.dailyNeed2_rv.visibility = View.GONE
                holder.itemView.iceCream_subCat_rv.visibility = View.VISIBLE
                holder.itemView.banner_with_marque_catll.visibility = View.GONE
                holder.itemView.headerTxtViewAll.visibility = View.GONE
                holder.itemView.headerTxt.text = dataList[position].heading
                holder.itemView.header_bgLL.setBackgroundResource(R.color.lightGray)
                catIconWidth = (width - 120) / 3
                holder.itemView.headerTxt.setTextColor(con.getResources().getColor(R.color.black))

                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, width)
                params.setMargins(20, 15, 15, 25)
                holder.itemView.iceCream_subCat_rv.layoutParams = params

                try {
                    if (dataList[position].subcategories.isNotEmpty()) {

                        hotspotZoneAdapter =
                            HotspotZoneAdapter(
                                con, dataList[position].subcategories as ArrayList,
                                object : HotspotZoneAdapter.ItemClickShop {
                                    override fun onItemClick(pos: Int, model: Subcategory) {
                                        itemSubCatClick.onItemClick(
                                            holder.adapterPosition,
                                            pos,
                                            dataList[position].subcategories[pos].category_id,
                                            model
                                        )

                                    }
                                }, catIconWidth
                            )
                        holder.itemView.iceCream_subCat_rv?.adapter = hotspotZoneAdapter
                        layoutManger = GridLayoutManager(con, 3)
                        layoutManger!!.orientation = LinearLayoutManager.VERTICAL
                        holder.itemView.iceCream_subCat_rv?.layoutManager = layoutManger
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            if (dataList.size-1==position){
                holder.itemView.itemHotspotSpaceBelow_ll.visibility=View.VISIBLE
            }else{
                holder.itemView.itemHotspotSpaceBelow_ll.visibility=View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.itemView.headerTxtViewAll.setOnClickListener {
            itemClick.onClickViewAll(position, dataList[position])
        }

    }

    fun updateData(list: ArrayList<Data>) {
        dataList = java.util.ArrayList<Data>()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    interface ItemClick {
        fun onItemClick(main_position: Int,pos: Int,category_id: String, model: Subcategory)
    }
}