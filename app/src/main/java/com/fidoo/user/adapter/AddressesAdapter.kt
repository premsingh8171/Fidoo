package com.fidoo.user.adapter

import android.R.id.message
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.CartActivity
import com.fidoo.user.R
import com.fidoo.user.SendPackageActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.ui.AddAddressActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.saved_address_items.view.*


class AddressesAdapter(
        val con: Context,
        val addressList: MutableList<com.fidoo.user.data.model.GetAddressModel.AddressList>,
        val adapterClick: AdapterClick,
        val stringExtra: String?
) : RecyclerView.Adapter<AddressesAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_address_items, parent, false)
    )

    override fun getItemCount() = addressList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //holder.locText.text=addressList.get(position).flatNo+", "+addressList.get(position).building+", "+addressList.get(position).landmark
        //  holder.nameText.text=addressList.get(position).name
        holder.tv_address_title.text = "House No.: " + addressList.get(position).flatNo + "\nBuilding: " + addressList[position].building + "\nLandmark: " + addressList.get(position).landmark + "\n" + "Location: " + addressList.get(
                position).location
        /*StoreItemsActivity.customerLatitude = addressList[position].latitude
        StoreItemsActivity.customerLongitude = addressList[position].longitude*/

        if (addressList[position].inDeliveryRange == "0"){
            holder.mainLay.alpha = 0.5f
        }

        when {
            addressList[position].addressType == "1" -> {
                if (addressList[position].is_default == "1") {
                    holder.storeName.text = "Home (Default)"
                } else {
                    holder.storeName.text = "Home"
                }

                //    holder.addressTypeIcon.setImageResource(R.drawable.ic_home)

            }
            addressList[position].addressType.equals("2") -> {
                if (addressList[position].is_default.equals("1")) {
                    holder.storeName.text = "Office (Default)"
                } else {
                    holder.storeName.text = "Office"
                }
                holder.storeName.text = "Office"
                //     holder.addressTypeIcon.setImageResource(R.drawable.ic_office)
            }

            else -> {
                if (addressList[position].is_default.equals("1")) {
                    holder.storeName.text = "Other (Default)"
                } else {
                    holder.storeName.text = "Other"
                }
                holder.storeName.text = "Other"
                // holder.addressTypeIcon.setImageResource(R.drawable.ic_other)
            }
        }




//        holder.deleteIcon.setOnClickListener {
//            adapterClick.onItemClick(addressList.get(position).id, "", "","",0,"", "")
//        }


//        holder.editIcon.setOnClickListener {
//           // con.startActivity(Intent(con,AddAddressActivity::class.java).putExtra("data",Gson().toJson(addressList.get(position))))
//        }



        holder.mainLay.setOnClickListener {
            Log.e("clicked", "clicked")
            if (addressList[position].inDeliveryRange == "0"){
                Toast.makeText(con, "Store doesn't deliver to this location", Toast.LENGTH_LONG).show()

            }else{

                if (stringExtra.equals("order")) {
                    CartActivity.selectedAddressId = addressList[position].id
                    CartActivity.selectedAddressName = addressList[position].location + "\nHouse No.: " + addressList.get(position).flatNo + "\nBuilding: " + addressList.get(position).building + "\nLandmark: " + addressList.get(position).landmark
                    CartActivity.userLat = addressList[position].latitude
                    CartActivity.userLong = addressList[position].longitude
                    //StoreItemsActivity.customerLatitude = addressList[position].latitude
                    //StoreItemsActivity.customerLongitude = addressList[position].longitude
                    when {
                        addressList[position].addressType.equals("1") -> {
                            SessionTwiclo(con).userAddress = "Home"
                        }

                        addressList[position].addressType.equals("2") -> {
                            SessionTwiclo(con).userAddress = "Office"
                        }

                        else -> {
                            SessionTwiclo(con).userAddress = "Other"
                        }
                    }
                    SessionTwiclo(con).userLat = addressList[position].latitude
                    SessionTwiclo(con).userLng = addressList[position].longitude
                    SessionTwiclo(con).userAddressId = addressList[position].id
                    CartActivity.selectedAddressTitle = SessionTwiclo(con).userAddress

                    val result = Intent()
                    (con as Activity).setResult(101, result)
                    (con as Activity).finish()
                }

                if (stringExtra.equals("address")) {
                    when {
                        addressList[position].addressType.equals("1") -> {
                            SessionTwiclo(con).userAddress = "Home"
                        }

                        addressList[position].addressType.equals("2") -> {
                            SessionTwiclo(con).userAddress = "Office"

                        }

                        else -> {
                            SessionTwiclo(con).userAddress = "Other"

                        }
                    }
                    SessionTwiclo(con).userAddress = addressList[position].location
                    SessionTwiclo(con).userAddressId = addressList[position].id
                    SessionTwiclo(con).userLat = addressList[position].latitude
                    SessionTwiclo(con).userLng = addressList[position].longitude

                    //  con.startActivity(Intent(con, MainActivity::class.java))
                    (con as Activity).finish()
                }

                if (stringExtra.equals("from")) {

                    SendPackageActivity.selectedFromAddress = addressList[position].location
                    SendPackageActivity.selectedfromLat = addressList[position].latitude.toDouble()
                    SendPackageActivity.selectedfromLng= addressList[position].longitude.toDouble()
                    SendPackageActivity.fromName = addressList[position].name
                    SendPackageActivity.fromNumber = addressList[position].phone_no


                    /*CartActivity.selectedAddressName =
						addressList.get(position).location + "\nHouse No.: " + addressList.get(position).flatNo + "\nBuilding: " + addressList.get(
							position
						).building + "\nLandmark: " + addressList.get(position).building
					SessionTwiclo(con).userAddress =
						addressList.get(position).location
					SessionTwiclo(con).userAddressId =
						addressList.get(position).id*/
                    (con as Activity).finish()
                }

                if (stringExtra.equals("to")) {

                    SendPackageActivity.selectedToAddress = addressList[position].location
                    SendPackageActivity.selectedtoLat = addressList[position].latitude.toDouble()
                    SendPackageActivity.selectedtoLng= addressList[position].longitude.toDouble()
                    SendPackageActivity.toName = addressList[position].name
                    SendPackageActivity.toNumber = addressList[position].phone_no
                    (con as Activity).finish()
                }

            }

//
            //adapterClick.onItemClick(addressList.get(position).id)
//        }

        }

        holder.mainLay.setOnLongClickListener {
            if (stringExtra.equals("address")) {
                con.startActivity(Intent(con, AddAddressActivity::class.java).putExtra("data", Gson().toJson(addressList[position])))
            }

            true
        }


    }
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_address_title = view.tv_location
        var storeName = view.tv_address_title
        var addressTypeIcon = view.locationImage
        // var deleteIcon = view.deleteIcon
        //var nameText = view.nameText
         var mainLay = view.mainLay
        //var editIcon = view.editIcon
    }
}