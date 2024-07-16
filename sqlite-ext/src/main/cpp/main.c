//
// Created by devo on 2024/3/5.
//


#include "sqlite3ext.h"
#include <math.h>
#include <stdlib.h>
#include "src/utils.c"
#include <android/log.h>
#include <string.h>
#include <vector.h>

SQLITE_EXTENSION_INIT1

static void l2d_func(sqlite3_context *context, int argc, sqlite3_value **argv) {

    const unsigned char *arg1 = sqlite3_value_text(argv[0]);
    const unsigned char *arg2 = sqlite3_value_text(argv[1]);
    app_log("执行 l2 函数 %s / %s ", (const char *) arg1, (const char *) arg2);

    if (argc != 2 || arg1 == NULL || arg2 == NULL) {
        sqlite3_result_null(context);
        return;
    }

    Vector *v1 = (Vector *) malloc(sizeof(Vector));
    Vector *v2 = (Vector *) malloc(sizeof(Vector));
    v1->size = 0;
    v2->size = 0;
    v1->data = string_to_double_array((const char *) arg1, &v1->size);
    v2->data = string_to_double_array((const char *) arg2, &v2->size);

    double distance = l2(*v1, *v2);
    sqlite3_result_double(context, distance);
    app_log("L2 distance = %f", distance);
    vector_free(v1);
    vector_free(v2);
}

static void cos_distance_func(sqlite3_context *context, int argc, sqlite3_value **argv) {
    const unsigned char *arg1 = sqlite3_value_text(argv[0]);
    const unsigned char *arg2 = sqlite3_value_text(argv[1]);
    app_log("执行 cos_distance 函数 %s / %s ", (const char *) arg1, (const char *) arg2);

    if (argc != 2 || arg1 == NULL || arg2 == NULL) {
        sqlite3_result_null(context);
        return;
    }

    Vector *v1 = (Vector *) malloc(sizeof(Vector));
    Vector *v2 = (Vector *) malloc(sizeof(Vector));
    v1->size = 0;
    v2->size = 0;
    v1->data = string_to_double_array((const char *) arg1, &v1->size);
    v2->data = string_to_double_array((const char *) arg2, &v2->size);

    double distance = cosine_similarity(*v1, *v2);
    sqlite3_result_double(context, distance);
    app_log("L2 distance = %f", distance);
    vector_free(v1);
    vector_free(v2);

}

static void nip_distance_func(sqlite3_context *context, int argc, sqlite3_value **argv) {
    const unsigned char *arg1 = sqlite3_value_text(argv[0]);
    const unsigned char *arg2 = sqlite3_value_text(argv[1]);

    app_log("执行 nip distance 函数 %s / %s ", (const char *) arg1, (const char *) arg2);

    if (argc != 2 || arg1 == NULL || arg2 == NULL) {
        sqlite3_result_null(context);
        return;
    }

    Vector *v1 = (Vector *) malloc(sizeof(Vector));
    Vector *v2 = (Vector *) malloc(sizeof(Vector));
    v1->size = 0;
    v2->size = 0;
    v1->data = string_to_double_array((const char *) arg1, &v1->size);
    v2->data = string_to_double_array((const char *) arg2, &v2->size);

    double distance = negative_inner_product_distance(*v1, *v2);
    sqlite3_result_double(context, distance);
    app_log("NIP distance = %f", distance);

    size_t i = 0;
    vector_free(v1);
    vector_free(v2);

}

// SQLite扩展初始化函数
int sqlite3_veclite_init(
        sqlite3 *db,
        char **pzErrMsg,
        const sqlite3_api_routines *pApi
) {
    SQLITE_EXTENSION_INIT2(pApi);
    sqlite3_create_function(db, "l2_dis", 2, SQLITE_UTF8, 0, l2d_func, 0, 0);
    sqlite3_create_function(db, "cos_dis", 2, SQLITE_UTF8, 0, cos_distance_func, 0, 0);
    sqlite3_create_function(db, "nip_dis", 2, SQLITE_UTF8, 0, nip_distance_func, 0, 0);
    return SQLITE_OK;
}