package com.fidoo.user.grocery.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.adapter.GroceryItemAdapter
import com.fidoo.user.grocery.adapter.GrocerySubItemAdapter
import com.fidoo.user.grocery.model.getGroceryProducts.Category
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.model.getGroceryProducts.Subcategory
import com.fidoo.user.grocery.viewmodel.GroceryProductsViewModel
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.select_cat_popup.*
import org.w3c.dom.Text

class GroceryItemsActivity : AppCompatActivity() {
    var viewmodel: GroceryProductsViewModel? = null
    lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_items)
        viewmodel = ViewModelProviders.of(this).get(GroceryProductsViewModel::class.java)
        recyclerView = findViewById(R.id.grocery_item_rv)
        val store_id = intent.getStringExtra("storeId")

        viewmodel?.getGroceryProductsFun(
            SessionTwiclo(this).loggedInUserDetail.accountId, SessionTwiclo(
                this
            ).loggedInUserDetail.accessToken, store_id
        )

        viewmodel?.GroceryProductsResponse?.observe(this, Observer { grocery ->
            dismissIOSProgress()

            if (!grocery.error) {
                val catList: ArrayList<Category> = ArrayList()
                val subcatList: ArrayList<Subcategory> = ArrayList()
                val productList: ArrayList<Product> = ArrayList()

                for (i in 0 until grocery.category.size) {
                    var catObj = grocery.category[i]

                    for (j in 0 until grocery.category[i].subcategory.size) {
                        var subCatObj = grocery.category[i].subcategory[j]

                        for (k in 0 until grocery.category[i].subcategory[j].product.size) {
                            var productListObj = grocery.category[i].subcategory[j].product[k]
                            productList.add(productListObj)
                        }

                        subcatList.add(subCatObj)
                    }

                    catList.add(catObj)
                }



                Log.d("kb___", "" + productList[0].company_name)

                rvlistSubcategory(subcatList)
                rvlistProduct(productList)

            }


        })

        viewmodel?.failureResponse?.observe(this, Observer {

        })
    }

    private fun catPopUp() {
       val selectAreaDiolog = Dialog(this)
        selectAreaDiolog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectAreaDiolog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selectAreaDiolog.setContentView(R.layout.select_cat_popup)
        selectAreaDiolog.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        selectAreaDiolog.getWindow()?.getAttributes()?.windowAnimations = R.style.diologIntertnet
        selectAreaDiolog.setCanceledOnTouchOutside(true)
        selectAreaDiolog.show()
       // catRecyclerview
        txtError.setOnClickListener(View.OnClickListener {
            selectAreaDiolog.dismiss() })
    }

    private fun rvlistProduct(listProduct: ArrayList<Product>) {

        grocery_item_rv.adapter = GroceryItemAdapter(
            this,
            listProduct,
            object : GroceryItemAdapter.GroceryItemClick {

                override fun onItemClick(pos: Int, grocery: Product) {
                    TODO("Not yet implemented")
                }

                override fun onItemAdd(pos: Int, itemcount: Int, grocery: Product) {
                    TODO("Not yet implemented")
                }

                override fun onItemSub(pos: Int, itemcount: Int, grocery: Product) {
                    TODO("Not yet implemented")
                }
            })


    }

    private fun rvlistSubcategory(subcatList: ArrayList<Subcategory>) {

        sub_cat_rv.adapter = GrocerySubItemAdapter(
            this,
            subcatList,
            object : GrocerySubItemAdapter.SubcategoryItemClick {

                override fun onItemClick(pos: Int, grocery: Subcategory) {
                    Log.d("grocery___", grocery.subcategory_name)
                }
            })


    }
}