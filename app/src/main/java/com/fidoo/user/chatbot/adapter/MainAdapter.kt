package com.fidoo.user.chatbot.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk

import com.fidoo.user.R
import com.fidoo.user.data.model.OrderStatusResponse
import com.fidoo.user.databinding.BotitemlayoutBinding
import kotlinx.android.synthetic.main.botitemlayout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainAdapter(val context: Context, var datalist:List<String>) : RecyclerView.Adapter<ResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        val itemLayoutBinding = BotitemlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ResponseViewHolder(itemLayoutBinding)
    }

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        val model = datalist[position]
        var count = datalist.size
        holder.itemLayoutBinding.apply {
            cardbotlayout.visibility = View.GONE
            CoroutineScope(Dispatchers.Main).launch {
                delay(position * 500L)
                Glide.with(context).asGif().load(R.drawable.typing).into(loadingGifChat)
                delay(position*250L)
                val aniFade: Animation = AnimationUtils.loadAnimation(FacebookSdk.getApplicationContext(), R.anim.fade_in)
                cardbotlayout.startAnimation(aniFade)
                tvTitle.text = model
                loadingGifChat.visibility = View.GONE
                cardbotlayout.visibility = View.VISIBLE
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml( model , Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(model)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}
class ResponseViewHolder(val itemLayoutBinding: BotitemlayoutBinding) : RecyclerView.ViewHolder(itemLayoutBinding.root)