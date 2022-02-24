package com.fidoo.user.dailyneed.adapter.pageradapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.dailyneed.adapter.prdadapter.SubcategoryByProductAdapter
import com.fidoo.user.dailyneed.model.prdmodel.Data
import com.fidoo.user.dailyneed.onclicklistener.ItemOnCatClickListener
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import com.fidoo.user.restaurants.activity.NewDBStoreItemsActivity
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.activity_store_items.*
import kotlinx.android.synthetic.main.item_rv.view.*


class SliderAdapterProduct(
    var con: Context,
    var dataList: MutableList<Data>,
    var itemClick: ItemOnCatClickListener,
    var onScrollMeasurement: OnScrollMeasurement,
    var width: Int
) : SliderViewAdapter<SliderAdapterProduct.SliderAdapterVH>() {
    var index = 0
    var subcategoryByProductAdapter: SubcategoryByProductAdapter? = null
    private var manager: GridLayoutManager? = null
    private var currentItems = 0
    private var totalItems: Int = 0
    private var scrollOutItems: Int = 0
    private var isScrolling = false

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {}


    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        manager = GridLayoutManager(con, 2)

        subcategoryByProductAdapter = SubcategoryByProductAdapter(
            con,
            dataList[position].products as ArrayList,
            object : ItemOnCatClickListener {
                override fun addtoCart(
                    main_position: Int,
                    productModel: com.fidoo.user.dailyneed.model.prdmodel.Product,
                    pos: Int,
                    store_id: String,
                    item_count: String,
                    type: String
                ) {
                    itemClick.addtoCart(position,productModel,pos,store_id,item_count,type)
                    dataList[position].products[pos].cart_quantity=item_count
                }

                override fun minusItemCart(
                    main_position: Int,
                    productModel: com.fidoo.user.dailyneed.model.prdmodel.Product,
                    pos: Int,
                    store_id: String,
                    item_count: String,
                    type: String
                ) {
                    itemClick.minusItemCart(position,productModel,pos,store_id,item_count,type)
                    dataList[position].products[pos].cart_quantity=item_count
                }

                override fun plusItemCart(
                    main_position: Int,
                    productModel: com.fidoo.user.dailyneed.model.prdmodel.Product,
                    pos: Int,
                    store_id: String,
                    item_count: String,
                    type: String
                ) {
                    itemClick.plusItemCart(position,productModel,pos,store_id,item_count,type)
                    dataList[position].products[pos].cart_quantity=item_count
                }

                override fun onClickViewAll(main_position: Int, model: Data) {}
            },
            width
        )
        viewHolder.itemView.productRecyclerviewId.adapter = subcategoryByProductAdapter
        //viewHolder.itemView.productRecyclerviewId.isNestedScrollingEnabled=false
        viewHolder.itemView.productRecyclerviewId.layoutManager = manager
        viewHolder.itemView.productRecyclerviewId?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                    //handleresponce = 0
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager!!.childCount
                totalItems = manager!!.itemCount
                scrollOutItems = manager!!.findFirstVisibleItemPosition()
                var firstvisibleItem = manager!!.findFirstCompletelyVisibleItemPosition()

                // Log.d("firstvisibleItem", "$currentItems---$totalItems---$scrollOutItems---$firstvisibleItem")

            }

        })

//        try {
//            viewHolder.itemView.nestedY.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//                onScrollMeasurement.nestedScrollMeasurement(
//                    scrollX,
//                    scrollY,
//                    oldScrollX,
//                    oldScrollY
//                )
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    override fun getCount(): Int {
        return dataList.size
    }

    interface OnScrollMeasurement {
        fun nestedScrollMeasurement(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }


}