package com.fidoo.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.data.model.Specification
import kotlinx.android.synthetic.main.row_specifications.view.*

class SpecificationsAdapter(
        val con: Context,
        val cart: ArrayList<Specification>

) :
        RecyclerView.Adapter<SpecificationsAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_specifications, parent, false)
    )

    override fun getItemCount() = cart.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.tv_specification_name.text = cart.get(position).name
        holder.tv_speci_value.text = cart.get(position).value


    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_specification_name = view.tv_specification_name
        var tv_speci_value = view.tv_speci_value

    }


}