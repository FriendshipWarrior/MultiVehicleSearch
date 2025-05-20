package com.lots.multivehiclesearch.controller

import com.lots.multivehiclesearch.models.VehicleLotRequest
import com.lots.multivehiclesearch.models.VehicleLotResponse
import com.lots.multivehiclesearch.service.VehicleLotSearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/vehicleLots")
class VehicleLotSearchController(val vehicleLotSearchService: VehicleLotSearchService) {
    @PostMapping("/search")
    fun search(@RequestBody request: List<VehicleLotRequest>): ResponseEntity<List<VehicleLotResponse>> {
        return ResponseEntity.ok(vehicleLotSearchService.search(request))
    }
}