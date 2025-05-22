package com.lots.multivehiclesearch.service

import com.lots.multivehiclesearch.models.VehicleLot
import com.lots.multivehiclesearch.models.VehicleLotRequest
import com.lots.multivehiclesearch.repository.VehicleLotDataRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class VehicleLotSearchServiceTest {

    private val vehicleLotDataRepository = mockk<VehicleLotDataRepository>()

    @Test
    fun `search - return empty list when repo fails`() {
        every { vehicleLotDataRepository.listVehicleLots() } returns emptyList()
        val service = VehicleLotSearchService(vehicleLotDataRepository)
        val result = service.search(emptyList())
        assertEquals(0, result.size)
    }

    @Test
    fun `search - returns set of lot2`() {
        val listings = listOf(
            VehicleLot(id = "lot1", length = 20, width = 10, location_id = "loc1", price_in_cents = 100),
            VehicleLot(id = "lot2", length = 30, width = 10, location_id = "loc1", price_in_cents = 150),
            VehicleLot(id = "lot3", length = 30, width = 10, location_id = "loc2", price_in_cents = 200)
        )
        every { vehicleLotDataRepository.listVehicleLots() } returns listings

        val service = VehicleLotSearchService(vehicleLotDataRepository)

        val request = listOf(
            VehicleLotRequest(length = 20, quantity = 1),
            VehicleLotRequest(length = 30, quantity = 1)
        )

        val results = service.search(request)

        assertTrue(results.isNotEmpty())
        val loc1 = results.find { it.location_id == "loc1" }

        assertNotNull(loc1)
        assertEquals(setOf("lot2"), loc1!!.listing_ids.toSet())
        assertEquals(150, loc1.total_price_in_cents)
    }

    @Test
    fun `search - returns set of lot1`() {
        val listings = listOf(
            VehicleLot(id = "lot1", length = 40, width = 10, location_id = "loc1", price_in_cents = 100),
            VehicleLot(id = "lot2", length = 30, width = 10, location_id = "loc1", price_in_cents = 150),
            VehicleLot(id = "lot3", length = 30, width = 10, location_id = "loc2", price_in_cents = 200)
        )
        every { vehicleLotDataRepository.listVehicleLots() } returns listings

        val service = VehicleLotSearchService(vehicleLotDataRepository)

        val request = listOf(
            VehicleLotRequest(length = 20, quantity = 1),
            VehicleLotRequest(length = 30, quantity = 1)
        )

        val results = service.search(request)

        assertTrue(results.isNotEmpty())
        val loc1 = results.find { it.location_id == "loc1" }

        assertNotNull(loc1)
        assertEquals(setOf("lot1"), loc1!!.listing_ids.toSet())
        assertEquals(100, loc1.total_price_in_cents)
    }

    @Test
    fun `search - returns empty set`() {
        val listings = listOf(
            VehicleLot(id = "lot1", length = 10, width = 10, location_id = "loc1", price_in_cents = 100),
            VehicleLot(id = "lot2", length = 10, width = 10, location_id = "loc1", price_in_cents = 150),
            VehicleLot(id = "lot3", length = 10, width = 10, location_id = "loc2", price_in_cents = 200)
        )
        every { vehicleLotDataRepository.listVehicleLots() } returns listings

        val service = VehicleLotSearchService(vehicleLotDataRepository)

        val request = listOf(
            VehicleLotRequest(length = 20, quantity = 1),
            VehicleLotRequest(length = 30, quantity = 1)
        )

        val results = service.search(request)

        assertTrue(results.isEmpty())
    }
}