package com.fidoo.user.ordermodule.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.fidoo.user.R
import com.fidoo.user.ordermodule.adapter.OrdersAdapter
import com.fidoo.user.ordermodule.model.MyOrdersModel
import com.fidoo.user.ordermodule.model.ReviewModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.FragmentOrdersBinding
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.interfaces.AdapterReviewClick
import com.fidoo.user.ordermodule.model.UploadPresModel
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.ordermodule.viewmodel.MyOrdersFragmentViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class OrdersFragment : Fragment(),
    AdapterReviewClick,
    AdapterClick {

    var viewmodel: MyOrdersFragmentViewModel? = null
    private var _progressDlg: ProgressDialog? = null
    var orderIdTemp: String? = ""
    var mmContext: Context? = null
    var fragmentOrdersBinding: FragmentOrdersBinding? = null
    var checkStatusOfReview:Int=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentOrdersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)

        viewmodel = ViewModelProviders.of(requireActivity()).get(MyOrdersFragmentViewModel::class.java)

        try {
            _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
            _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

            _progressDlg!!.setCancelable(false)
            _progressDlg!!.show()
        } catch (ex: Exception) {
            Log.wtf("IOS_error_starting", ex.cause!!)
        }


        if ((activity as MainActivity).isNetworkConnected) {
            if (SessionTwiclo(requireContext()).isLoggedIn){
                viewmodel?.getMyOrders(
                    SessionTwiclo(activity).loggedInUserDetail.accountId,
                    SessionTwiclo(activity).loggedInUserDetail.accessToken
                )
            }else{
                _progressDlg!!.dismiss()
                fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG).show()
            }



        } else {
            _progressDlg!!.dismiss()
            (activity as MainActivity).showInternetToast()
        }


        viewmodel?.failureResponse?.observe(requireActivity(), Observer { user ->
            //dismissIOSProgress()
            if (_progressDlg != null) {

                _progressDlg!!.dismiss()
                _progressDlg = null
            }
            Log.e("cart response", Gson().toJson(user))
            //showToast(user)
            Toast.makeText(mmContext, user, Toast.LENGTH_SHORT).show()


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.myOrdersResponse?.observe(requireActivity(), Observer { user ->
            if (_progressDlg != null) {

                _progressDlg!!.dismiss()
                _progressDlg = null
            }

            if (!user.error) {
                val mModelData: MyOrdersModel = user
                Log.e("ordersResponse", Gson().toJson(mModelData))
                if (mModelData.orders != null) {

                    val adapter = OrdersAdapter(mmContext, mModelData.orders, this, this)
                    fragmentOrdersBinding?.ordersRecyclerView?.layoutManager = GridLayoutManager(context, 1)
                    fragmentOrdersBinding?.ordersRecyclerView?.setHasFixedSize(true)
                    fragmentOrdersBinding?.ordersRecyclerView?.adapter = adapter

                    fragmentOrdersBinding?.noOrdersTxt?.visibility = View.GONE

                } else {
                    fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
                    // Toast.makeText(context,"No Orders", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (user.errorCode == 101) {
                    showAlertDialog(requireContext())
                }
            }
        })

        viewmodel?.reviewResponse?.observe(requireActivity(), { user ->
          if (checkStatusOfReview==1) {
              dismissIOSProgress()
              if (_progressDlg != null) {

                  _progressDlg!!.dismiss()
                  _progressDlg = null
              }


              val mModelData: ReviewModel = user
              Log.e("ordersResponse", Gson().toJson(mModelData))
              Toast.makeText(context, user.message, Toast.LENGTH_SHORT).show()
              viewmodel?.getMyOrders(
                  SessionTwiclo(activity).loggedInUserDetail.accountId,
                  SessionTwiclo(activity).loggedInUserDetail.accessToken
              )
          }

        })

        viewmodel?.uploadPrescriptionResponse?.observe(requireActivity(), { user ->
            dismissIOSProgress()
            if (_progressDlg != null) {

                _progressDlg!!.dismiss()
                _progressDlg = null
            }
            val mModelData: UploadPresModel = user

            Log.e("uploadResponse", Gson().toJson(mModelData))

        })

        return fragmentOrdersBinding?.root
    }


    override fun onReviewDoneClick(
        orderId: String?,
        storeRating: String?,
        reviewStore: String?,
        ratingDriver: String?,
        reviewDriver: String?
    ) {
        try {
            _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
            _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

            _progressDlg!!.setCancelable(false)
            _progressDlg!!.show()
        } catch (ex: Exception) {
            Log.wtf("IOS_error_starting", ex.cause!!)
        }
        checkStatusOfReview=1
        viewmodel?.reviewSubmitApi(
            SessionTwiclo(activity).loggedInUserDetail.accountId,
            SessionTwiclo(activity).loggedInUserDetail.accessToken,
            orderId,
            storeRating,
            reviewStore,
            ratingDriver,
            reviewDriver
        )

    }

    override fun onReviewSubmit(
        orderId: String?,
        star: String?,
        improvement: String?,
        message: String?,
        type: String?
    ) {

        Log.d("orderId____",orderId+"---"+star+"---"+improvement+"---"+message+"--"+type)
     ///yaha karna h


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            // imgProfile.setImageURI(fileUri)

            //You can get File object from intent
            val file: File = ImagePicker.getFile(data)!!

            //You can also get File Path from intent
            val filePath: String = ImagePicker.getFilePath(data)!!
            uplaodGallaryImage(orderIdTemp, data?.data!!.path!!)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(mmContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(mmContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onItemClick(
        productId: String?,
        type: String?,
        count: String?,
        offerPrice: String?,
        customizeCount: Int?,
        productType: String?,
        cart_id: String?
    ) {
        orderIdTemp = productId
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            //   .compress(1024)			//Final image size will be less than 1 MB(Optional)
            //  .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    fun uplaodGallaryImage(orderId: String?, mImagePth: String?) {
        /* if (!isNetworkConnected()) {
             showToast(resources.getString(R.string.provide_internet))
             return
         }*/
        Log.e("orderId", orderId.toString())
        Log.e("mImagePth", mImagePth.toString())
        Log.e("accountId", SessionTwiclo(activity).loggedInUserDetail.accountId.toString())
        Log.e("accessToken", SessionTwiclo(activity).loggedInUserDetail.accessToken.toString())
        var mImageParts: MultipartBody.Part? = null
        // creating image format for upload
        mImageParts = if (!TextUtils.isEmpty(mImagePth)) {
            val file = File(mImagePth)
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("document", file.name, requestFile)
        } else {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            MultipartBody.Part.createFormData("document", "", requestFile)
        }
        // Parameter request body
        val accountId = RequestBody.create("text/plain".toMediaTypeOrNull(), SessionTwiclo(activity).loggedInUserDetail.accountId)  // Parameter request body
        val accessToken = RequestBody.create("text/plain".toMediaTypeOrNull(), SessionTwiclo(activity).loggedInUserDetail.accessToken)  // Parameter request body
        val orderId = RequestBody.create("text/plain".toMediaTypeOrNull(), orderId!!)
        try {
            _progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
            _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

            _progressDlg!!.setCancelable(false)
            _progressDlg!!.show()
        } catch (ex: Exception) {
            Log.wtf("IOS_error_starting", ex.cause!!)
        }
        viewmodel?.uploadPrescriptionImage(accountId, accessToken, orderId, mImageParts)

        // showIOSProgress();
        // getRestfullInstance().uploadImageToGallary(column_number, mImageParts, mApiHandler)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mmContext = context
    }

    override fun onResume() {
        super.onResume()

        if ((activity as MainActivity).isNetworkConnected) {
            if (SessionTwiclo(requireContext()).isLoggedIn){
                Log.e("ONRESUME", "get orders")
                viewmodel?.getMyOrders(
                    SessionTwiclo(activity).loggedInUserDetail.accountId,
                    SessionTwiclo(activity).loggedInUserDetail.accessToken
                )
            }else{
                fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG).show()
            }



        } else {
            _progressDlg!!.dismiss()
            (activity as MainActivity).showInternetToast()
        }

    }

}