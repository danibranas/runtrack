package com.muei.apm.runtrack.utils

import com.muei.apm.runtrack.data.models.Location
import org.junit.Assert.*
import org.junit.Test

class TrackingUtilsTest {
    private val pointA = Location(38.898556, -77.037852)
    private val pointB = Location(38.897147, -77.043934)
    private val pointC = Location(38.888910, -77.048951)
    private val pointD = Location(38.876769, -77.031522)

    private val disAtoB = 549.0
    private val disBtoC = 1014.0
    private val disCtoD = 2025.0

    private val delta = 1.0

    @Test
    fun calculateDistanceBetweenTwoPoints() {
        assertEquals(disAtoB, TrackingUtils.calculateDistance(pointA, pointB), delta)
        assertEquals(disBtoC, TrackingUtils.calculateDistance(pointB, pointC), delta)
        assertEquals(disCtoD, TrackingUtils.calculateDistance(pointC, pointD), delta)
    }

    @Test
    fun distanceBetweenPointsIsCommutative() {
        assertEquals(disAtoB, TrackingUtils.calculateDistance(pointB, pointA), delta)
        assertEquals(disBtoC, TrackingUtils.calculateDistance(pointC, pointB), delta)
        assertEquals(disCtoD, TrackingUtils.calculateDistance(pointD, pointC), delta)
    }

    @Test
    fun calculateDistanceBetweenSamePoint() {
        assertEquals(0.0, TrackingUtils.calculateDistance(pointA, pointA), delta)
        assertEquals(0.0, TrackingUtils.calculateDistance(pointB, pointB), delta)
        assertEquals(0.0, TrackingUtils.calculateDistance(pointC, pointC), delta)
        assertEquals(0.0, TrackingUtils.calculateDistance(pointD, pointD), delta)
    }

    @Test
    fun calculateDistanceFromOneItemList() {
        assertEquals(0.0, TrackingUtils.calculateDistance(listOf(pointA)), delta)
        assertEquals(0.0, TrackingUtils.calculateDistance(listOf(pointB)), delta)
        assertEquals(0.0, TrackingUtils.calculateDistance(listOf(pointC)), delta)
        assertEquals(0.0, TrackingUtils.calculateDistance(listOf(pointD)), delta)
    }

    @Test
    fun calculateDistanceFromList() {
        val distance = disAtoB + disBtoC + disCtoD
        assertEquals(TrackingUtils.calculateDistance(listOf(pointA, pointB, pointC, pointD)), distance, delta)
    }

    @Test
    fun distanceToKm() {
        assertEquals(12.0, TrackingUtils.distanceToKm(12000.0), delta)
    }

    @Test
    fun speedToPace() {
        val delta = 0.01
        val speedA = 60000.0 // m/s = 60 km/h
        assertEquals(1.0, TrackingUtils.speedToPace(speedA), delta)

        val speedB = 12000.0 // m/s
        assertEquals(5.0, TrackingUtils.speedToPace(speedB), delta)

        val speedC = 17400.0 // m/s
        assertEquals(3.45, TrackingUtils.speedToPace(speedC), delta)
    }
}