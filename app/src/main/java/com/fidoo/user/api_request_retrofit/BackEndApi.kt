package com.fidoo.user.api_request_retrofit

import com.example.myapplication.Models.cancelStatus.ChatCancelModel
import com.fidoo.user.addressmodule.model.AddAddressModel
import com.fidoo.user.addressmodule.model.DeleteAddressModel
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.addressmodule.model.RemoveAddressModel
import com.fidoo.user.cartview.model.CartModel
import com.fidoo.user.chatbot.model.feedBackModel.FeedbackModel
import com.fidoo.user.chatbot.model.cancelWithoutRefundEmpty
import com.fidoo.user.chatbot.model.cancelWithoutRefundWithSendkey
import com.fidoo.user.dailyneed.model.DailyNeedModel
import com.fidoo.user.dailyneed.model.prdmodel.ViewAllProduct
import com.fidoo.user.dashboard.model.CheckPaymentStatusModel
import com.fidoo.user.dashboard.model.HomeServicesModel
import com.fidoo.user.dashboard.model.newmodel.ServiceDetailsModel
import com.fidoo.user.data.model.*
import com.fidoo.user.grocerynewui.model.getGroceryProducts.GroceryProductsResponse
import com.fidoo.user.newRestaurants.model.NewStoreDetailsModel
import com.fidoo.user.newsearch.model.KeywordBasedSearchResultsModel
import com.fidoo.user.newsearch.model.KeywordBasedSearchSuggestionsModel
import com.fidoo.user.newsearch.model.SearchSuggestionsModel
import com.fidoo.user.ordermodule.model.*
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.NewTrackModel
import com.fidoo.user.profile.model.EditProfileModel
import com.fidoo.user.referral.model.ReferralModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.restaurants.model.StoreDetailsModel
import com.fidoo.user.search.model.SearchListModel
import com.fidoo.user.sendpackages.model.*
import com.fidoo.user.store.model.StoreListingModel
import com.fidoo.user.user_tracker.model.UserTrackerModel
import com.fidoo.user.viewmodels.UpdateAppModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface BackEndApi {


    @FormUrlEncoded
    @POST("login.inc.php")
    fun login(
        @Field("username") username: String?,
        @Field("country_code") country_code: String?,
        @Field("device_id") device_id: String?,
        @Field("device_type") device_type: String?,
        @Field("referred_by") referred_by: String?
    ): Call<LoginModel>

    @FormUrlEncoded
    @POST("homeBanner.inc.php")
    fun getBannersApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("isNewApp") isNewApp: String?

    ): Call<BannerModel>

    @FormUrlEncoded
    @POST("homeData.inc.php")
  suspend  fun gethomeDataApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("user_lat") user_lat: String?,
        @Field("user_long") user_long: String?
    ): Response<ServiceDetailsModel>

    @FormUrlEncoded
    @POST("serviceList.inc.php")
    fun getHomeServicesApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?

    ): Call<HomeServicesModel>

    @FormUrlEncoded
    @POST("refer.inc.php")
    fun getReferralApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<ReferralModel>

    @FormUrlEncoded
    @POST("storeCategoryList.inc.php")
    fun getStoreCategoryApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("store_id") store_id: String?,
        @Field("cat_search") cat_search: String?

    ): Call<StoreCategoriesModel>

    @FormUrlEncoded
    @POST("storeList.inc.php")
    fun getStoresApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("service_id") service_id: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("distance_start") distance_start: String?,
        @Field("distance_end") distance_end: String?,
        @Field("sort_by") sort_by: String?,
        @Field("cuisine_to_search") cuisine_to_search: String?,
        @Field("page_count") page_count: String?
    ): Call<StoreListingModel>

    @FormUrlEncoded
    @POST("storeDetails.inc.php")
    fun getStoreDetailsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("store_id") store_id: String?,
        @Field("is_nonveg") is_nonveg: String?,
        @Field("cat_id") cat_id: String?,
        @Field("contains_egg") contains_egg: String?

    ): Call<StoreDetailsModel>

    //new storeDetailsApi
    @FormUrlEncoded
    @POST("storeDetails_v2.inc.php")
    suspend  fun getStoreDetailsApiNew(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("store_id") store_id: String?,
        @Field("is_nonveg") is_nonveg: String?,
        @Field("cat_id") cat_id: String?,
        @Field("contains_egg") contains_egg: String?,
        @Field("start_id") start_id: String?
    ): Response<NewStoreDetailsModel>

    //for grocery
    @FormUrlEncoded
    @POST("getGroceryProducts.inc.php")
    fun getGroceryProducts(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("store_id") store_id: String?,
        @Field("category_id") category_id: String?,
        @Field("subcategory_id") subcategory_id: String?
    ): Call<com.fidoo.user.grocery.model.getGroceryProducts.GroceryProductsResponse>

    //for grocery
    @FormUrlEncoded
    @POST("getGroceryProducts.inc.php")
    fun getGroceryProductsnew(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("store_id") store_id: String?,
        @Field("category_id") category_id: String?,
        @Field("subcategory_id") subcategory_id: String?,
        @Field("allow_limit") allow_limit: String?
    ): Call<GroceryProductsResponse>

    @FormUrlEncoded
    @POST("searchStoreProduct.inc.php")
    fun getSearchStoreProductApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("store_id") store_id: String?,
        @Field("search") search: String?,
        @Field("is_nonveg") is_nonveg: String?
    ): Call<StoreDetailsModel>

    @FormUrlEncoded
    @POST("productDetails.inc.php")
    fun getProductDetailsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("product_id") product_id: String?

    ): Call<ProductDetailsModel>

    @FormUrlEncoded
    @POST("customerActivityLog.inc.php")
    fun customerActivityLogApi(
        @Field("accountId") accountId: String?,
        @Field("user_name") user_name: String?,
        @Field("screen_name") screen_name: String?,
        @Field("app_version") app_version: String?,
        @Field("service_type") service_type: String?,
        @Field("deviceId") deviceId: String?
    ): Call<UserTrackerModel>

/*
    @Headers("Content-Type: application/json;charset=UTF-8")
    @FormUrlEncoded
    @POST("addcart.inc.php")
    fun addToCartApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("products") products: String?


    ): Call<AddToCartModel>*/

    /*  @Headers("Accept: application/json", "Content-Type: application/json")
      @FormUrlEncoded
      @POST("addcart.inc.php")
      fun addToCartApi(@Field("accountId") accountId: String?,
                       @Field("accessToken") accessToken: String?,
                       @Body products: ArrayList<AddCartInputModel>
      ): Call<AddToCartModel>*/

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("addcart.inc.php")
    fun addToCartApi(@Body dataPost: AddCartInputModelFinal): Call<AddToCartModel>

    /*
    @FormUrlEncoded
    @POST("addcart.inc.php")
    fun addToCartApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("product_id") product_id: String?,
        @Field("quantity") quantity: String?,
        @Field("message") message: String?,
        @Field("is_customize") is_customize: String?,
        @Field("customize_sub_cat_id[]") customize_sub_cat_id: ArrayList<String>?

    ): Call<AddToCartModel>*/

    @FormUrlEncoded
    @POST("orderRatingReview.inc.php")
    fun reviewSubmitApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?,
        @Field("store_rating") store_rating: String?,
        @Field("store_review") store_review: String?,
        @Field("delivery_rating") delivery_rating: String?,
        @Field("delivery_review") delivery_review: String?
    ): Call<ReviewModel>

    @FormUrlEncoded


    @POST("addFeedback.php")
    fun addFeedbackApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?,
        @Field("star") star: String?,
        @Field("improvement[]") improvement: String?,
        @Field("message") message: String?,
        @Field("type") type: String?
    ): Call<Feedback>

    @FormUrlEncoded
    @POST("cartdetail.inc.php")
    fun getCartDetailsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("user_lat") userLat: String?,
        @Field("user_long") userLong: String?
    ): Call<CartModel>

    //@Field("store_customer_distance") storeCustomerDistance: String?,
    //@Field("store_id") storeId: String?

    @FormUrlEncoded
    @POST("deleteCartProduct.inc.php")
    fun deleteCartApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("product_id") product_id: String?,
        @Field("cart_id") cart_id: String?
    ): Call<DeleteModel>

    @FormUrlEncoded
    @POST("deleteCartCoupon.inc.php")
    fun deleteCartCouponApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<DeleteModel>

    @FormUrlEncoded
    @POST("addRemoveCartProduct.inc.php")
    fun addRemoveCartApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("product_id") product_id: String?,
        @Field("addRemoveType") addRemoveType: String?,
        @Field("is_customize") is_customize: String?,
        @Field("product_customize_id") product_customize_id: String?,
        @Field("cart_id") cart_id: String?,
        @Field("customize_sub_cat_id[]") customize_sub_cat_id: ArrayList<String>?
    ): Call<AddRemoveCartModel>

    @FormUrlEncoded
    @POST("payment.inc.php")
    fun paymentApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?,
        @Field("transaction_id") transaction_id: String?,
        @Field("payment_bank") payment_bank: String?,
        @Field("payment_mode") payment_mode: String?,
        @Field("other_taxes_and_charges") other_taxes_and_charges: String?
    ): Call<PaymentModel>

    @FormUrlEncoded
    @POST("paymentFailure.inc.php")
    fun paymentFailureApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?
    ): Call<PaymentModel>
/*
    @FormUrlEncoded
    @POST("generateRazorPayOrderId.inc.php")
    fun razorPayPaymentApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?,
        @Field("amount") amount: String?
    ): Call<PaymentModel> */

    @FormUrlEncoded
    @POST("order.inc.php")
    fun orderPlaceApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("payment_amt") payment_amt: String?,
        @Field("delivery_option") delivery_option: String?,
        @Field("address_id") address_id: String?,
        @Field("promo_id") promo_id: String?,
        @Field("delivery_instructions") delivery_instructions: String?,
        @Field("payment_mode") payment_mode: String?,
        @Field("merchant_instructions") merchant_instructions: String?
    ): Call<OrderPlaceModel>

    @FormUrlEncoded
    @POST("listAddress.inc.php")
    fun getAddressesApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("store_latitude") storeLatitude: String?,
        @Field("store_longitude") storeLongitude: String?
    ): Call<GetAddressModel>


    @FormUrlEncoded
    @POST("applyPromoCode.inc.php")
    fun applyOffersApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("promocode") promocode: String?
    ): Call<ApplyPromoModel>

    @FormUrlEncoded
    @POST("offers.inc.php")
    fun getOffersApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<PromoModel>

    @FormUrlEncoded
    @POST("aboutus.inc.php")
    fun getAboutUsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<AboutUsModel>

    @POST("termnconditon.inc.php")
    fun getTermsApi(
    ): Call<AboutUsModel>

    @POST("cancelationpolicy.inc.php")
    fun getCancellationApi(
    ): Call<AboutUsModel>

    @FormUrlEncoded
    @POST("orderList.inc.php")
    fun getMyOrdersApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<MyOrdersModel>

    @FormUrlEncoded
    @POST("orderDetails.inc.php")
    fun orderDetailsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?
    ): Call<OrderDetailsModel>

    @FormUrlEncoded
    @POST("productChangeRequestDetails.inc.php")
    fun productChangeRequestDetailsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("request_id") request_id: String?
    ): Call<ProductChangeRequestDetailsModel>

    @FormUrlEncoded
    @POST("approveProductChangeRequest.inc.php")
    fun approveProductChangeRequestApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("request_id") request_id: String?,
        @Field("order_id") order_id: String?
    ): Call<ApproveProductChangeRequestModel>

    @FormUrlEncoded
    @POST("sendPackageOrderDetails.inc.php")
    fun sendPackageOrderDetailsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?
    ): Call<NewSendpackageOrderDetail>

    @FormUrlEncoded
    @POST("orderCancel.inc.php")
    fun cancelOrderApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?
    ): Call<DeleteModel>

    @FormUrlEncoded
    @POST("trackPackage.inc.php")
    fun trackPackageOrderApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?
    ): Call<TrackPackageOrderModel>

    @FormUrlEncoded
    @POST("repeatOrder.inc.php")
    fun repeatOrderApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?
    ): Call<RepeatOrderModel>

    @FormUrlEncoded
    @POST("getOrderStatus.inc.php")
    fun getOrderStatusApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?
    ): Call<GetOrderStatusModel>

    @FormUrlEncoded
    @POST("locationGet.inc.php")
    fun getLocationApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") order_id: String?,
        @Field("user_type") user_type: String?
    ): Call<LocationResponseModel>

    @FormUrlEncoded
    @POST("customizeProduct.inc.php")
    fun customizeProductApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("product_id") product_id: String?
    ): Call<CustomizeProductResponseModel>

    @FormUrlEncoded
    @POST("addAddress.inc.php")
    fun addAddressApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("flat_no") flat_no: String?,
        @Field("building") building: String?,
        @Field("location") location: String?,
        @Field("landmark") landmark: String?,
        @Field("address_type") address_type: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("name") name: String?,
        @Field("email_id") email_id: String?,
        @Field("is_default") is_default: String?,
        @Field("phone_no") phone_no: String?,
        @Field("contact_type ") contact_type : String?
    ): Call<AddAddressModel>

    @FormUrlEncoded
    @POST("editAddress.inc.php")
    fun editAddressApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("flat_no") flat_no: String?,
        @Field("building") building: String?,
        @Field("location") location: String?,
        @Field("landmark") landmark: String?,
        @Field("address_type") address_type: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("name") name: String?,
        @Field("phone_no") phone_no: String?,
        @Field("email_id") email_id: String?,
        @Field("address_id") address_id: String?,
        @Field("is_default") is_default: String?,
        @Field("contact_type ") contact_type : String?
    ): Call<AddAddressModel>

    @FormUrlEncoded
    @POST("deleteAddress.inc.php")
    fun deleteAddressApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("address_id") address_id: String?
    ): Call<DeleteAddressModel>

    @FormUrlEncoded
    @POST("removeAddresss.inc.php")
    fun removeAddressApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("address_id") address_id: String?
    ): Call<RemoveAddressModel>

    @FormUrlEncoded
    @POST("searchList.inc.php")
    fun searchApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("search") search: String?,
        @Field("store_id") store_id: String?
    ): Call<SearchModel>

    @FormUrlEncoded
    @POST("searchList.inc.php")
    fun searchApiForNewUi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("search") search: String?
    ): Call<SearchListModel>

    @FormUrlEncoded
    @POST("logout.inc.php")
    fun logoutApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<LogoutModel>

    @FormUrlEncoded
    @POST("sendPackageCategory.inc.php")
    fun packageCategoriesApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<PackageCatResponseModel>

    @FormUrlEncoded
    @POST("sendPackageCategory.inc.php")
    fun packageCategoriesApi2(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<SendPackagesCatModel>

    @FormUrlEncoded
    @POST("getSendPackagePayModeStatus.inc.php")
    fun getSendPackagePayModeStatusApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<SendPackagePaymentModeModel>

    @FormUrlEncoded
    @POST("countcart.inc.php")
    fun cartCountApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<CartCountModel>

    @FormUrlEncoded
    @POST("checkPaymentStatus.inc.php")
    fun checkPaymentStatusApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<CheckPaymentStatusModel>

    @FormUrlEncoded
    @POST("deleteCart.inc.php")
    fun clearCartApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<ClearCartModel>

    @FormUrlEncoded
    @POST("sendPackage.inc.php")
    fun getPackageDetails(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("package_distance") package_distance: String?
    ): Call<SendPackagesModel>

    @FormUrlEncoded
    @POST("sendPackagePayment.inc.php")
    fun sendPackageApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("from_address") from_address: String?,
        @Field("from_note") from_note: String?,
        @Field("to_address") to_address: String?,
        @Field("from_name") from_name: String?,
        @Field("from_phone") from_phone: String?,
        @Field("to_name") to_name: String?,
        @Field("to_phone") to_phone: String?,
        @Field("payment_mode") payment_mode: String?,
        @Field("package_distance") package_distance: String?,
        @Field("payment_amount") payment_amount: String?,
        @Field("package_delivery_time") package_delivery_time: String?,
        @Field("from_latitude") from_latitude: String?,
        @Field("from_longitude") from_longitude: String?,
        @Field("to_latitude") to_latitude: String?,
        @Field("to_longitude") to_longitude: String?,
        @Field("category_id") category_id: String?,
        @Field("item_name") item_name: String?,
        @Field("images") images: String?,
        @Field("tax") tax: String?
    ): Call<SendPackageOrderDetailModel>

    /*@Field("from_address") from_address: String?,
    @Field("from_note") from_note: String?,
    @Field("to_address") to_address: String?,
    @Field("from_name") from_name: String?,
    @Field("from_phone") from_phone: String?,
    @Field("to_name") to_name: String?,
    @Field("to_phone") to_phone: String?,*/

    @FormUrlEncoded
    @POST("customerCallDelivery.inc.php")
    fun callCustomerApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("customer_phone") customer_phone: String?,
        @Field("delivery_boy_phone") delivery_boy_phone: String?
    ): Call<CallResponseModel>

    @FormUrlEncoded
    @POST("customerCallMerchant.inc.php")
    fun customerCallMerchantApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("customer_phone") customer_phone: String?,
        @Field("merchant_phone") merchant_phone: String?
    ): Call<CustomerCallMerchantModel>

    @FormUrlEncoded
    @POST("deletePrescription.inc.php")
    fun deletePrescriptionApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("document_id") document_id: String?
    ): Call<DeletePrescriptionModel>

    @FormUrlEncoded
    @POST("deletePackageImage.inc.php")
    fun deleteSendPackagesImgApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("document_id") document_id: String?
    ): Call<DeleteSendPackagesImgModel>

    @Multipart
    @POST("uploadPrescription.inc.php")
    fun uploadPrescriptionImage(
        @Part("accountId") accountId: RequestBody?,
        @Part("accessToken") accessToken: RequestBody?,
        @Part document: MultipartBody.Part?
    ): Call<UploadPresModel>

    @Multipart
    @POST("uploadPackageImage.inc.php")
    fun uploadSendPackagesImage(
        @Part("accountId") accountId: RequestBody?,
        @Part("accessToken") accessToken: RequestBody?,
        @Part document: MultipartBody.Part?
    ): Call<SendPackagesUploadImageModel>

    @Multipart
    @POST("uploadPrescription.inc.php")
    fun uploadPrescriptionMultipleImage(
        @Part("accountId") accountId: RequestBody?,
        @Part("accessToken") accessToken: RequestBody?,
        @Part prescription: ArrayList<MultipartBody.Part>?
    ): Call<UploadPresModel>

    @Multipart
    @POST("addUpdateProfile.inc.php")
     fun addUpdateProfileApi(
        @Part("accountId") accountId: RequestBody?,
        @Part("accessToken") accessToken: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("emailid") emailid: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Call<EditProfileModel>

    @FormUrlEncoded
    @POST("orderProceed_ver_29.inc.php")
    fun proceedToOrder(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("order_id") orderId: String?
    ): Call<ProceedToOrder>

    @FormUrlEncoded
    @POST("custAppVerCheck.inc.php")
    fun updateApp(
        @Field("app_version") app_version: String?,
        @Field("accountId") accountId: String?,
        @Field("deviceId") deviceId: String?
    ): Call<UpdateAppModel>


    @FormUrlEncoded
  //  @POST("serviceDetailsProductDriven.inc.php")
    @POST("serviceDetailsProductDriven_ver_36.inc.php")
    fun getserviceDetailsProductDrivenApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("service_id") service_id: String?,
        @Field("user_lat") user_lat: String?,
        @Field("user_long") user_long: String?
    ): Call<DailyNeedModel>

    @FormUrlEncoded
    @POST("viewAllproductsProductDriven.inc.php")
    fun viewAllproductsProductDriven(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("category_id") category_id: String?,
        @Field("user_lat") user_lat: String?,
        @Field("user_long") user_long: String?
    ): Call<ViewAllProduct>

    @FormUrlEncoded
    @POST("searchSuggestions.inc.php")
    fun searchSuggestionsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?
    ): Call<SearchSuggestionsModel>

    @FormUrlEncoded
    @POST("keywordBasedSearchSuggestions.inc.php")
    fun keywordBasedSearchSuggestionsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("keyword") keyword: String?,
        @Field("user_lat") user_lat: String?,
        @Field("user_long") user_long: String?,
        @Field("page_count") page_count: String?,
        @Field("service_id") service_id: String?
    ): Call<KeywordBasedSearchSuggestionsModel>

    @FormUrlEncoded
    @POST("keywordBasedSearchResults.inc.php")
    fun keywordBasedSearchResultsApi(
        @Field("accountId") accountId: String?,
        @Field("accessToken") accessToken: String?,
        @Field("keyword") keyword: String?,
        @Field("user_lat") user_lat: String?,
        @Field("user_long") user_long: String?,
        @Field("page_count") page_count: String?
    ): Call<KeywordBasedSearchResultsModel>

    //chat bot
    @FormUrlEncoded
    @POST("chatbotCancelOrder.inc.php")
    fun cancelOrderApi1(
        @Field("accessToken") accessToken: String,
        @Field("accountId") accountId: String,
        @Field("orderId") orderId: String
    ): Call<ChatCancelModel>

    @FormUrlEncoded
    @POST("chatbotGetOrderDetails.inc.php")
    fun oredrStatusApi(
        @Field("accessToken") accessToken: String,
        @Field("accountId") accountId: String,
        @Field("orderId") orderId: String
    ): Call<OrderStatusResponse>

    @FormUrlEncoded
    @POST("chatbotGetMessages.inc.php")
    fun botStatusMsgApi(
        @Field("accessToken") accessToken: String,
        @Field("accountId") accountId: String,
        @Field("orderId") orderId: String?
    ): Call<OrderStatusMsg>

    @FormUrlEncoded
    @POST("orderCancelWithoutRefund.inc.php")
    fun cancelWithoutRefundmsgApi(
        @Field("accessToken") accessToken: String,
        @Field("accountId") accountId: String,
        @Field("orderId") orderId: String
    ): Call<cancelWithoutRefundEmpty>

    @FormUrlEncoded
    @POST("orderCancelWithoutRefund.inc.php")
    fun cancelWithoutRefundmsgApiwithKey(
        @Field("accessToken") accessToken: String,
        @Field("accountId") accountId: String,
        @Field("orderId") orderId: String,
        @Field("res") res: Int
    ): Call<cancelWithoutRefundWithSendkey>

    @FormUrlEncoded
    @POST("chatbotFeedback.inc.php")
    fun FeedbackApi(
        @Field("accessToken") accessToken: String,
        @Field("accountId") accountId: String,
        @Field("orderId") orderId: String,
        @Field("res") res: Int
    ): Call<FeedbackModel>

    @FormUrlEncoded
    @POST("orderTrackingNewScreen.inc.php")
    fun NewTrackScreenApi(
        @Field("accessToken") accessToken: String,
        @Field("accountId") accountId: String,
        @Field("orderId") orderId: String
    ): Call<NewTrackModel>

}

