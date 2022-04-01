package com.fidoo.user.sendpackages.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.R
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.sendpackages.adapter.SendPackagesCategoryAdapter
import com.fidoo.user.sendpackages.adapter.SendPackagesImgAdapter
import com.fidoo.user.sendpackages.model.Categories
import com.fidoo.user.sendpackages.roomdb.database.SendPackagesDb
import com.fidoo.user.sendpackages.roomdb.entity.SendPackagesItemImage
import com.fidoo.user.sendpackages.viewmodel.SendPackagesViewModel
import com.fidoo.user.utils.BaseActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_grocery_new_ui.*
import kotlinx.android.synthetic.main.activity_sendpackages_additem.*
import kotlinx.android.synthetic.main.service_adapter.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class SendPackagesAddItem : BaseActivity() {
    var contsCardHeight = 0
    var height = 0
    var width = 0
    var isSelected: Int? = 0
    var sendPackagesDiolog: Dialog? = null
    private lateinit var sendPackagesDb: SendPackagesDb
    var sendPackagesImgAdapter: SendPackagesImgAdapter? = null
    var check: Int = 0
    var sendPackages_img_id: Int = 0
    var cat_idStr: String = ""
    var cat_nameStr: String = ""
    var arraylist: ArrayList<SendPackagesItemImage>? = null
    var catList: ArrayList<Categories>? = null
    var sendPackagesImgList: ArrayList<String>? = null
    var filePathTemp: String = ""
    var fileUri: Uri? = null
    var cat_Diolog: Dialog? = null
    lateinit var cat_Recyclerview: RecyclerView
    var viewmodel: SendPackagesViewModel? = null
    private var mMixpanel: MixpanelAPI? = null
    var selectValue2: String = ""
    var text_et: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_color)
        setContentView(R.layout.activity_sendpackages_additem)
        // deleteAllSendPackages()
        viewmodel = ViewModelProvider(this).get(SendPackagesViewModel::class.java)
        sendPackagesImgList = ArrayList()

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
        // Display size
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = Math.round(width * 0.5).toInt()

        height = Math.round(((displayMetrics.heightPixels * 40) / 100).toDouble()).toInt()

        contsCardHeight = height - 150

        sendPackagesConstAddItem.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )

        val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.topToTop = 0
        params.setMargins(50, contsCardHeight, 50, 0)
        sendPackagesConstCardAddItem.layoutParams = params

        if (SendPackageActivity.catnameeee.isNotEmpty()) {
            selectCat_text.text = SendPackageActivity.catnameeee
        }
        if (SendPackageActivity.etvalue.isNotEmpty()) {
            itemList_eTxt.setText(SendPackageActivity.etvalue)
        }


        viewmodel?.getPackageCatApi2(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken
        )

        viewmodel?.catResponse!!.observe(this, {
            Log.e("catResponse_", Gson().toJson(it))
            if (it.error_code == 200) {
                if (it.categories_list.size != 0) {
                    catList = it.categories_list as ArrayList<Categories>
                }
            }
        })

        viewmodel?.uploadSendPackagesResponse?.observe(this, {
            dismissIOSProgress()
            Log.e("uploadSendPackages__", Gson().toJson(it))
            sendPackagesUpdate(
                sendPackages_img_id,
                fileUri.toString(),
                filePathTemp,
                it.document_id
            )
            sendPackages_img_id++
            sendPackagesImgInsert(sendPackages_img_id, "null", "null", "null")
            getSendPackagesImg()

            sendPackagesImgList!!.add(it.document_id)
            val s: Set<String> = LinkedHashSet<String>(sendPackagesImgList)
            sendPackagesImgList!!.clear()
            sendPackagesImgList!!.addAll(s)
            selectCat_text.text = cat_nameStr

        })

        back_actionAddItem.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }

        itemNameConLl.setOnClickListener {
            catPopUp()


        }

        info_imTop.setOnClickListener {
            restrictionPopUp()
        }

        addItemImgll.setOnClickListener {
            addItemImgll.visibility = View.GONE
            addItemRecLl.visibility = View.VISIBLE

        }

        itemList_eTxt?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                SessionTwiclo(this@SendPackagesAddItem).setetvalue(s.toString())
                if (s.isNotEmpty()) {
                    infoll_top.visibility = View.VISIBLE
                } else {
                    infoll_top.visibility = View.GONE
                }
            }
        })

        //agree of restriction
        info_agreeImg.setOnClickListener {
            if (isSelected == 0) {
                save_itemll.background.setTint(getResources().getColor(R.color.primary_color))
                info_agreeImg.setColorFilter(getResources().getColor(R.color.primary_color));
                isSelected = 1
            } else {
                save_itemll.background.setTint(getResources().getColor(R.color.background))
                info_agreeImg.setColorFilter(getResources().getColor(R.color.icon_tin));
                isSelected = 0
            }

        }

        save_itemll.setOnClickListener {
            if (itemList_eTxt.text.toString().isNotEmpty()) {
                if (isSelected == 1) {
                    var selectValue = sendPackagesImgList.toString()
                    val regex = "["
                    selectValue = selectValue.replace(regex, "")
                    selectValue2 = selectValue.replace("]", "").trim()
                    Log.e("val__", selectValue2)


                    val resultIntent = Intent()
                    resultIntent.putExtra("Item_details", "addItem")
                    resultIntent.putExtra("Item_list", itemList_eTxt.text.toString())
                    resultIntent.putExtra("cat_name", cat_nameStr)
                    resultIntent.putExtra("cat_id", cat_idStr)
                    resultIntent.putExtra("document_id", selectValue2)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                    AppUtils.finishActivityLeftToRight(this)
                } else {
                    val toast =
                        Toast.makeText(this, "Please select restriction", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }else{
                val toast =
                    Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        sendPackagesImgInsert(sendPackages_img_id, "null", "null", "null")
        getSendPackagesImg()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("cartActivity__", "request code $requestCode resultcode $resultCode")
        if (requestCode == Checkout.RZP_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        else {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.e("data", data.toString())
                    fileUri = data?.data
                    val file: File = ImagePicker.getFile(data)!!
                    val filePath: String = ImagePicker.getFilePath(data)!!
                    filePathTemp = filePath

                    Log.e("filePath_", filePath)
                    //yaha hai

                    uplaodGallaryImage(filePath!!)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Please upload your prescription", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    fun uplaodGallaryImage(mImagePth: String?) {
        Log.e("mImagePth", mImagePth.toString())
        var mImageParts: MultipartBody.Part? = null
        mImageParts = if (!TextUtils.isEmpty(mImagePth)) {
            val file = File(mImagePth)
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("document", file.name, requestFile)
        } else {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            MultipartBody.Part.createFormData("document", "", requestFile)
        }
        val accountId = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            SessionTwiclo(this).loggedInUserDetail.accountId
        )
        val accessToken = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            SessionTwiclo(this).loggedInUserDetail.accessToken
        )

        showIOSProgress()
        viewmodel?.uploadSendPackagesImage(
            accountId,
            accessToken,
            mImageParts
        )

    }


    private fun restrictionPopUp() {
        sendPackagesDiolog = Dialog(this)
        sendPackagesDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sendPackagesDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sendPackagesDiolog?.setContentView(R.layout.send_packages_additem_popup)
        sendPackagesDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        // sendPackagesDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        sendPackagesDiolog?.setCanceledOnTouchOutside(true)
        sendPackagesDiolog?.show()
        val disable_gradientRl =
            sendPackagesDiolog?.findViewById<RelativeLayout>(R.id.disable_gradientRl)
        val restrictiontxt = sendPackagesDiolog?.findViewById<TextView>(R.id.restrictiontxt)
        val restrictionLl = sendPackagesDiolog?.findViewById<LinearLayout>(R.id.restrictionLl)

        restrictionLl!!.visibility = View.VISIBLE
        dynamicTextForCenter(
            restrictiontxt!!,
            "Delivery of ",
            "alcohal, tobacco or any illegal items ",
            "prohibited by the Gov. of India."
        )

        disable_gradientRl?.setOnClickListener {
            sendPackagesDiolog?.dismiss()
        }

    }


    //prescription database
    private fun sendPackagesImgInsert(
        pres_id: Int,
        image: String,
        file_path: String,
        document_id: String
    ) {
        Thread {
            try {
                sendPackagesDb = Room.databaseBuilder(
                    applicationContext,
                    SendPackagesDb::class.java, SendPackagesDb.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                sendPackagesDb!!.sendPackagesDao()!!
                    .insertSendPackagesView(
                        SendPackagesItemImage(
                            pres_id.toString(),
                            image,
                            file_path,
                            document_id
                        )
                    )

                getSendPackagesImg()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    } //prescription database


    private fun sendPackagesUpdate(
        pres_id: Int,
        image: String,
        file_path: String,
        document_id: String
    ) {
        Thread {
            try {
                sendPackagesDb = Room.databaseBuilder(
                    applicationContext,
                    SendPackagesDb::class.java, SendPackagesDb.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                sendPackagesDb!!.sendPackagesDao()!!
                    .updateSendPackagesView(pres_id, image, file_path!!, document_id)

                getSendPackagesImg()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    //getprescription
    private fun getSendPackagesImg() {
        Handler().postDelayed(
            {
                try {
                    dismissIOSProgress()
                    sendPackagesDb!!.sendPackagesDao()!!.getSendPackagesView().observe(this, {
                        arraylist = it as ArrayList
                        Log.e("presasb_", arraylist!!.size.toString())

                        sendPackagesImgAdapter = SendPackagesImgAdapter(
                            this,
                            arraylist!!,
                            object : SendPackagesImgAdapter.OnClickSendPackages {
                                override fun clearImage(
                                    position: Int,
                                    model: SendPackagesItemImage
                                ) {

                                    showIOSProgress()
                                    deleteSendPackages(model.package_imgId.toInt())
                                    sendPackagesImgList!!.remove(model.document_id)

                                    viewmodel?.deleteSendPackagesApi(
                                        SessionTwiclo(this@SendPackagesAddItem).loggedInUserDetail.accountId,
                                        SessionTwiclo(this@SendPackagesAddItem).loggedInUserDetail.accessToken,
                                        model.document_id
                                    )

                                }

                                override fun uploadImage(
                                    position: Int,
                                    model: SendPackagesItemImage
                                ) {

                                    sendPackages_img_id = model.package_imgId.toInt()
                                    ImagePicker.with(this@SendPackagesAddItem)
                                        .crop()
                                        .compress(1024)
                                        .cameraOnly()
                                        .maxResultSize(1080, 1080)
                                        .start()
                                }
                            })

                        addItem_rv?.adapter = sendPackagesImgAdapter

                    })

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, 100
        )
    }

    //delete prescription
    private fun deleteSendPackages(pres_id: Int) {
        Thread {
            try {
                sendPackagesDb = Room.databaseBuilder(
                    applicationContext,
                    SendPackagesDb::class.java, SendPackagesDb.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                sendPackagesDb!!.sendPackagesDao()!!.deleteItem(pres_id)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        getSendPackagesImg()

    }

    private fun catPopUp() {
        cat_Diolog = Dialog(this@SendPackagesAddItem)
        cat_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        cat_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cat_Diolog?.setContentView(R.layout.select_cat_sendpackage_popup)
        cat_Diolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        cat_Diolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        cat_Diolog?.setCanceledOnTouchOutside(true)
        cat_Diolog?.show()
        val dismisspopUp_ = cat_Diolog?.findViewById<ConstraintLayout>(R.id.dismisspopUp_)
        cat_Recyclerview = cat_Diolog?.findViewById(R.id.cat_Recyclerview)!!

        dismisspopUp_?.setOnClickListener {
            cat_Diolog?.dismiss()

        }

        if (catList!!.size != 0) {
            rvCategory(catList!!)
        }

    }

    private fun rvCategory(list: ArrayList<Categories>) {
        cat_Recyclerview.adapter = SendPackagesCategoryAdapter(
            this,
            list,
            object : SendPackagesCategoryAdapter.CategoryItemClick {
                override fun onItemClick(pos: Int, sendPackage: Categories) {
                    cat_Diolog?.dismiss()
                    cat_idStr = sendPackage.id
                    cat_nameStr = sendPackage.cat_name
                    selectCat_text.text = sendPackage.cat_name
                    SessionTwiclo(this@SendPackagesAddItem).setcatname(sendPackage.cat_name)

                    // itemList_eTxt.text=""
                }
            })

    }


    override fun onBackPressed() {

        finish()
        AppUtils.finishActivityLeftToRight(this)
    }
}