package com.fidoo.user.restaurants.model

import androidx.recyclerview.widget.DiffUtil
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity

class vegDiffUtil(private val oldlist: ArrayList<StoreItemProductsEntity>, private val newlist: ArrayList<StoreItemProductsEntity>)

    : DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldlist.size
    }

    override fun getNewListSize(): Int {
        return newlist.size
    }

    /**
     * Checks if the items are same based on a key value i.e here considering id is unique
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition].id == newlist[newItemPosition].id
    }

    /**
     * This is called if the areItemsTheSame returns true and checks if other contents are the same
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition] == oldlist[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }


}