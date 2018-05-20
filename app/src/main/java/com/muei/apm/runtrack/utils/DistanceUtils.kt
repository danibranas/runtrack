package com.muei.apm.runtrack.utils

import com.muei.apm.runtrack.data.models.Location

class DistanceUtils {
    companion object {
        private const val EARTH_RADIUS_KM = 6371

        /**
         * Calculates the distance in meters of an iterable group of geo locations.
         * @param distances distances list
         * @return distance in meters
         */
        fun calculateDistance(distances: Iterable<Location>): Double {
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
        fun calculateDistance(pointA: Location, pointB: Location): Double {
            val lat1 = Math.toRadians(pointA.latitude)
            val lat2 = Math.toRadians(pointB.latitude)
            val lon1 = Math.toRadians(pointA.longitude)
            val lon2 = Math.toRadians(pointB.longitude)

            // Cosine method
            return Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                    Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2-lon1)) * EARTH_RADIUS_KM * 1000
        }
    }
}