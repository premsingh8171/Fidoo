package com.fidoo.user.profile.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.profile.viewmodel.EditProfileViewModel
import com.fidoo.user.utils.BaseActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_signup_screen_fm.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.regex.Pattern

class EditProfileActivity : BaseActivity() {
    var viewmodel: EditProfileViewModel? = null
    var filePathTemp: String? = ""
    var sessionTwiclo: SessionTwiclo? = null
    var contsCardHeight = 0
    var height = 0
    var width = 0

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+")

    private var mMixpanel: MixpanelAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_edit_profile)
        viewmodel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        sessionTwiclo = SessionTwiclo(this)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        // Display size
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = Math.round(width * 0.5).toInt()

        height = Math.round(((displayMetrics.heightPixels * 40) / 100).toDouble()).toInt()

        contsCardHeight = height - 200

        constraintLayoutProEdit.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )

        val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.topToTop=0
        params.setMargins(50,contsCardHeight,50,0)
        constCardEditPro.layoutParams = params

        if (sessionTwiclo!!.profileDetail != null) {
            Log.e("profileDetail_", sessionTwiclo!!.profileDetail.account.image!!)
            nameEdt.setText(sessionTwiclo!!.profileDetail.account.name!!)
            nameEdt.setSelection(nameEdt.length())
            emailEdt.setText(sessionTwiclo!!.profileDetail.account.emailid!!)
            emailEdt.setSelection(emailEdt.length())
            Glide.with(this).asBitmap()
                .load(sessionTwiclo!!.profileDetail.account.image!!)
                .fitCenter()
                .override(100, 100)
                .placeholder(R.drawable.ic_user_single)
                .into(img_user)
            mobileEdt.setText("+91 " + sessionTwiclo!!.profileDetail.account.userName!!)
        } else {
            mobileEdt.setText("+91 " + sessionTwiclo!!.loginDetail.account.userName!!)
            nameEdt.setText(sessionTwiclo!!.loginDetail.account.name!!)
            emailEdt.setText(sessionTwiclo!!.loginDetail.account.emailid!!)


        }


//        if (sessionTwiclo!!.profileDetail.account!=null){
//            mobileEdt.text = sessionTwiclo!!.profileDetail.account.country_code!!+ sessionTwiclo!!.profileDetail.account.userName!!
//
//        }else{
//            mobileEdt.text = "+91 "+ sessionTwiclo!!.profileDetail.account.userName!!
//
//        }

        save_data.setOnClickListener {
            if (nameEdt.text.toString().equals("")) {
                showToast("Please enter full name")
            } else if (!(isValidEmail(emailEdt.text.toString()))) {
                showToast("Please enter valid email")
            } else {
                addUpdateProfileApi(filePathTemp)
            }
        }

        backIconProfile.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }

        fab_image_camera.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                //   .compress(1024)			//Final image size will be less than 1 MB(Optional)
                //  .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()

        }


        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            showToast("Something went wrong")
        })


        viewmodel?.addUpdateResponse?.observe(this, Observer { user ->
            Log.e("addUpdateResponse_", Gson().toJson(user))
            dismissIOSProgress()
            if (user.errorCode==200) {

                    sessionTwiclo!!.storeProfileDetail(user)
                    //  Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_LONG).show()
//                startActivity(Intent(this, MainActivity::class.java))
//                finishAffinity()
                finish()
            }else{
                showToast(user.message)
            }

        })

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result Code is -1 send from Payumoney activity

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Log.e("ee", resultCode.toString())
            Log.e("data", data.toString())
            val fileUri = data?.data
            // img_user.setImageURI(fileUri)
            Glide.with(this)
                .load(fileUri)
                .fitCenter()
                .into(img_user)
            //You can get File object from intent
            val file: File = ImagePicker.getFile(data)!!

            //You can also get File Path from intent
            val filePath: String = ImagePicker.getFilePath(data)!!
            filePathTemp = filePath

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            //Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }

    }

    fun addUpdateProfileApi(mImagePth: String?) {

        /* if (!isNetworkConnected()) {
             showToast(resources.getString(R.string.provide_internet))
             return
         }*/

        Log.e("mImagePth", mImagePth.toString())
        Log.e("accountId", SessionTwiclo(this).loggedInUserDetail.accountId.toString())
        Log.e("accessToken", SessionTwiclo(this).loggedInUserDetail.accessToken.toString())
        var mImageParts: MultipartBody.Part? = null
        // creating image format for upload
        mImageParts = if (!TextUtils.isEmpty(mImagePth)) {
            // Pass it like this
            val file = File(mImagePth)
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("photo", file.name, requestFile)
        } else {
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            MultipartBody.Part.createFormData("photo", "", requestFile)
        }
        // Parameter request body
        val accountId =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                SessionTwiclo(this).loginDetail.accountId.toString()
            )  // Parameter request body
        val accessToken =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                SessionTwiclo(this).loginDetail.accessToken.toString()
            )  // Parameter request body
        val name =
            RequestBody.create("text/plain".toMediaTypeOrNull(), nameEdt.text.toString())
        val email =
            RequestBody.create("text/plain".toMediaTypeOrNull(), emailEdt.text.toString())
        showIOSProgress()
        viewmodel?.addUpdateProfileApi(
            accountId,
            accessToken, name, email, mImageParts
        )

        // showIOSProgress();
        // getRestfullInstance().uploadImageToGallary(column_number, mImageParts, mApiHandler)
    }

    override fun onBackPressed() {
        finish()
        AppUtils.finishActivityLeftToRight(this)
    }

    fun isValidEmail(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }
}