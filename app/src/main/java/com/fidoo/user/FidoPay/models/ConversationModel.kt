package com.fidoo.user.FidoPay.models

data class ConversationModel(
    val paySenderAmt: String,
    val msgSender: String,
    val payReceiverAmt: String,
    val msgReceiver: String
)
