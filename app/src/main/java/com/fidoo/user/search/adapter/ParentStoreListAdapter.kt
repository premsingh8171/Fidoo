package com.fidoo.user.search.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.data.model.SearchModel
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.search.model.ProductsList
import com.fidoo.user.search.model.Store
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.store_header_item_layout.view.*

class ParentStoreListAdapter(
        var context:Context,
        var list: ArrayList<SearchModel.Store>,
        private val adapterClick: AdapterClick,
        private var storeID : String,
        var adapterAddRemoveClick: AdapterAddRemoveClick,
        var adapterCartAddRemoveClick: AdapterCartAddRemoveClick
): RecyclerView.Adapter<ParentStoreListAdapter.ViewHolder>()  {
    private lateinit var childStoreListProductsAdapter: ChildStoreListProductsAdapter
    var customIdsList: ArrayList<SearchModel.ProductList>? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.store_header_item_layout, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.store_item_tv.text=list[position].storeName
        holder.itemView.tv_distance_ofStore.text="distance 5km"

         customIdsList= list[position]?.list as ArrayList<SearchModel.ProductList>?

        Log.d("sfddffdd", customIdsList?.get(position)?.productName.toString())
        childStoreListProductsAdapter = ChildStoreListProductsAdapter(context, customIdsList!!,object: AdapterClick{
            override fun onItemClick(productId: String?, type: String?, count: String?, offerPrice: String?, customize_count: Int?, productType: String?, cart_id: String?) {
                adapterClick.onItemClick(productId,type,count,offerPrice,customize_count,productType,cart_id)
            }
        },list[position].storeId,object : AdapterAddRemoveClick{
            override fun onItemAddRemoveClick(productId: String?, count: String?, type: String?, price: String?, storeId: String?, cartId: String?, position: Int) {
                adapterAddRemoveClick.onItemAddRemoveClick(productId,count,type,price,storeId,cartId,position)
            }

            override fun clearCart() {
                adapterAddRemoveClick.clearCart()
            }
        },object: AdapterCartAddRemoveClick{
            override fun onAddItemClick(productId: String?, items: String?, offerPrice: String?, customizeid: String?, prodcustCustomizeId: String?, cart_id: String?) {
                adapterCartAddRemoveClick.onAddItemClick(productId,items,offerPrice,customizeid,prodcustCustomizeId,cart_id)
            }

            override fun onRemoveItemClick(productId: String?, quantity: String?, customizeid: String?, prodcustCustomizeId: String?, cart_id: String?) {
                adapterCartAddRemoveClick.onRemoveItemClick(productId,quantity,customizeid,prodcustCustomizeId,cart_id)
            }

        })
        holder.itemView.search_products_rv?.adapter = childStoreListProductsAdapter

    }

    override fun getItemCount(): Int {
        return list.size
    }

//    fun setUpdate(listData_: ArrayList<SearchModel.Store>) {
//        list = ArrayList<SearchModel.Store>()
//        list.addAll(listData_)
//        notifyDataSetChanged()
//    }
}