//
// Created by devo on 2024/7/10.
//

#ifndef VECLITE_VECTOR_H
#define VECLITE_VECTOR_H

typedef struct Vector {
    int size;
    double *data;
} Vector;

double l2(const Vector v1, const Vector v2);

double cosine_similarity(const Vector v1, const Vector v2);

double negative_inner_product_distance(const Vector v1, const Vector v2);

void vector_free(Vector *v);



#endif //VECLITE_VECTOR_H

