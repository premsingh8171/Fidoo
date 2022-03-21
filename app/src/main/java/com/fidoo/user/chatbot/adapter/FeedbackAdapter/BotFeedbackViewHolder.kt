package com.fidoo.user.chatbot.adapter.FeedbackAdapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.botlogocount
import com.fidoo.user.chatbot.model.feedBackModel.FeedbackModel
import kotlinx.android.synthetic.main.botitemlayout.view.*

class BotFeedbackViewHolder (var context: Context, val view : View) : RecyclerView.ViewHolder(view) {
    fun setdata(feedbackModel:  String) {
        view.apply {
            if (botlogocount.botrefundCount >= 1) {
                tvTitle.text = feedbackModel
                //botlogo.visibility = View.GONE
                //botlogolayout.visibility = View.INVISIBLE
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(feedbackModel, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(feedbackModel)
                }

            } else {
                tvTitle.text = feedbackModel
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(feedbackModel, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(feedbackModel)
                }

            }
            botlogocount.botrefundCount++
            // count++
        }



    }

}
