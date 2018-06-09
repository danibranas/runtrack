package com.muei.apm.runtrack.utils

import com.muei.apm.runtrack.data.models.Location
import org.junit.Assert.*
import org.junit.Test
import java.util.*

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
    fun calculateSpeed() {
        val calendar = Calendar.getInstance()
        val now = calendar.time.clone() as Date

        calendar.add(Calendar.HOUR, -1)
        val before = calendar.time.clone() as Date

        val pointA = Location(46.89803, 2.60693, before)
        val pointB = Location(46.90266, 2.61826, now)

        // There's ~1km from A to B
        assertEquals(1000.0/3600, TrackingUtils.calculateSpeed(pointA, pointB), 0.001)
    }

    @Test
    fun calculatePace() {
        val calendar = Calendar.getInstance()
        val now = calendar.time.clone() as Date

        calendar.add(Calendar.HOUR, -1)
        val before = calendar.time.clone() as Date

        val pointA = Location(46.89803, 2.60693, before)
        val pointB = Location(46.90266, 2.61826, now)
        val pace = TrackingUtils.calculatePace(pointA, pointB)

        // There's ~1km from A to B
        assertEquals(TrackingUtils.speedToPace(1000.0/3600), pace, 0.2)
    }

    @Test
    fun speedToPace() {
        val delta = 0.001

        val speedA = 2.77778 // m/s = 60 km/h
        assertEquals(6.0, TrackingUtils.speedToPace(speedA), delta)

        val speedB = 3.33333333 // m/s
        assertEquals(5.0, TrackingUtils.speedToPace(speedB), delta)

        val speedC = 5.55556 // m/s
        assertEquals(3.0, TrackingUtils.speedToPace(speedC), delta)
    }
}