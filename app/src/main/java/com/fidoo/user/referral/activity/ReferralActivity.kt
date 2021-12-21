package com.fidoo.user.referral.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.referral.viewmodel.ReferralViewModel
import com.fidoo.user.utils.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_referral.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.net.URLEncoder


class ReferralActivity : BaseActivity() {
    lateinit var sessionTwiclo: SessionTwiclo
    var referralviewMode: ReferralViewModel? = null
    var contsCardHeight = 0
    var height = 0
    var width = 0

    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var image_str: String = ""
        var title: String = ""
        var short_description: String = ""
        var description: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_referral)
        sessionTwiclo = SessionTwiclo(this)
        referralviewMode = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(ReferralViewModel::class.java)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")


        // Display size
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        //  int height = displayMetrics.heightPixels;
        //  int height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels
        height = Math.round(width * 0.5).toInt()

        height=Math.round(((displayMetrics.heightPixels*40)/100).toDouble()).toInt()
        Log.d("height___", height?.toString())
        Log.d("height_rrrr__", displayMetrics.heightPixels.toString())
        contsCardHeight = height - 200

        constraintLayoutRef.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )

        val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.topToTop=0
        params.setMargins(50,contsCardHeight,50,0)
        constReferralCard.layoutParams = params

        referralviewMode?.referralApi(
            sessionTwiclo.loggedInUserDetail.accountId,
            sessionTwiclo.loggedInUserDetail.accessToken
        )

        referralviewMode?.referralModelRes!!.observe(this, { response ->
            Log.e("refer_response", Gson().toJson(response))
            if (response.error_code == 200) {
                title = response.title.trimIndent()
                description = response.description.trimIndent()
                short_description = response.short_description.trimIndent()
                image_str = response.image
            }
        })

        referralviewMode?.failureResponse!!.observe(this, {
            Log.e("refer_response", Gson().toJson(it))

        })

        backIcon_referral.setOnClickListener {
            AppUtils.finishActivityLeftToRight(this)
            finish()
        }

        referral_bTn.setOnClickListener {
            createReferraLink()
        }
    }

    private fun createReferraLink() {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://fidoo.in/"))
            .setDomainUriPrefix("https://fidoo.page.link") // Open links with this app on Android
            .setAndroidParameters(
                AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            // .setIosParameters(IosParameters.Builder("com.example.ios").build())
            .buildDynamicLink()

        val dynamicLinkUri: Uri = dynamicLink.uri
        Log.d("dynamicLinkUri_", "Long referral $dynamicLinkUri")
        // https://fidoo.page.link?apn=com.fidoo.user&ibi=com.example.ios&link=https%3A%2F%2Ffidoo.in%2F
       // https://fidoo.in/include/assets/front/image/sec-1.webp

//        val sharelinktext = "https://fidoo.page.link?/?" +
//                "&apn=" + packageName +
//                "&st=" + "Fidoo| Online food, medicine delivery & more!" +
//                "&sd=" + "With Fidoo get same day home delivery." +
//                "&si=" + "https://play-lh.googleusercontent.com/r1KXZidYbhTj0r29484AAYylifZXKYzAZ_slfvsNrz6w8OqxgXUvVKO39Y7RR5OGrT4=s180-rw"+
//                "&link=https://fidoo.in/psd.php?user_id="+sessionTwiclo.loggedInUserDetail.accountId

        val sharelinktext = "https://fidoo.page.link?/?" +
                "&apn=" + packageName +
                "&st="+URLEncoder.encode("Fidoo| Online food, medicine delivery & more!","UTF-8")+
                "&sd="+URLEncoder.encode(short_description,"UTF-8")+""+
                "&si=https://play-lh.googleusercontent.com/r1KXZidYbhTj0r29484AAYylifZXKYzAZ_slfvsNrz6w8OqxgXUvVKO39Y7RR5OGrT4=s180-rw"+
                "&link=https://fidoo.in/psd.php?user_id=" + sessionTwiclo.loggedInUserDetail.accountId

        var sharestr= URLEncoder.encode(sharelinktext,"UTF-8")
        Log.d("sharelinktext", "Long manual referral $sharelinktext")
        Log.d("sharestr", "Long manual referral $sharestr")

        //For short link
        val shortLinkTask: Task<ShortDynamicLink> =
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelinktext))
                //.setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, object : OnCompleteListener<ShortDynamicLink?> {
                    override fun onComplete(@NonNull task: Task<ShortDynamicLink?>) {
                        if (task.isSuccessful()) {
                            // Short link created
                            val shortLink: Uri? = task.getResult()!!.getShortLink()
                            val flowchartLink: Uri? = task.getResult()!!.getPreviewLink()
                            Log.d("shortLink", "Short referral $shortLink\n$flowchartLink")
                            shareApp(shortLink.toString())

                        } else {
                            Log.d("shortLink", "error ${task.exception}")

                        }
                    }
                })
    }


    private fun shareApp(link: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = description.trimIndent()

            shareMessage = "${shareMessage}".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, "$shareMessage\n$link")
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        AppUtils.finishActivityLeftToRight(this)
        finish()
    }

//    fun  copycontent(){
//        val clipboard: ClipboardManager =
//            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip = ClipData.newPlainText("text", "Your Text")
//        clipboard.setPrimaryClip(clip)
//    }

}