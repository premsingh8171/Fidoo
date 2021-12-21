package com.fidoo.user.dashboard.listener

import com.fidoo.user.dashboard.model.newmodel.*

interface ClickEventOfDashboard {
    fun onExploreCatClick(outerPosition: Int?, innerPosition: Int?, model: Service)
    fun onCurationCatClick(outerPosition: Int?, innerPosition: Int?, model: Curation)
    fun onPackageCatClick(outerPosition: Int?, innerPosition: Int?, model: PackageCategory)
    fun onOfferCatClick(outerPosition: Int?, innerPosition: Int?, model: Offer)
    fun onShopCatClick(outerPosition: Int?, innerPosition: Int?, model: ShopCategory)
    fun onUpcomingServicesClick(outerPosition: Int?, innerPosition: Int?, model: UpcomingServices)
}