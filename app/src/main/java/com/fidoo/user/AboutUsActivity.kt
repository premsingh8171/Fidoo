package com.fidoo.user

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUsActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)


        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        aboutUsWebView.webViewClient = WebViewClient()

        // this will load the url of the website
        if (intent.getStringExtra("about_us").equals("about_us")){
            aboutUsWebView.loadUrl("https://fidoo.in/our-story")
        }else if(intent.getStringExtra("privacy_policy").equals("privacy_policy")){
            aboutUsWebView.loadUrl("https://fidoo.in/privacy-policy")
        } else{
            aboutUsWebView.loadUrl("https://fidoo.in/faq")
        }


        // this will enable the javascript settings
        aboutUsWebView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        aboutUsWebView.settings.setSupportZoom(true)

    }



}
