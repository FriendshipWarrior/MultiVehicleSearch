package com.lots.multivehiclesearch.service

import com.lots.multivehiclesearch.models.VehicleLot
import com.lots.multivehiclesearch.models.VehicleLotRequest
import com.lots.multivehiclesearch.models.VehicleLotResponse
import com.lots.multivehiclesearch.repository.VehicleLotDataRepository
import org.springframework.stereotype.Service

@Service
class VehicleLotSearchService(
    private val vehicleLotDataRepository: VehicleLotDataRepository
) {

    fun search(request: List<VehicleLotRequest>): List<VehicleLotResponse> {
        val lotsByLocation = vehicleLotDataRepository.listVehicleLots()
            .groupBy { it.location_id }

        return lotsByLocation.mapNotNull { (locationId, lots) ->
            val bestCombo = (1..lots.size)
                .flatMap { lots.combinations(it) }
                .filter { canStoreAllVehicles(it, request) }
                .minByOrNull { it.sumOf { lot -> lot.price_in_cents } }

            bestCombo?.let {
                VehicleLotResponse(
                    location_id = locationId,
                    listing_ids = it.map { lot -> lot.id },
                    total_price_in_cents = it.sumOf { lot -> lot.price_in_cents }
                )
            }
        }.sortedBy { it.total_price_in_cents }
    }

    private fun <T> List<T>.combinations(k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        if (k > size) return emptyList()
        return this.withIndex().flatMap { (i, value) ->
            subList(i + 1, size).combinations(k - 1).map { listOf(value) + it }
        }
    }

    private fun canStoreAllVehicles(
        lots: List<VehicleLot>,
        request: List<VehicleLotRequest>
    ): Boolean {
        val availableSpots = lots
            .sortedByDescending { it.price_in_cents }
            .associate { it.id to getMaxVehicleSlotsForListing(it).toMutableMap() }

        for (vehicle in request) {
            var remaining = vehicle.quantity
            // println("Allocating ${vehicle.quantity} vehicles of length ${vehicle.length}")

            for ((listingId, capacities) in availableSpots) {
                val available = capacities[vehicle.length] ?: 0
                val used = minOf(remaining, available)
                capacities[vehicle.length] = available - used
                remaining -= used

                // println(" - $listingId: can fit $available, used $used, remaining $remaining")
                if (remaining == 0) break
            }

            if (remaining > 0) {
                // println("Cannot store all vehicles of length ${vehicle.length}, remaining: $remaining")
                return false
            }
        }

        return true
    }

    private fun getMaxVehicleSlotsForListing(listing: VehicleLot): Map<Int, Int> {
        val (length, width) = listing.length to listing.width
        return (10..maxOf(length, width) step 10).mapNotNull { vehicleLength ->
            val slots1 = (length / vehicleLength) * (width / 10)
            val slots2 = (width / vehicleLength) * (length / 10)
            val maxSlots = maxOf(slots1, slots2)
            if (maxSlots > 0) vehicleLength to maxSlots else null
        }.toMap().also {
            // println("Listing ${listing.id} can fit: $it")
        }
    }
}
