package com.fidoo.user.dailyneed.adapter.pageradapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import com.fidoo.user.R
import com.fidoo.user.dailyneed.adapter.prdadapter.SubcategoryByProductAdapter
import com.fidoo.user.dailyneed.model.prdmodel.Data
import com.fidoo.user.dailyneed.onclicklistener.ItemOnCatClickListener
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import com.smarteist.autoimageslider.SliderViewAdapter
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

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {}


    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
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
        viewHolder.itemView.productRecyclerviewId.isNestedScrollingEnabled=false

        try {
            viewHolder.itemView.nestedY.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                onScrollMeasurement.nestedScrollMeasurement(
                    scrollX,
                    scrollY,
                    oldScrollX,
                    oldScrollY
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCount(): Int {
        return dataList.size
    }

    interface OnScrollMeasurement {
        fun nestedScrollMeasurement(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }


}