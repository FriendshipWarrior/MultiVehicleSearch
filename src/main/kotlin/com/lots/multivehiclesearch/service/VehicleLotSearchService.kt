package com.lots.multivehiclesearch.service

import com.lots.multivehiclesearch.models.VehicleLotRequest
import com.lots.multivehiclesearch.models.VehicleLotResponse
import com.lots.multivehiclesearch.repository.VehicleLotDataRepository
import org.springframework.stereotype.Service

@Service
class VehicleLotSearchService(val vehicleLotDataRepository: VehicleLotDataRepository) {

    fun search(request: List<VehicleLotRequest>): List<VehicleLotResponse> {
        val vehicleLots = vehicleLotDataRepository.listVehicleLots()

        return listOf()
    }
}
