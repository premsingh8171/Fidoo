package com.fidoo.user.grocerynewui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fidoo.user.R
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Subcategory
import com.fidoo.user.grocerynewui.onclicklistener.ItemViewOnClickListener
import kotlinx.android.synthetic.main.activity_search_item.*
import kotlinx.android.synthetic.main.grocery_sub_cat_product_layout.view.*


class GrocerySubCatToPrdAdapter(
    var context: Context,
    var list: ArrayList<Subcategory>,
    var itemViewOnClickListener: ItemViewOnClickListener, var storeID: String,
    var width: Int
) : RecyclerView.Adapter<GrocerySubCatToPrdAdapter.ViewHolder>() {

    var index: Int? = 0
    var updateChildList = 0
    private lateinit var groceryItemAdapter: GroceryItemAdapter2

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.grocery_sub_cat_product_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.itemView.grocery_subHeader.text = list.get(position)?.subcategory_name.toString()

        holder.itemView.viewAllItem.setOnClickListener {
            itemViewOnClickListener.onClickViewAll(position,list[position],position,storeID,"0","viewAll")
        }

        if (list[position].product.size.toString().equals("0")){
            holder.itemView.headerSubcatview.visibility=View.GONE
        }else {
            holder.itemView.headerSubcatview.visibility = View.VISIBLE


            groceryItemAdapter = GroceryItemAdapter2(
                context,
                list[position].product as ArrayList<Product>,
                object : ItemViewOnClickListener {
                    override fun addtoCart(
                        main_position: Int,
                        productModel: Product,
                        pos: Int,
                        store_id: String,
                        item_count: String, type: String
                    ) {
                        if (type.equals("add")) {
                            itemViewOnClickListener.addtoCart(
                                position,
                                productModel,
                                pos,
                                store_id,
                                item_count, type
                            )
                        } else {
                            itemViewOnClickListener.addtoCart(
                                position,
                                productModel,
                                pos,
                                store_id,
                                item_count, type
                            )
                        }
                        list[position].product[pos].cart_quantity = item_count.toInt()
                        groceryItemAdapter.notifyDataSetChanged()
                    }

                    override fun plusItemCart(
                        main_position: Int,
                        productModel: Product,
                        pos: Int,
                        store_id: String,
                        item_count: String, type: String
                    ) {
                        itemViewOnClickListener.plusItemCart(
                            position,
                            productModel,
                            pos,
                            store_id,
                            item_count, type
                        )
                        list[position].product[pos].cart_quantity = item_count.toInt()
                        groceryItemAdapter.notifyDataSetChanged()
                    }

                    override fun minusItemCart(
                        main_position: Int,
                        productModel: Product,
                        pos: Int,
                        store_id: String,
                        item_count: String, type: String
                    ) {
                        itemViewOnClickListener.minusItemCart(
                            position,
                            productModel,
                            pos,
                            store_id,
                            item_count, type
                        )
                        list[position].product[pos].cart_quantity = item_count.toInt()
                        groceryItemAdapter.notifyDataSetChanged()
                    }

                    override fun onClickViewAll(
                        main_position: Int,
                        productModel: Subcategory,
                        pos: Int,
                        store_id: String,
                        item_count: String,
                        type: String
                    ) {

                    }
                }, storeID, width
            )

            try {
                if (3 < list[position].product.size) {
                    var layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.HORIZONTAL)
                    holder.itemView?.grocery_product_rv?.setLayoutManager(layoutManager)
                    holder.itemView?.grocery_product_rv?.adapter = groceryItemAdapter
                } else {
                    var layoutManager = StaggeredGridLayoutManager(1, GridLayoutManager.HORIZONTAL)
                    holder.itemView?.grocery_product_rv?.setLayoutManager(layoutManager)
                    holder.itemView?.grocery_product_rv?.adapter = groceryItemAdapter
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateData(list_: ArrayList<Subcategory>, update_data: Int) {
        list = java.util.ArrayList<Subcategory>()
        list.addAll(list_)
        updateChildList=update_data
        notifyDataSetChanged()
    }
}