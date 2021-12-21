package com.fidoo.user.newsearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.newsearch.model.Suggestion
import kotlinx.android.synthetic.main.suggestions_search_item.view.*

class SearchSuggestionsAdapter(
    var context: Context,
    var list: ArrayList<Suggestion>,
    var recentSearchItemClick: SuggestionsSearchItemClick
) : RecyclerView.Adapter<SearchSuggestionsAdapter.ViewHolder>() {
    var index: Int? = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.suggestions_search_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.suggestionsSearch_txt.text = list.get(position)?.service_name

        holder.itemView.suggestionsSearch_Ll.setOnClickListener {
            recentSearchItemClick.onItemClick(position, list.get(position))
            index = position
        }

//        if(index==position){
//            holder.itemView.recentSearch_ll.setBackgroundResource(R.drawable.rectangle_shap)
//            holder.itemView.recent_search_txt.setTextColor(Color.parseColor("#ffffff"))
//        }
//        else{
//            holder.itemView.recentSearch_ll.setBackgroundResource(R.drawable.black_rounded_empty)
//            holder.itemView.recent_search_txt.setTextColor(Color.parseColor("#818181"))
//        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface SuggestionsSearchItemClick {
        fun onItemClick(pos: Int, model: Suggestion)
    }


}