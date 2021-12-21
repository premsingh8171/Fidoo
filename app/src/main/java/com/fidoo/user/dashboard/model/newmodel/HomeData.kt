package com.fidoo.user.dashboard.model.newmodel

data class HomeData(
    val curation_heading: String?="",
    val curations: List<Curation>?=null,
    val key: String?="",
    val offer_banner: String?="",
    val offer_heading: String?="",
    val offers: List<Offer>?=null,
    val offer_banners: List<String>?=null,
    val package_categories: List<PackageCategory>?=null,
    val package_heading: String?="",
    val service_banner: String?="",
    val service_text: String?="",
    val service_heading: String?="",
    val services: List<Service>?=null,
    val shop_categories: List<ShopCategory>?=null,
    val upcoming_services: List<UpcomingServices>?=null,
    val shop_heading: String?="",
    val offer_marquee: String?="",
    val timing_text: String?=""
)