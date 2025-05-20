package com.lots.multivehiclesearch.models

data class VehicleLot (
    val id: String,
    val location_id: String,
    val length: Int,
    val width: Int,
    val price_in_cents: Double
)