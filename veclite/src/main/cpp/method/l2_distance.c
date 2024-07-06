//
// Created by devo on 2024/3/5.
//

#include <stddef.h>
#include "math.h"

// 计算两个向量之间的L2距离
static double l2d(const double *vec1, const double *vec2, size_t n) {
    double sum = 0.0;
    for (int i = 0; i < n; i++) {
        sum += pow(vec1[i] - vec2[i],2);
    }
    return sqrt(sum);
}

