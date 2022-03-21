package com.fidoo.user.chatbot.adapter.orderStatusMsgAdapter

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
import com.fidoo.user.databinding.BotitemlayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class orderStatusAdapter(val context: Context, var msgdatalist:List<String>) : RecyclerView.Adapter<ResponseViewHolder2>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder2 {
        val itemLayoutBinding = BotitemlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ResponseViewHolder2(itemLayoutBinding)
    }

    override fun onBindViewHolder(holder: ResponseViewHolder2, position: Int) {
        val model = msgdatalist[position]
        var count = msgdatalist.size
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
        return msgdatalist.size
    }
}
class ResponseViewHolder2(val itemLayoutBinding: BotitemlayoutBinding) : RecyclerView.ViewHolder(itemLayoutBinding.root)