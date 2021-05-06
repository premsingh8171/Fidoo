package com.fidoo.user

import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        chatWebView.webViewClient = WebViewClient()

        chatWebView.loadUrl("https://app.syrow.com/script/chatter-gS0Bcjk3qQye7BbQQt+TsO4uT3Ja8zBVGzaNTQRfmNY=.js")

        // this will enable the javascript settings
        chatWebView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        chatWebView.settings.setSupportZoom(true)

        chatWebView.webViewClient = MyWebViewClient()
    }


    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView, url: String) {}
        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
        }
    }
}