package com.fidoo.user.cartview.model.regionmodel

data class Geometry(
    val bounds: Bounds,
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)