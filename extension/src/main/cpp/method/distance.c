//
// Created by devo on 2024/3/5.
//

#include "math.h"

// 计算两个向量之间的L2距离
static double l2_distance(const double *vec1, const double *vec2, int n) {
    double sum = 0.0;
    for (int i = 0; i < n; i++) {
        sum += pow(vec1[i] - vec2[i], 2);
    }
    return sqrt(sum);
}

// 计算两个向量之间的余弦相似度
static double cosine_similarity(const double *vec1, const double *vec2, int n) {
    double dot_product = 0.0; // 点积
    double norm1 = 0.0;       // 向量1的范数
    double norm2 = 0.0;       // 向量2的范数

    // 计算点积和每个向量的范数
    for (int i = 0; i < n; ++i) {
        dot_product += vec1[i] * vec2[i];
        norm1 += pow(vec1[i],2);
        norm2 += pow(vec2[i],2);
    }

    // 计算范数的平方根
    norm1 = sqrt(norm1);
    norm2 = sqrt(norm2);

    // 如果任一向量的范数为0，则余弦相似度无法计算
    if (norm1 == 0 || norm2 == 0) {
        return 0.0; // 或者返回其他错误标记
    }

    // 计算余弦相似度
    double cosine_sim = dot_product / (norm1 * norm2);
    return cosine_sim;
}

// 计算两个向量之间的负内积距离
static double negative_inner_product_distance(const double *vec1, const double *vec2, int n) {
    double inner_product = 0.0; // 内积

    // 计算内积
    for (int i = 0; i < n; ++i) {
        inner_product += vec1[i] * vec2[i];
    }

    // 返回负内积距离
    return -inner_product;
}
