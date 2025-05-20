package com.lots.multivehiclesearch.models

data class VehicleLotResponse (
    val location_id: String,
    val listing_ids: List<String>,
    val total_price_in_cents: Double
)