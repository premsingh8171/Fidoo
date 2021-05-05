package com.fidoo.user.adapter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.data.model.CustomListModel
import com.fidoo.user.data.model.CustomizeProductResponseModel
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.interfaces.CustomCartAddRemoveClick
import kotlinx.android.synthetic.main.store_customization_adapter.view.*


class StoreCustomItemsAdapter(
        val con: Context,
        val category: MutableList<CustomizeProductResponseModel.Category>,
        val customCartAddRemoveClick: CustomCartAddRemoveClick,
        val categoryy: ArrayList<CustomListModel>?,
        val adapterCustomRadioClick: AdapterCustomRadioClick
) :
    RecyclerView.Adapter<StoreCustomItemsAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.store_customization_adapter, parent, false)
    )

    override fun getItemCount() = category.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        Log.d("dsdsddsds___",category.get(position).subCat.size.toString())
        if(category[position].isMandatory.equals("0")) {
            holder.catName.text = category[position].catName
        } else{
            holder.catName.text = category[position].catName+"*"
        }


//        val radioGroup = RadioGroup(con)
//        val params = RelativeLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
       // params.setMargins(65, 0, 65, 0)

      //  radioGroup.layoutParams = params
        if (category[position].isMultiple.equals("0")) {


          //  radioGroup.tag= category[position].catId.toInt()
            // radioGroup.id=category.get(position).catId.toInt()
            holder.descriptionTxt.text="Please select any one option"

            val adapter = StoreSubCustomItemsAdapter(
                    con,
                    category[position].maxSelectionCount,
                    category[position].subCat,
                    customCartAddRemoveClick,"RadioGroup",
                    object :AdapterCustomRadioClick{
                        override fun onCustomRadioClick(checkedId: String?, position: String?) {
                            adapterCustomRadioClick.onCustomRadioClick(checkedId,position)
                        }
                    }
            )
            holder.subItemsRecyclerview.layoutManager = LinearLayoutManager(con)
            holder.subItemsRecyclerview.setHasFixedSize(true)
            holder.subItemsRecyclerview.adapter = adapter

//            for (i in 0 until category.get(position).subCat.size) {
//                val geek2 = RadioButton(con)
//                geek2.layoutParams = RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//                geek2.gravity=Gravity.LEFT
//                geek2.layoutDirection=View.LAYOUT_DIRECTION_RTL
//                geek2.text = category[position].subCat[i].subCatName+" ("+con.resources.getString(R.string.ruppee)+ category[position].subCat[i].price+")" ////setting text of second radio button
//                geek2.id = category[position].subCat[i].id.toInt()
//                // Create RadioGroup Dynamically
//                //adding button to the radio group container
//                radioGroup.addView(geek2)
//
//            }
//            if(category[position].isMandatory.equals("0")) { }
//            else { radioGroup.check(category[position].subCat[0].id.toInt()) }
//            holder.layoutt.addView(radioGroup)


        } else {
            holder.descriptionTxt.text="Please select options"
            //  layout.addView(radioGroup)
            Log.d("edff0", category[position].subCat.size.toString())
            val adapter = StoreSubCustomItemsAdapter(
                con,
                category[position].maxSelectionCount,
                category[position].subCat,
                customCartAddRemoveClick,"CheckBox",object :AdapterCustomRadioClick{
                override fun onCustomRadioClick(checkedId: String?, position: String?) {
                   // adapterCustomRadioClick.onCustomRadioClick(checkedId,position)
                }
            }
            )
            holder.subItemsRecyclerview.layoutManager = LinearLayoutManager(con)
            holder.subItemsRecyclerview.setHasFixedSize(true)
            holder.subItemsRecyclerview.adapter = adapter
        }

        // holder.subItemsRecyclerview.adapter = adapter
      //  radioGroup.setOnCheckedChangeListener { group, checkedId ->
        //    adapterCustomRadioClick.onCustomRadioClick(checkedId.toString(),position.toString())
/*

            var tempCat: String? =""
    for (i in 0..category.size-1)
    {

        for(j in 0..category.get(i).subCat.size-1)
        {

            if(checkedId==category.get(i).subCat.get(j).id.toInt())
            {
                tempCat=category.get(i).catId

                break
            }
            if(!tempCat.equals(""))
            {
                break
            }

        }

    }
var tempAddEdit: String?="add"
var tempAddEditId: String?="add"
            for (i in 0..categoryy!!.size-1) {

                if(categoryy.get(i).category.equals(tempCat))
                {
                  */
/*  var customListModel: CustomListModel?= CustomListModel()
                    customListModel!!.category= category.get(i).catId
                    customListModel!!.id= category.get(i).subCat.get(0).id.toInt()*//*

                    tempAddEdit="edit"
                    tempAddEditId=i.toString()
                   categoryy!!.get(i).id=checkedId
                    break
                }


            }


            if(tempAddEdit.equals("edit"))
            {

                categoryy!!.get(tempAddEditId!!.toInt()).id=checkedId

            }
            else
            {

                var customListModel: CustomListModel?= CustomListModel()
                customListModel!!.category= tempCat
                customListModel!!.id= checkedId
                categoryy!!.add(customListModel)

            }
*/

     //   }



     /*  customAddBtn.setOnClickListener {
            Log.e("selected radio",Gson().toJson(categoryy))
            //Log.e("selected check",)

        }*/

    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var subItemsRecyclerview = view.subItemsRecyclervieww
        var catName = view.catName
        var descriptionTxt = view.descriptionTxt
        var layoutt = view.layoutt
    }
}