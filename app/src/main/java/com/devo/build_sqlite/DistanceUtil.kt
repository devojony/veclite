package com.devo.build_sqlite

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceUtil {

    private fun deg2rad(degrees: Double) = degrees * 0.01745329251994329 // degrees * pi over 180


    // same to AMapUtils.calculateLineDistance
    fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Int {
        val lng1rad: Double = deg2rad(lng1)
        val lat1rad: Double = deg2rad(lat1)
        val lng2rad: Double = deg2rad(lng2)
        val lat2rad: Double = deg2rad(lat2)
        val sinLng1Rad = sin(lng1rad)
        val sinLat1Rad = sin(lat1rad)
        val cosLng1Rad = cos(lng1rad)
        val cosLat1Rad = cos(lat1rad)
        val sinLng2Rad = sin(lng2rad)
        val sinLat2Rad = sin(lat2rad)
        val cosLng2Rad = cos(lng2rad)
        val cosLat2Rad = cos(lat2rad)
        val v1 = cosLat1Rad * cosLng1Rad
        val v2 = cosLat2Rad * cosLng2Rad
        val v3 = cosLat1Rad * sinLng1Rad
        val v4 = cosLat2Rad * sinLng2Rad
        val v = sqrt(
            (v1 - v2) * (v1 - v2) + (v3 - v4) * (v3 - v4) + (sinLat1Rad - sinLat2Rad) * (sinLat1Rad - sinLat2Rad)
        )
        return (asin(v / 2.0) * 12742001.5798544).toInt()
    }

    fun l2distance(vec1: DoubleArray, vec2: DoubleArray): Double {
        if (vec1.size != vec2.size) {
            return -1.0
        }

        val n = vec1.size

        val sum = (0 until n).sumOf { i ->
            (vec1[i] - vec2[i]).pow(2)
        }
        return sqrt(sum);
    }

}