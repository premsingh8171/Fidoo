package com.fidoo.user.dashboard.adapter.newadapter

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dashboard.adapter.SliderAdapterExample
import com.fidoo.user.dashboard.listener.ClickEventOfDashboard
import com.fidoo.user.dashboard.model.newmodel.*
import com.fidoo.user.data.SliderItem
import kotlinx.android.synthetic.main.item_service_details_adapter.view.*
import java.util.*
import kotlin.collections.ArrayList


class ServiceDetailsAdapter(
    val con: Context, val
    serviceList: MutableList<HomeData>,
    var clickEventOfDashboard: ClickEventOfDashboard,
    var width: Int
) :
    RecyclerView.Adapter<ServiceDetailsAdapter.UserViewHolder>() {
    var index = 0
    var serviceCategoryAdapter: ServiceCategoryAdapter? = null
    var curationsCatAdapter2: PopularCurationsCatAdapter2? = null
    var curationsCatAdapter: PopularCurationsCatAdapter? = null
    var nearByShopAdapter: NearShopCategoryAdapter? = null
    var upComingServiceAdapter: UpComingServiceAdapter? = null
    var topOfferAdapter: TopOfferAdapter? = null
    var servicescatList: ArrayList<Service>? = null
    var curationList1: ArrayList<Curation>? = null
    var curationList2: ArrayList<Curation>? = null
    private var layoutManger: GridLayoutManager? = null

    var timer: Timer? = null
    val DELAY_MS: Long = 8000 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 4000 // time in milliseconds between successive task executions.
    var currentPage = 0
    var height = 0

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_details_adapter, parent, false)
    )

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        //explore category view
        if (position == 0) {
            var catIconWidth = 0

            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 20, 0)
            holder.itemView.service_Details_Cat_rv.layoutParams = params

            servicescatList = ArrayList()
            holder.itemView.dividerOfCatView.visibility = View.GONE
            holder.itemView.sendPackagesLl.visibility = View.GONE
            holder.itemView.viewpager_banner_rv.visibility = View.GONE
            holder.itemView.service_Details_curationCat_rv.visibility = View.GONE
            holder.itemView.mainHeaderServicell.visibility = View.VISIBLE
            holder.itemView.service_cat_staticView.visibility = View.VISIBLE
            holder.itemView.mainlist.visibility = View.VISIBLE
            //y holder.itemView.bannerImgCard.visibility = View.VISIBLE
            holder.itemView.service_cat_staticView.visibility = View.VISIBLE
            holder.itemView.headerTxt.text = serviceList[position].service_heading

            if (serviceList[position].service_text.toString().isNotEmpty()) {
                holder.itemView.marque_txt.text = serviceList[position].service_text + "  |  " + serviceList[position].service_text
                holder.itemView.marque_txt.isSelected = true
                holder.itemView.marque_txt.visibility = View.VISIBLE

            } else {
                holder.itemView.marque_txt.visibility = View.GONE
            }

            if (serviceList[position].service_banner!!.isNotEmpty()) {
                holder.itemView.bannerImgCard.visibility = View.VISIBLE
                serviceList[position].service_banner?.let { loadImg(it, holder.itemView.bannerImg) }
            } else {
                holder.itemView.bannerImgCard.visibility = View.GONE
            }

            Log.e("catServices__", serviceList[0].services!!.size.toString())
            holder.itemView.header_serviceDetails.setImageResource(R.drawable.explor_cat)
            catIconWidth = (width - 160) / 4

            try {
                for (i in serviceList[position].services!!.indices) {
                    if (i == 0) {
                        holder.itemView.fruit_txt.text =
                            serviceList[position].services?.get(0)!!.service_name
                        serviceList[position].services?.get(0)
                            ?.let { loadImg(it.image, holder.itemView.fruit_img) }

                    } else if (i == 1) {
						Log.d("hygfyk",serviceList[position].services?.get(1)!!.image)
                        holder.itemView.restaurant_txt.text =
                            serviceList[position].services?.get(1)!!.service_name.replace(" ", "\n")

                        serviceList[position].services?.get(1)
                            ?.let { loadImg(it.image, holder.itemView.restaurantImg) }

                    } else if (i == 2) {
                        holder.itemView.dailyneed_txt.text =
                            serviceList[position].services?.get(2)!!.service_name.replace(" ", "\n")

                        serviceList[position].services?.get(2)
                            ?.let { loadImg(it.image, holder.itemView.dailyneed_Img) }

                        Log.e(
                            "catSerserviceListvices__",
                            serviceList[position].services?.get(2).toString()
                        )

                    } else {
                        serviceList[position].services?.get(i)?.let { servicescatList!!.add(it) }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                serviceCategoryAdapter = ServiceCategoryAdapter(
                    con,
                    servicescatList!!,
                    object :
                        ServiceCategoryAdapter.ItemClickService {
                        override fun onItemClick(pos: Int, model: Service) {
                            Log.e("model___", model.toString())
                            clickEventOfDashboard.onExploreCatClick(0, pos, model)
                        }

                    },
                    catIconWidth
                )
                holder.itemView.service_Details_Cat_rv.adapter = serviceCategoryAdapter
                layoutManger = GridLayoutManager(con, 4)
                holder.itemView.service_Details_Cat_rv?.layoutManager = layoutManger

            } catch (e: Exception) {
                e.printStackTrace()
            }

            holder.itemView.fruitsClick_constView.setOnClickListener {
                var model: Service = serviceList[position].services!!.get(0)
                clickEventOfDashboard.onExploreCatClick(0, 0, model)
            }

            holder.itemView.restaurants_constView.setOnClickListener {
                var model: Service = serviceList[position].services!!.get(1)
                clickEventOfDashboard.onExploreCatClick(0, 1, model)
            }

            holder.itemView.dailyNeed_constView.setOnClickListener {
                var model: Service = serviceList[position].services!!.get(2)
                clickEventOfDashboard.onExploreCatClick(0, 2, model)
            }

        }

        //popular curation view
        if (position == 1) {
            var catIconWidth = 0

            curationList1 = ArrayList()
            curationList2 = ArrayList()
            holder.itemView.sendPackagesLl.visibility = View.GONE
            holder.itemView.banner_with_marque_catll.visibility = View.GONE
            holder.itemView.service_cat_staticView.visibility = View.GONE
            holder.itemView.marque_txt.visibility = View.GONE
            holder.itemView.viewpager_banner_rv.visibility = View.GONE
            holder.itemView.bannerImgCard.visibility = View.GONE
            holder.itemView.dividerOfCatView.visibility = View.VISIBLE
            holder.itemView.service_Details_curationCat_rv.visibility = View.VISIBLE
            holder.itemView.mainHeaderServicell.visibility = View.VISIBLE
            holder.itemView.mainlist.visibility = View.VISIBLE
            holder.itemView.headerTxt.text = serviceList[position].curation_heading
            holder.itemView.header_serviceDetails.setImageResource(R.drawable.la_fire)
            catIconWidth = (width - 180) / 3

            try {
                var cu_val = serviceList[position].curations!!.size
                var final_val = cu_val / 2
                Log.e("final_val", final_val.toString())

                curationList1 = serviceList[position].curations as ArrayList<Curation>
//                for (i in 0 until serviceList[position].curations!!.size) {
//                    if (i < final_val) {
//                        curationList1!!.add(serviceList[position].curations?.get(i)!!)
//                    } else {
//                        curationList2!!.add(serviceList[position].curations?.get(i)!!)
//                    }
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                curationsCatAdapter = PopularCurationsCatAdapter(
                    con,
                    curationList1!!,
                    object : PopularCurationsCatAdapter.ItemClickService {
                        override fun onItemClick(pos: Int, model: Curation) {
                            clickEventOfDashboard.onCurationCatClick(1, pos, model)
                        }

                    },
                    catIconWidth
                )
                holder.itemView.service_Details_Cat_rv.adapter = curationsCatAdapter
                layoutManger = GridLayoutManager(con, 2)
                layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                holder.itemView.service_Details_Cat_rv?.layoutManager = layoutManger
            } catch (e: Exception) {
                e.printStackTrace()
            }

            holder.itemView.service_Details_curationCat_rv?.visibility = View.GONE

//            try {
//                curationsCatAdapter2 = PopularCurationsCatAdapter2(
//                    con,
//                    curationList2!!,
//                    object : PopularCurationsCatAdapter2.ItemClickService {
//                        override fun onItemClick(pos: Int, model: Curation) {
//                            clickEventOfDeshboard.onCurationCatClick(1, pos, model)
//
//                        }
//
//                    },
//                    catIconWidth
//                )
//                holder.itemView.service_Details_curationCat_rv.adapter = curationsCatAdapter2
//                layoutManger = GridLayoutManager(con, 1)
//                layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
//                holder.itemView.service_Details_curationCat_rv?.layoutManager = layoutManger
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

        }

        //send package view
        if (position == 2) {
            holder.itemView.dividerOfCatView.visibility = View.GONE
            holder.itemView.sendPackagesLl.visibility = View.VISIBLE
            holder.itemView.mainlist.visibility = View.GONE
            holder.itemView.bannerImgCard.visibility = View.GONE
            holder.itemView.mainHeaderServicell.visibility = View.GONE
            holder.itemView.mainHeaderServicell.visibility = View.GONE
            holder.itemView.sendpack_txtTop.text = "| " + serviceList[position].timing_text
            try {
                for (i in 0 until serviceList[position].package_categories!!.size) {

                    if (i == 0) {
                        holder.itemView.cat_itemTxt.text =
                            serviceList[position].package_categories?.get(0)!!.name
                        serviceList[position].package_categories?.get(0)!!.image
                            ?.let { loadImg(it, holder.itemView.cat_itemImg) }
                    }
                    if (i == 1) {
                        holder.itemView.surprise_llTxt.text =
                            serviceList[position].package_categories?.get(1)!!.name
                        serviceList[position].package_categories?.get(1)!!.image
                            ?.let { loadImg(it, holder.itemView.surpirceImg) }
                    }
                    if (i == 2) {
                        holder.itemView.medicine_lTxt.text =
                            serviceList[position].package_categories?.get(2)!!.name
                        serviceList[position].package_categories?.get(2)!!.image
                            ?.let { loadImg(it, holder.itemView.medicine_lImg) }
                    }
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            holder.itemView.send1conll.setOnClickListener {
                serviceList[position].package_categories?.get(0)?.let { it1 ->
                    clickEventOfDashboard.onPackageCatClick(
                        2, 0,
                        it1
                    )
                }
            }

            holder.itemView.send2conll.setOnClickListener {
                serviceList[position].package_categories?.get(1)?.let { it1 ->
                    clickEventOfDashboard.onPackageCatClick(
                        2, 1,
                        it1
                    )
                }
            }

            holder.itemView.send3conll.setOnClickListener {
                serviceList[position].package_categories?.get(2)?.let { it1 ->
                    clickEventOfDashboard.onPackageCatClick(
                        2, 2,
                        it1
                    )
                }
            }

            holder.itemView.booknow_txt.setOnClickListener {
                serviceList[position].package_categories?.get(2)?.let { it1 ->
                    clickEventOfDashboard.onPackageCatClick(
                        2, 3,
                        it1
                    )
                }
            }

        }

        //offer view
        if (position == 3) {
            var sliderItem = SliderItem()

            holder.itemView.service_cat_staticView.visibility = View.GONE
            holder.itemView.service_Details_curationCat_rv.visibility = View.GONE
            holder.itemView.banner_with_marque_catll.visibility = View.VISIBLE
            holder.itemView.dividerOfCatView.visibility = View.GONE
            holder.itemView.mainHeaderServicell.visibility = View.VISIBLE
            holder.itemView.sendPackagesLl.visibility = View.GONE
            holder.itemView.bannerImg.visibility = View.GONE
            holder.itemView.bannerImgCard.visibility = View.GONE
            holder.itemView.mainlist.visibility = View.VISIBLE
            holder.itemView.service_Details_Cat_rv.visibility = View.VISIBLE
            holder.itemView.viewpager_banner_rv.visibility = View.VISIBLE
            holder.itemView.headerTxt.text = serviceList[position].offer_heading


            if (serviceList[position].offer_marquee.toString().isNotEmpty()) {
                holder.itemView.marque_txt.text =
                    serviceList[position].offer_marquee + " |  " + serviceList[position].offer_marquee + " |  " + serviceList[position].offer_marquee
                holder.itemView.marque_txt.isSelected = true
                holder.itemView.marque_txt.visibility = View.VISIBLE
            } else {
                holder.itemView.marque_txt.visibility = View.GONE
            }

            //serviceList[position].offer_banner?.let { loadImg(it, holder.itemView.bannerImg) }

            holder.itemView.header_serviceDetails.setImageResource(R.drawable.offer_icon)

            //start banner code
            if (serviceList[position].offer_banners!!.isNotEmpty()) {
                holder.itemView.viewpager_banner_rv.visibility = View.VISIBLE
                height = Math.round(width * 0.49).toInt()
                holder.itemView.viewpager_banner_rv!!.layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                holder.itemView.viewpager_banner_rv!!.setClipToPadding(false)

                val sliderItemList: ArrayList<SliderItem> = ArrayList()

                for (i in 0 until serviceList[position].offer_banners!!.size) {
                    sliderItem = SliderItem()
                    sliderItem.imageUrl = serviceList[position].offer_banners!!.get(i)
                    sliderItemList.add(sliderItem)
                }

                val adapterr = SliderAdapterExample(con) {
                    Log.d("sdfdgdg", "aaya")
                }

                adapterr.renewItems(sliderItemList)
                holder.itemView.viewpager_banner_rv.adapter = adapterr
                val handler = Handler()
                val Update = Runnable {
                    if (currentPage == sliderItemList.size) {
                        currentPage = 0
                    } else {
                        currentPage++
                    }
                    holder.itemView.viewpager_banner_rv.setCurrentItem(currentPage, true)
                }
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(Update)
                    }
                }, DELAY_MS, PERIOD_MS)
            } else {
                holder.itemView.viewpager_banner_rv.visibility = View.GONE

            }
            //end banner code


            try {
                topOfferAdapter = TopOfferAdapter(
                    con,
                    serviceList[position].offers as ArrayList,
                    object : TopOfferAdapter.ItemClickShop {
                        override fun onItemClick(pos: Int, model: Offer) {
                            clickEventOfDashboard.onOfferCatClick(3, pos, model)
                        }
                    },
                    width
                )
                holder.itemView.service_Details_Cat_rv.adapter = topOfferAdapter
                layoutManger = GridLayoutManager(con, 1)
                layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                holder.itemView.service_Details_Cat_rv?.layoutManager = layoutManger
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        //near by shop view
        if (position == 4) {
            holder.itemView.sendPackagesLl.visibility = View.GONE
            holder.itemView.service_Details_curationCat_rv.visibility = View.GONE
            holder.itemView.service_cat_staticView.visibility = View.GONE
            holder.itemView.banner_with_marque_catll.visibility = View.GONE
            holder.itemView.marque_txt.visibility = View.GONE
            holder.itemView.viewpager_banner_rv.visibility = View.GONE
            holder.itemView.bannerImgCard.visibility = View.GONE
            holder.itemView.dividerOfCatView.visibility = View.VISIBLE
            holder.itemView.mainlist.visibility = View.VISIBLE
            holder.itemView.mainHeaderServicell.visibility = View.VISIBLE
            holder.itemView.headerTxt.text = serviceList[position].shop_heading
            holder.itemView.header_serviceDetails.setImageResource(R.drawable.near_shop)

            if (serviceList[position].shop_categories!!.isNotEmpty()) {
                try {
                    nearByShopAdapter = NearShopCategoryAdapter(
                        con,
                        serviceList[position].shop_categories as ArrayList,
                        object : NearShopCategoryAdapter.ItemClickShop {
                            override fun onItemClick(pos: Int, model: ShopCategory) {
                                clickEventOfDashboard.onShopCatClick(4, pos, model)
                            }
                        },
                        width
                    )
                    holder.itemView.service_Details_Cat_rv.adapter = nearByShopAdapter
                    layoutManger = GridLayoutManager(con, 2)
                    layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                    holder.itemView.service_Details_Cat_rv?.layoutManager = layoutManger
                    holder.itemView.mainlist.visibility = View.VISIBLE

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                holder.itemView.mainlist.visibility = View.GONE
            }

        }

        //up coming service view
        if (position == 5) {
            var catIconWidth = 0
            var height = 0

            holder.itemView.sendPackagesLl.visibility = View.GONE
            holder.itemView.service_Details_curationCat_rv.visibility = View.GONE
            holder.itemView.service_cat_staticView.visibility = View.GONE
            holder.itemView.banner_with_marque_catll.visibility = View.GONE
            holder.itemView.marque_txt.visibility = View.GONE
            holder.itemView.viewpager_banner_rv.visibility = View.GONE
            holder.itemView.bannerImgCard.visibility = View.GONE
            holder.itemView.dividerOfCatView.visibility = View.VISIBLE
            holder.itemView.mainlist.visibility = View.VISIBLE
            holder.itemView.mainHeaderServicell.visibility = View.VISIBLE
            holder.itemView.headerTxt.text = serviceList[position].shop_heading
            holder.itemView.header_serviceDetails.setImageResource(R.drawable.ic_upcoming)
            catIconWidth = (width - 180) / 2
            //  height = Math.round(catIconWidth * 0.49).toInt()

            try {
                upComingServiceAdapter = UpComingServiceAdapter(
                    con,
                    serviceList[position].upcoming_services as ArrayList,
                    object : UpComingServiceAdapter.ItemClickShop {
                        override fun onItemClick(pos: Int, model: UpcomingServices) {
                            clickEventOfDashboard.onUpcomingServicesClick(5, pos, model)

                        }
                    },
                    catIconWidth
                )
                holder.itemView.service_Details_Cat_rv.adapter = upComingServiceAdapter
                layoutManger = GridLayoutManager(con, 1)
                layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
                holder.itemView.service_Details_Cat_rv?.layoutManager = layoutManger
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        if (serviceList.size - 1 == position) {
            holder.itemView.bottomview_ll.visibility = View.VISIBLE
        } else {
            holder.itemView.bottomview_ll.visibility = View.GONE
        }

    }

    private fun loadImg(imageUrl: String, view: ImageView) {
        Glide.with(con)
            .load(imageUrl)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(view)
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}


    interface ItemClick {
        fun onItemClick(pos: Int, serviceList: Service)
    }
}