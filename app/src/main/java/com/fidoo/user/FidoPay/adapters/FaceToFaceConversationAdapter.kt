package com.fidoo.user.FidoPay.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.FidoPay.models.ConversationModel
import com.fidoo.user.R
import kotlinx.android.synthetic.main.item_receiver_layout.view.*
import kotlinx.android.synthetic.main.item_sender_layout.view.*

class FaceToFaceConversationAdapter(

    val context: Context,
    var conversationList: ArrayList<ConversationModel>

    ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val receiverItem=1
    //val receiverItem=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view:View

        if (viewType==receiverItem){
            view=LayoutInflater.from(context).inflate(R.layout.item_receiver_layout,parent,false)
            return MyviewHolder(view)
        }else{
            view=LayoutInflater.from(context).inflate(R.layout.item_sender_layout,parent,false)
            return MyviewHolderTow(view)
        }
       // return MyviewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
      //  if(position==0) {
           // return position;
        return position % 2 * 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("listisize", conversationList.size.toString())
        if (holder.itemViewType==receiverItem){
            val vaultItemHolder: MyviewHolder = holder as MyviewHolder
            vaultItemHolder.itemView.receiverAmt_tv.text=context.resources.getString(R.string.ruppee) + " "+conversationList.get(position).payReceiverAmt+" >>"
            vaultItemHolder.itemView.msgreceiver_tv.text=conversationList.get(position).msgReceiver

        }else{
            val vaultItemHolder: MyviewHolderTow = holder as MyviewHolderTow
            vaultItemHolder.itemView.paySenderAmt_tv.text=context.resources.getString(R.string.ruppee) + " "+conversationList.get(position).paySenderAmt+" >>"
            vaultItemHolder.itemView.smsSender_tv.text=conversationList.get(position).msgSender
        }

    }

    override fun getItemCount(): Int {
       return conversationList.size
    }
    class MyviewHolder(view: View) : RecyclerView.ViewHolder(view) {}
    class MyviewHolderTow(view: View) : RecyclerView.ViewHolder(view) {
    }
    fun updateData(conversationlist: ArrayList<ConversationModel>,value:String) {
        //   Log.d("updateData___", isMore1!!.toString())
        conversationList.clear();
        conversationList = ArrayList<ConversationModel>()
        conversationList.addAll(conversationlist)
        notifyDataSetChanged()
    }
}