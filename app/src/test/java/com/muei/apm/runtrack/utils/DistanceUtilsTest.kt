package com.muei.apm.runtrack.utils

import com.muei.apm.runtrack.data.models.Location
import org.junit.Assert.*
import org.junit.Test

class DistanceUtilsTest {
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
        assertEquals(disAtoB, DistanceUtils.calculateDistance(pointA, pointB), delta)
        assertEquals(disBtoC, DistanceUtils.calculateDistance(pointB, pointC), delta)
        assertEquals(disCtoD, DistanceUtils.calculateDistance(pointC, pointD), delta)
    }

    @Test
    fun distanceBetweenPointsIsCommutative() {
        assertEquals(disAtoB, DistanceUtils.calculateDistance(pointB, pointA), delta)
        assertEquals(disBtoC, DistanceUtils.calculateDistance(pointC, pointB), delta)
        assertEquals(disCtoD, DistanceUtils.calculateDistance(pointD, pointC), delta)
    }

    @Test
    fun calculateDistanceBetweenSamePoint() {
        assertEquals(0.0, DistanceUtils.calculateDistance(pointA, pointA), delta)
        assertEquals(0.0, DistanceUtils.calculateDistance(pointB, pointB), delta)
        assertEquals(0.0, DistanceUtils.calculateDistance(pointC, pointC), delta)
        assertEquals(0.0, DistanceUtils.calculateDistance(pointD, pointD), delta)
    }

    @Test
    fun calculateDistanceFromOneItemList() {
        assertEquals(0.0, DistanceUtils.calculateDistance(listOf(pointA)), delta)
        assertEquals(0.0, DistanceUtils.calculateDistance(listOf(pointB)), delta)
        assertEquals(0.0, DistanceUtils.calculateDistance(listOf(pointC)), delta)
        assertEquals(0.0, DistanceUtils.calculateDistance(listOf(pointD)), delta)
    }

    @Test
    fun calculateDistanceFromList() {
        val distance = disAtoB + disBtoC + disCtoD
        assertEquals(DistanceUtils.calculateDistance(listOf(pointA, pointB, pointC, pointD)), distance, delta)
    }
}