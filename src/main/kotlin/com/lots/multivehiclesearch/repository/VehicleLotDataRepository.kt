package com.lots.multivehiclesearch.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lots.multivehiclesearch.models.VehicleLot
import org.springframework.stereotype.Repository

@Repository
class VehicleLotDataRepository {
    private val vehicleLots: List<VehicleLot>

    init {
        val inputStream = javaClass.getResourceAsStream("/listings.json")
        vehicleLots = jacksonObjectMapper().readValue(inputStream)
    }

    fun listVehicleLots(): List<VehicleLot> = vehicleLots
}
