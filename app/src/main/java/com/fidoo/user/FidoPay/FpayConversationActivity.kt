package com.fidoo.user.FidoPay

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.FidoPay.adapters.FaceToFaceConversationAdapter
import com.fidoo.user.FidoPay.models.ConversationModel
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_change_address.*
import kotlinx.android.synthetic.main.fpay_conversation_activity.*
import java.util.ArrayList

class FpayConversationActivity : BaseActivity(){

    var conversationList: ArrayList<ConversationModel>?=null
    var updatedList: ArrayList<ConversationModel>?=null
    var msg_value:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.fpay_conversation_activity)
        conversationList=ArrayList()
        updatedList=ArrayList()
        conversation_rv?.layoutManager = LinearLayoutManager(this)

        for (i in 1..2) {
                conversationList!!.add(ConversationModel("50000","message from sender","20000","message from receiver"))

        }

        conversation_rv?.setHasFixedSize(true)
        var adapter = FaceToFaceConversationAdapter(this, conversationList!!)
        conversation_rv?.adapter = adapter
        updatedList=  conversationList
        Log.d("updatedList_", updatedList!!.size.toString())
        message_et?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                msg_value=s.toString()


            }
        })
        sendmsg_img.setOnClickListener(View.OnClickListener {

            msg_value?.let { it1 -> adapter.updateData(updatedList!!, it1) }
            adapter.notifyDataSetChanged()
            Log.d("updatedListnew__", updatedList!!.size.toString())
        })
    }


}