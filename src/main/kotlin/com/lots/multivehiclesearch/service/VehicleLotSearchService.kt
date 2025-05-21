package com.lots.multivehiclesearch.service

import com.lots.multivehiclesearch.models.VehicleLot
import com.lots.multivehiclesearch.models.VehicleLotRequest
import com.lots.multivehiclesearch.models.VehicleLotResponse
import com.lots.multivehiclesearch.repository.VehicleLotDataRepository
import org.springframework.stereotype.Service
import kotlin.to

@Service
class VehicleLotSearchService(val vehicleLotDataRepository: VehicleLotDataRepository) {

    fun search(request: List<VehicleLotRequest>): List<VehicleLotResponse> {
        val response = mutableListOf<VehicleLotResponse>()
        val allVehicleLotListings = vehicleLotDataRepository.listVehicleLots()
        val groupedLotListings = allVehicleLotListings.groupBy { it.location_id }

        for ((locationId, locationListings) in groupedLotListings) {
            val combinations = allListingCombinations(locationListings)

            val bestMatch = combinations
                .filter { canStoreAllVehicles(it, request) }
                .minByOrNull { it.sumOf { listing -> listing.price_in_cents } }

            if (bestMatch != null) {
                response.add(
                    VehicleLotResponse(
                        locationId,
                        bestMatch.map { it.id },
                        bestMatch.sumOf { it.price_in_cents }
                    )
                )
            }
        }

        return response.sortedBy { it.total_price_in_cents }
    }

    private fun allListingCombinations(locationListings: List<VehicleLot>): List<List<VehicleLot>> {
        val result = mutableListOf<List<VehicleLot>>()
        val n = locationListings.size
        for (i in 1 until (1 shl n)) {
            val subset = mutableListOf<VehicleLot>()
            for (j in 0 until n) {
                if ((i and (1 shl j)) != 0) {
                    subset.add(locationListings[j])
                }
            }
            result.add(subset)
        }
        return result
    }

    private fun canStoreAllVehicles(
        combination: List<VehicleLot>,
        request: List<VehicleLotRequest>
    ): Boolean {
        val availableSpots = combination.associate { listing ->
            val slots = getMaxVehicleSlotsForListing(listing)
            listing.id to slots.toMutableMap()
        }

        for (vehicle in request) {
            var remaining = vehicle.quantity
            for ((_, listingCapacities) in availableSpots) {
                val fitCount = listingCapacities[vehicle.length] ?: 0
                val used = minOf(remaining, fitCount)
                listingCapacities[vehicle.length] = fitCount - used
                remaining -= used
                if (remaining == 0) break
            }

            if (remaining > 0) {
                return false
            }
        }

        return true
    }

    private fun getMaxVehicleSlotsForListing(listing: VehicleLot): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        val length = listing.length
        val width = listing.width
        val possibleVehicleLengths = (10..maxOf(length, width) step 10)

        for (vehicleLength in possibleVehicleLengths) {
            val count1 = (length / vehicleLength) * (width / 10)
            val count2 = (width / vehicleLength) * (length / 10)
            val maxCount = maxOf(count1, count2)

            if (maxCount > 0) {
                result[vehicleLength] = maxCount
            }
        }

        return result
    }
}
