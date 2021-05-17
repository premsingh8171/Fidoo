package com.fidoo.user.grocery.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.model.getGroceryProducts.Subcategory
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*

class GrocerySubItemAdapter(var context: Context,

                            var list:ArrayList<Subcategory>,
                            var subcategoryItemClick:SubcategoryItemClick,
var selectvalue:String?=""): RecyclerView.Adapter<GrocerySubItemAdapter.ViewHolder>() {


    var index:Int?=0
    var valueselected = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_sub_cat_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.grocery_sub_tv.text = list.get(position)?.subcategory_name.toString()

        // index=valueselected
        holder.itemView.grocery_sub_cons.setOnClickListener {
            if (SessionTwiclo(context).isLoggedIn){
                index=position
                subcategoryItemClick.onItemClick(position,list.get(position))
                notifyDataSetChanged()
            }else{
                showLoginDialog("Please login to proceed")
            }

        }



//        if(index==position){
//            holder.itemView.active_dotLL.visibility = View.VISIBLE
//        }
//        else{
//            holder.itemView.active_dotLL.visibility=View.GONE
//
//        }

        if (selectvalue!!.equals(list.get(position)?.subcategory_name.toString())){
          //  holder.itemView.active_dotLL.visibility=View.VISIBLE
            holder.itemView.active_dotLL.setBackgroundResource(R.drawable.bg_green_roundborder)
            holder.itemView.grocery_sub_tv.setTextColor(Color.parseColor("#ffffff"))

        }else{
               // holder.itemView.active_dotLL.visibility = View.GONE
            holder.itemView.active_dotLL.setBackgroundResource(R.drawable.black_full_rounded_empty)
            holder.itemView.grocery_sub_tv.setTextColor(Color.parseColor("#818181"))

        }
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

    private fun showLoginDialog(message: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { _, _ ->
            context.startActivity(Intent(context, LoginActivity::class.java))


        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}