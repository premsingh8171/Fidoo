package com.fidoo.user.grocery.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.grocery.activity.GroceryItemsActivity
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.model.getGroceryProducts.Subcategory
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_product_layout.view.*

class GrocerySubCategoryToProductAdapter(var context: Context, var list:ArrayList<Subcategory>, var subcategoryItemClick:SubcategoryItemClick): RecyclerView.Adapter<GrocerySubCategoryToProductAdapter.ViewHolder>() {

    var index:Int?=0
    var valueselected = 0
    private lateinit var groceryItemAdapter: GroceryItemAdapter2

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_sub_cat_product_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.grocery_subHeader.text = list.get(position)?.subcategory_name.toString()


        groceryItemAdapter = GroceryItemAdapter2(
            context,
            list[position].product as ArrayList<Product>,
            0, ""
        )
        holder.itemView?.grocery_product_rv?.adapter = groceryItemAdapter
        Log.d("ddsdsds_adapter", GroceryItemsActivity.itemPosition!!.toString())

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface SubcategoryItemClick {
        fun onItemClick(pos: Int,grocery:Subcategory)
    }

    fun updateReceiptsList(value: Int) {
        valueselected = value
        Log.d("valueselected____", java.lang.String.valueOf(valueselected))
        notifyDataSetChanged()
    }
}