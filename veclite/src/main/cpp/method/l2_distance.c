//
// Created by devo on 2024/3/5.
//

#include "math.h"

// degrees * pi over 180
#define DEG2RAD(degrees) (degrees * 0.01745329251994329) // degrees * pi over 180

// same to AMapUtils.calculateLineDistance
static int distance(double lat1, double lng1, double lat2, double lng2) {
    double lng1rad = DEG2RAD(lng1);
    double lat1rad = DEG2RAD(lat1);
    double lng2rad = DEG2RAD(lng2);
    double lat2rad = DEG2RAD(lat2);

    double sinLng1Rad = sin(lng1rad);
    double sinLat1Rad = sin(lat1rad);
    double cosLng1Rad = cos(lng1rad);
    double cosLat1Rad = cos(lat1rad);

    double sinLng2Rad = sin(lng2rad);
    double sinLat2Rad = sin(lat2rad);
    double cosLng2Rad = cos(lng2rad);
    double cosLat2Rad = cos(lat2rad);

    double v1 = cosLat1Rad * cosLng1Rad;
    double v2 = cosLat2Rad * cosLng2Rad;
    double v3 = cosLat1Rad * sinLng1Rad;
    double v4 = cosLat2Rad * sinLng2Rad;
    double v =  sqrt((v1 - v2) * (v1 - v2) +
                     (v3 - v4) * (v3 - v4) +
                     (sinLat1Rad - sinLat2Rad) * (sinLat1Rad - sinLat2Rad)
    );

    return asin(v / 2.0) * 12742001.5798544;
}

