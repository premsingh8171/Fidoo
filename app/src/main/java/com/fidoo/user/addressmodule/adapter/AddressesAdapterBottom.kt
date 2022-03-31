package com.fidoo.user.addressmodule.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.sendpackages.activity.SendPackageActivity
import kotlinx.android.synthetic.main.bottom_sheet_saved_address_items.view.*

class AddressesAdapterBottom(
    val con: Context,
    val addressList: MutableList<GetAddressModel.AddressList>,
    val setOnDeteleAddListener: SetOnDeteleAddListener,
    val stringExtra: String?,

) : RecyclerView.Adapter<AddressesAdapterBottom.UserViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_saved_address_items, parent, false)
    )

    override fun getItemCount() = addressList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //holder.tv_address_title.text = "House No.: " + addressList.get(position).flatNo + "\nBuilding: " + addressList[position].building + "\nLandmark: " + addressList.get(position).landmark + "\n" + "Location: " + addressList.get(position).location

        holder.tv_address_title.text = addressList.get(position).flatNo + ", " + addressList.get(position).landmark + ", " + addressList.get(position).location

        if (addressList[position].inDeliveryRange == "0"){
            holder.mainLay.alpha = 0.5f
        }

        when {
            addressList[position].addressType == "1" -> {
                if (addressList[position].is_default == "1") {
                    holder.storeName.text = "Home (Default)"
                } else {
                    holder.storeName?.text = "Home"
                }
                holder.addressTypeIcon.setImageResource(R.drawable.ic_home_new)
            }
            addressList[position].addressType.equals("2") -> {
                if (addressList[position].is_default.equals("1")) {
                    holder.storeName.text = "Office (Default)"
                } else {
                    holder.storeName.text = "Office"
                }
                holder.storeName.text = "Office"
                holder.addressTypeIcon.setImageResource(R.drawable.ic_office_new)
            }

            else -> {
                if (addressList[position].is_default.equals("1")) {
                    holder.storeName.text = "Other (Default)"
                } else {
                    holder.storeName.text = "Other"
                }
                holder.storeName.text = "Other"
                holder.addressTypeIcon.setImageResource(R.drawable.ic_others_new)
            }
        }



        holder.mainLay.setOnClickListener {
            Log.e("clicked", "clicked")
            if (addressList[position].inDeliveryRange == "0"){
                Toast.makeText(con, "Store doesn't deliver to this location", Toast.LENGTH_LONG).show()
            }
            else{

                if (stringExtra.equals("order")) {
                    CartActivity.selectedAddressId = addressList[position].id
                    //CartActivity.selectedAddressName = "House No.: " + addressList.get(position).flatNo + ", Building: " + addressList.get(position).building + ", Landmark: " + addressList.get(position).landmark+", "+addressList[position].location
                    CartActivity.selectedAddressName = addressList.get(position).flatNo + ", Building: " + addressList.get(position).building + ", Landmark: " + addressList.get(position).landmark+", "+addressList[position].location
                    CartActivity.selectedPreAddressName = addressList.get(position).flatNo + "\nBuilding: " + addressList.get(position).building + "\nLandmark: " + addressList.get(position).landmark
                    CartActivity.userLat = addressList[position].latitude
                    CartActivity.userLong = addressList[position].longitude
                    when {
                        addressList[position].addressType.equals("1") -> {
                            CartActivity.selectedAddressTitle= "Home"
                        }
                        addressList[position].addressType.equals("2") -> {
                            CartActivity.selectedAddressTitle= "Office"
                        }
                        else -> {
                            CartActivity.selectedAddressTitle = "Other"
                        }
                    }
                    SessionTwiclo(con).userLat = addressList[position].latitude
                    SessionTwiclo(con).userLng = addressList[position].longitude
                    SessionTwiclo(con).userAddressId = addressList[position].id
                    SessionTwiclo(con).userAddress = addressList.get(position).flatNo + ", " + addressList.get(position).landmark + ", " + addressList.get(position).location
                    val result = Intent()
                    (con as Activity).setResult(101, result)
                    (con as Activity).finish()
                }
                else if (stringExtra.equals("address")) {
                    when {
                        addressList[position].addressType.equals("1") -> {
                            SessionTwiclo(con).userAddress = addressList.get(position).flatNo + ", " + addressList.get(position).landmark + ", " + addressList.get(position).location
                            SessionTwiclo(con).addressType= "Home"
                        }
                        addressList[position].addressType.equals("2") -> {
                            SessionTwiclo(con).userAddress = addressList.get(position).flatNo + ", " + addressList.get(position).landmark + ", " + addressList.get(position).location
                            SessionTwiclo(con).addressType = "Office"
                        }
                        else -> {
                            SessionTwiclo(con).userAddress = addressList.get(position).flatNo + ", " + addressList.get(position).landmark + ", " + addressList.get(position).location
                            SessionTwiclo(con).addressType = "Other"
                        }
                    }

                    SessionTwiclo(con).userAddress = addressList.get(position).flatNo + ", " + addressList.get(position).landmark + ", " + addressList.get(position).location
                    SessionTwiclo(con).userAddressId = addressList[position].id
                    SessionTwiclo(con).userLat = addressList[position].latitude
                    SessionTwiclo(con).userLng = addressList[position].longitude
                    (con as Activity).finish()
                }
                /**
                 * BottomSheet Dialogue Data
                 */
                else if (stringExtra.equals("bottomSheetAddress")) {
                    setOnDeteleAddListener.onClick(addressList[position])
                }

                if (stringExtra.equals("from")) {
                    SendPackageActivity.selectedFromAddress= addressList.get(position).flatNo + ", Building: " + addressList.get(position).building + ", Landmark: " + addressList.get(position).landmark+", "+addressList[position].location
                    //  SendPackageActivity.selectedFromAddress = addressList[position].location
                    SendPackageActivity.selectedfromLat = addressList[position].latitude.toDouble()
                    SendPackageActivity.selectedfromLng= addressList[position].longitude.toDouble()
                    SendPackageActivity.fromName = addressList[position].name
                    SendPackageActivity.fromNumber = addressList[position].phone_no
                    (con as Activity).finish()
                }
                if (stringExtra.equals("to")) {
                    SendPackageActivity.selectedToAddress= addressList.get(position).flatNo + ", Building: " + addressList.get(position).building + ", Landmark: " + addressList.get(position).landmark+", "+addressList[position].location
                    /// SendPackageActivity.selectedToAddress = addressList[position].location
                    SendPackageActivity.selectedtoLat = addressList[position].latitude.toDouble()
                    SendPackageActivity.selectedtoLng= addressList[position].longitude.toDouble()
                    SendPackageActivity.toName = addressList[position].name
                    SendPackageActivity.toNumber = addressList[position].phone_no
                    (con as Activity).finish()
                }
            }
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_address_title = view.tv_location
        var storeName = view.tv_address_title
        var addressTypeIcon = view.locationImage
        var mainLay = view.mainLay
    }

    interface SetOnDeteleAddListener{
        fun onDelete(add_id:String,addressList: GetAddressModel.AddressList)
        fun onClick(addressList: GetAddressModel.AddressList)
    }
}