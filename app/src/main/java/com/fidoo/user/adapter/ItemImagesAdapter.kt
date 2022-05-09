package com.fidoo.user.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterImageClick
import kotlinx.android.synthetic.main.item_images_adapter.view.*

class ItemImagesAdapter(val con: Context,val images: MutableList<String>,val adapterImageClick: AdapterImageClick
) :
        RecyclerView.Adapter<ItemImagesAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_images_adapter, parent, false)
    )
    override fun getItemCount() = images.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        Glide.with(con)
                .load(images.get(position))
                .fitCenter()
                .into(holder.imageView)
        holder.imageView.setOnClickListener {
            adapterImageClick.onSelectedImageClick(position)
            // con.startActivity(Intent(con,SingleProductActivity::class.java))
        }
    }
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView=view.imageView
    }
}