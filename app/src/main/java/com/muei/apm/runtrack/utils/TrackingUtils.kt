package com.muei.apm.runtrack.utils

import com.muei.apm.runtrack.data.models.Location

class TrackingUtils {
    companion object {
        private const val EARTH_RADIUS_KM = 6371
        private const val DELTA_M = 1

        /**
         * Calculates the distance in meters of an iterable group of geo locations.
         * @param distances distances list
         * @return distance in meters
         */
        fun calculateDistance(distances: Iterable<Location?>): Double {
            var result = 0.0

            val iterator = distances.iterator()
            if (!iterator.hasNext()) {
                return result
            }

            var lastLocation = iterator.next()

            while (iterator.hasNext()) {
                val newLocation = iterator.next()
                result += calculateDistance(lastLocation, newLocation)
                lastLocation = newLocation
            }

            return result
        }

        /**
         * Calculates the distance in meters between two geo locations.
         * @param pointA a location point
         * @param pointB another location point
         * @return distance in meters
         */
        fun calculateDistance(pointA: Location?, pointB: Location?): Double {
            if (pointA == null || pointB == null) {
                return 0.0
            }

            val lat1 = Math.toRadians(pointA.latitude)
            val lat2 = Math.toRadians(pointB.latitude)
            val lon1 = Math.toRadians(pointA.longitude)
            val lon2 = Math.toRadians(pointB.longitude)

            // Cosine method
            return Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                    Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * EARTH_RADIUS_KM * 1000
        }

        /**
         * Given two locations, calculates the speed in meters/seconds.
         */
        fun calculateSpeed(pointA: Location?, pointB: Location?): Double {
            return calculateSpeed(pointA, pointB, calculateDistance(pointA, pointB))
        }

        /**
         * Given a start point, an end point (dates are skipped) and a distance, calculates the
         * speed in meters/seconds.
         */
        fun calculateSpeed(pointA: Location?, pointB: Location?, distance: Double): Double {
            if (distance <= 0.0 || pointA == null || pointB == null || (pointB.date.time - pointA.date.time) < DELTA_M) {
                return 0.0
            }

            val time = Math.abs(pointB.date.time - pointA.date.time) / 1000 // seconds
            return distance/time
        }

        /**
         * Given a start point, an end point (dates are skipped) and a distance, calculates the
         * pace in seconds/meter.
         */
        fun calculatePace(pointA: Location?, pointB: Location?): Double {
            return speedToPace(calculateSpeed(pointA, pointB))
        }

        /**
         * Given a start point, an end point (dates are skipped) and a distance, calculates the
         * pace in seconds/meter.
         */
        fun calculatePace(pointA: Location?, pointB: Location?, distance: Double): Double {
            return speedToPace(calculateSpeed(pointA, pointB, distance))
        }

        /**
         * Converts speed, given in m/s, to pace in min/km
         */
        fun speedToPace(v: Double): Double {
            return if (v > 0.0) {
                (1 / v) * 60 * 1000
            } else {
                Double.POSITIVE_INFINITY
            }
        }

        /**
         * Converts distance, given in m, to km
         */
        fun distanceToKm(d: Double): Double {
            return d / 1000
        }
    }
}