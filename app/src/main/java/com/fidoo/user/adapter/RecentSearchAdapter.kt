package com.fidoo.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterImageClick
import kotlinx.android.synthetic.main.recent_search_adapter.view.*


class RecentSearchAdapter(
        val con: Context,val
        searchSuggestionsList: ArrayList<String>,val adapterImageClick: AdapterImageClick
) :
        RecyclerView.Adapter<RecentSearchAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recent_search_adapter, parent, false)
    )

    override fun getItemCount() = searchSuggestionsList.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.searchTxt.text = searchSuggestionsList.get(position)

        holder.searchTxt.setOnClickListener(View.OnClickListener {
            adapterImageClick.onSelectedImageClick(position)
        })
        /* holder.storeName.text = storeList.get(position).name
        holder.locText.text = storeList.get(position).address
        Glide.with(con)
            .load(storeList.get(position).image)
            .fitCenter()
            .into(holder.storeImg)
        holder.rightArrow.setOnClickListener(View.OnClickListener {
            con.startActivity(
                Intent(con, StoreItemsActivity::class.java).putExtra(
                    "storeId",
                    storeList.get(position).id
                ).putExtra("storeName",storeList.get(position).name)
            )
        })
        holder.mainLay.setOnClickListener(View.OnClickListener {
            con.startActivity(
                Intent(con, StoreItemsActivity::class.java).putExtra(
                    "storeId",
                    storeList.get(position).id
                ).putExtra("storeName",storeList.get(position).name)
            )
        })*/
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        /*var rightArrow = view.rightArrow
        var storeName = view.storeName
        var storeImg = view.storeImg
        var locText = view.locText*/
        var searchTxt = view.searchTxt
    }
}