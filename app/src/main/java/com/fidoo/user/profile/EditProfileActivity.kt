package com.fidoo.user.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.utils.BaseActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_edit_profile.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileActivity : BaseActivity() {
    var viewmodel: EditProfileViewModel? = null
    var filePathTemp: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        viewmodel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)

        if (sessionInstance.profileDetail != null) {
            Log.e("ddd", sessionInstance.profileDetail.account.image!!)
            nameEdt.setText(sessionInstance.profileDetail.account.name!!)
            nameEdt.setSelection(nameEdt.length())
            emailEdt.setText(sessionInstance.profileDetail.account.emailid!!)
            emailEdt.setSelection(emailEdt.length())
            Glide.with(this).asBitmap()
                .load(sessionInstance.profileDetail.account.image!!)
                .fitCenter()
                .override(100, 100)
                .placeholder(R.drawable.ic_user_single)
                .into(img_user)
        }

        mobileEdt.text = sessionInstance.profileDetail.account.country_code!!+ sessionInstance.profileDetail.account.userName!!
        save_data.setOnClickListener {
            if (nameEdt.text.toString().equals("")) {
                showToast("Please enter full name")
            } else if (emailEdt.text.toString().equals("")) {
                showToast("Please enter email")
            } else {
                addUpdateProfileApi(filePathTemp)
            }
        }
        backIconProfile.setOnClickListener {

            finish()
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
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })


        viewmodel?.addUpdateResponse?.observe(this, Observer { user ->

            dismissIOSProgress()
            if (user.account == null) {
                showToast(user.message)
            } else {
                sessionInstance.storeProfileDetail(user)
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
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
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
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
                SessionTwiclo(this).loggedInUserDetail.accountId
            )  // Parameter request body
        val accessToken =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                SessionTwiclo(this).loggedInUserDetail.accessToken
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
}