package com.fidoo.user.sendpackages.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.cartview.roomdb.entity.PrescriptionViewEntity
import com.fidoo.user.sendpackages.roomdb.entity.SendPackagesItemImage
import kotlinx.android.synthetic.main.item_prescription_view.view.*

class SendPackagesImgAdapter(
    var con: Context, var arraylist: ArrayList<SendPackagesItemImage>,
    var onClickPrescription: OnClickSendPackages
) : RecyclerView.Adapter<SendPackagesImgAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_prescription_view, parent, false)
    )

    override fun getItemCount() = arraylist.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val index = arraylist[position]

        try {
            if (index.package_url.equals("null")) {
                holder.itemView.precription_imgClear2.visibility = View.GONE
            } else {
                holder.itemView.precription_imgClear2.visibility = View.VISIBLE
            }
            Glide.with(con)
                .load(index.package_url)
                .fitCenter()
                .error(R.drawable.add_more)
                .placeholder(R.drawable.add_more)
                .into(holder.itemView.precription_img2)

            holder.itemView.precription_img2.setOnClickListener {
                onClickPrescription.uploadImage(position, arraylist[position])
            }

            holder.itemView.precription_imgClear2.setOnClickListener {
                onClickPrescription.clearImage(position, arraylist[position])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    interface OnClickSendPackages {
        fun uploadImage(position: Int, model: SendPackagesItemImage)
        fun clearImage(position: Int, model: SendPackagesItemImage)

    }

}