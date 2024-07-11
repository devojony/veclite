//
// Created by devo on 2024/3/5.
//

#include "math.h"
#include "vector.h"
#include "stdlib.h"
// 计算两个向量之间的L2距离
double l2(const Vector v1, const Vector v2) {

    if (v1.size != v2.size) {
        return -1.0;
    }

    double sum = 0.0;

    const int size = v1.size;

    const double *vec1 = v1.data;
    const double *vec2 = v2.data;

    for (int i = 0; i < size; i++) {
        sum += pow(vec1[i] - vec2[i], 2);
    }

    return sqrt(sum);
}

// 计算两个向量之间的余弦相似度
double cosine_similarity(const Vector v1, const Vector v2) {

    if (v1.size != v2.size) {
        return -1.0;
    }

    const int size = v1.size;

    const double *vec1 = v1.data;
    const double *vec2 = v2.data;

    double dot_product = 0.0; // 点积
    double norm1 = 0.0;       // 向量1的范数
    double norm2 = 0.0;       // 向量2的范数

    // 计算点积和每个向量的范数
    for (int i = 0; i < size; ++i) {
        dot_product += vec1[i] * vec2[i];
        norm1 += pow(vec1[i],2);
        norm2 += pow(vec2[i],2);
    }

    // 计算范数的平方根
    norm1 = sqrt(norm1 + 1e-9); // 增加1e-9以获得数值稳定性
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
double negative_inner_product_distance(const Vector v1, const Vector v2) {
    if (v1.size != v2.size) {
        return 0.0;
    }

    const int size = v1.size;

    const double *vec1 = v1.data;
    const double *vec2 = v2.data;

    double inner_product = 0.0; // 内积

    // 计算内积
    for (int i = 0; i < size; ++i) {
        inner_product += vec1[i] * vec2[i];
    }

    // 返回负内积距离
    return -inner_product;
}


void vector_free(Vector *v){
    free(v->data);
    free(v);
}