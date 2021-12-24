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
import com.fidoo.user.addressmodule.activity.NewAddAddressActivity
import com.fidoo.user.addressmodule.activity.SavedAddressesActivity
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.profile.ui.ProfileFragment
import com.fidoo.user.sendpackages.activity.SendPackageActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.saved_address_items.view.*


class AddressesAdapter(
    val con: Context,
    val addressList: MutableList<GetAddressModel.AddressList>,
    val setOnDeteleAddListener: SetOnDeteleAddListener,
    val stringExtra: String?
) : RecyclerView.Adapter<AddressesAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_address_items, parent, false)
    )

    override fun getItemCount() = addressList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
     //holder.tv_address_title.text = "House No.: " + addressList.get(position).flatNo + "\nBuilding: " + addressList[position].building + "\nLandmark: " + addressList.get(position).landmark + "\n" + "Location: " + addressList.get(position).location

     holder.tv_address_title.text = addressList.get(position).flatNo + ", " + addressList.get(position).landmark + ", " + addressList.get(position).location

        if (ProfileFragment.addManages.equals("add_manage")) {
            holder.itemView.deleteAdd.visibility = View.VISIBLE
            holder.itemView.editAdd.visibility = View.VISIBLE
            holder.itemView.tv_yourAddresses.visibility = View.GONE

            holder.itemView.setassDefaultTxt.visibility = View.VISIBLE
            holder.itemView.selectedadd_img.visibility = View.VISIBLE

            holder.itemView.deleteAdd.setOnClickListener {
                setOnDeteleAddListener.onDelete(
                    addressList.get(position).id,
                    addressList.get(position)
                )
            }

            if (SessionTwiclo(con).userAddressId.equals(addressList[position].id)) {
                holder.itemView.setassDefaultTxt.visibility = View.VISIBLE
                holder.itemView.selectedadd_img.visibility = View.VISIBLE
                holder.itemView.selectedadd_img.setImageResource(R.drawable.filter_on)

            } else {
                holder.itemView.setassDefaultTxt.visibility = View.GONE
                holder.itemView.selectedadd_img.visibility = View.VISIBLE
            }

        }else {
            holder.itemView.deleteAdd.visibility = View.GONE
            holder.itemView.editAdd.visibility = View.GONE
            holder.itemView.setassDefaultTxt.visibility = View.GONE
            holder.itemView.setassDefaultTxt.visibility = View.GONE
            holder.itemView.selectedadd_img.visibility = View.GONE

            if (position == 0) {
                holder.itemView.tv_yourAddresses.visibility = View.VISIBLE
            } else {
                holder.itemView.tv_yourAddresses.visibility = View.GONE
            }
        }

//        if (position==0){
//           holder.itemView.tv_yourAddresses.visibility=View.VISIBLE
//        }else{
//            holder.itemView.tv_yourAddresses.visibility=View.GONE
//        }

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
                           /// SessionTwiclo(con).userAddress = "Home"
                            CartActivity.selectedAddressTitle= "Home"
                        }

                        addressList[position].addressType.equals("2") -> {
                            CartActivity.selectedAddressTitle= "Office"

                           // SessionTwiclo(con).userAddress = "Office"
                        }

                        else -> {
                            // SessionTwiclo(con).userAddress = "Other"
                            CartActivity.selectedAddressTitle = "Other"

                        }
                    }
                    SessionTwiclo(con).userLat = addressList[position].latitude
                    SessionTwiclo(con).userLng = addressList[position].longitude
                    SessionTwiclo(con).userAddressId = addressList[position].id
                    SessionTwiclo(con).userAddress = addressList.get(position).location
                    // holder.itemView.setassDefaultTxt.visibility=View.VISIBLE
                    //  holder.itemView.selectedadd_img.visibility=View.VISIBLE
                    //   holder.itemView.selectedadd_img.setImageResource(R.drawable.filter_on)
                    val result = Intent()
                    (con as Activity).setResult(101, result)
                    (con as Activity).finish()
                } else if (stringExtra.equals("address")) {
                    Log.e("clickedaddress", "address")
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
                    // holder.itemView.setassDefaultTxt.visibility=View.VISIBLE
                    //  holder.itemView.selectedadd_img.visibility=View.VISIBLE
                    // holder.itemView.selectedadd_img.setImageResource(R.drawable.filter_on)

                    SessionTwiclo(con).userAddress = addressList[position].location
                    SessionTwiclo(con).userAddressId = addressList[position].id
                    SessionTwiclo(con).userLat = addressList[position].latitude
                    SessionTwiclo(con).userLng = addressList[position].longitude
                    (con as Activity).finish()
                } else if (ProfileFragment.addManages.equals("add_manage")) {
                    Log.e("clickedaddress", "address")
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

                     holder.itemView.setassDefaultTxt.visibility=View.VISIBLE
                     holder.itemView.selectedadd_img.visibility=View.VISIBLE
                     holder.itemView.selectedadd_img.setImageResource(R.drawable.filter_on)

                    SessionTwiclo(con).userAddress = addressList[position].location
                    SessionTwiclo(con).userAddressId = addressList[position].id
                    SessionTwiclo(con).userLat = addressList[position].latitude
                    SessionTwiclo(con).userLng = addressList[position].longitude
                    (con as Activity).finish()
                }

                if (stringExtra.equals("from")) {

                    SendPackageActivity.selectedFromAddress = addressList[position].location
                    SendPackageActivity.selectedfromLat = addressList[position].latitude.toDouble()
                    SendPackageActivity.selectedfromLng= addressList[position].longitude.toDouble()
                    SendPackageActivity.fromName = addressList[position].name
                    SendPackageActivity.fromNumber = addressList[position].phone_no
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


        }

       // Log.d("ewwewew",stringExtra.equals("address").toString())

        holder.itemView.editAdd.setOnClickListener {
            if (addressList[position].phone_no.toString().equals("")||addressList[position].name.toString().equals("")){
                SavedAddressesActivity.addAddressOrNot ="current_location"
            }else{
                SavedAddressesActivity.addAddressOrNot ="new_add"
            }

           // Log.d("ewwewew",addressList[position].phone_no.toString())
           // if (stringExtra.equals("address")||stringExtra.equals("from")||stringExtra.equals("to")) {
              con.startActivity(Intent(con, NewAddAddressActivity::class.java).putExtra("data", Gson().toJson(addressList[position])))
        //    }

         //   true
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

    interface SetOnDeteleAddListener{
        fun onDelete(add_id:String,addressList: GetAddressModel.AddressList)
    }
}