//
// Created by devo on 2024/3/5.
//


#include "sqlite3ext.h"
#include <math.h>
#include <stdlib.h>
#include "method/distance.c"
#include <android/log.h>
#include <string.h>

SQLITE_EXTENSION_INIT1

// 计算两个向量的L2距离

void app_log(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    __android_log_vprint(ANDROID_LOG_INFO, "veclite_extension", fmt, args);
    va_end(args);
}

// 假设字符串中的数字以逗号分隔
double *string_to_double_array(const char *str, int *count) {
    *count = 0;
    if (str == NULL || strlen(str) == 0) {
        return NULL;
    }

    const char *ptr = str;
    int num_count = 1;

    // 计算有多少个数字
    while (*ptr) {
        if (*ptr == ',') {
            num_count++;
        }
        ptr++;
    }

    // 分配double数组
    double *array = (double *) malloc(num_count * sizeof(double));
    if (array == NULL) {
        return NULL;
    }

    // 复制字符串，以免修改原始输入
    char *str_copy = (char *) malloc(strlen(str) + 1);
    if (str_copy == NULL) {
        free(array);
        return NULL;
    }
    strcpy(str_copy, str);

    // 使用strtok来分割复制的字符串
    const char *sep = ",";
    char *token = strtok(str_copy, sep);
    int index = 0;
    while (token != NULL) {
        // 将字符串转换为double
        array[index++] = strtod(token, NULL);
        token = strtok(NULL, sep);
    }

    *count = num_count;
    free(str_copy);
    return array;
}


static void l2d_func(sqlite3_context *context, int argc, sqlite3_value **argv) {

    const unsigned char *arg1 = sqlite3_value_text(argv[0]);
    const unsigned char *arg2 = sqlite3_value_text(argv[1]);
    app_log("执行 l2 函数 %s / %s ", (const char *) arg1, (const char *) arg2);

    if (argc != 2 || arg1 == NULL || arg2 == NULL) {
        sqlite3_result_null(context);
        return;
    }

    int count1 = 0;
    int count2 = 0;
    double *vec1 = string_to_double_array((const char *) arg1, &count1);
    double *vec2 = string_to_double_array((const char *) arg2, &count2);

    if (vec1 == NULL || vec2 == NULL || count1 != count2 || count1 == 0) {
        sqlite3_result_null(context);

        if (vec1) free(vec1);
        if (vec2) free(vec2);
        return;
    }

    size_t n = count1;

    const double *v1 = (const double *) vec1;
    const double *v2 = (const double *) vec2;
    double distance = l2_distance(v1, v2, n);
    sqlite3_result_double(context, distance);
    app_log("L2 distance = %f", distance);

    if (vec1) free(vec1);
    if (vec2) free(vec2);
}

static void cos_distance_func(sqlite3_context *context, int argc, sqlite3_value **argv) {
    const unsigned char *arg1 = sqlite3_value_text(argv[0]);
    const unsigned char *arg2 = sqlite3_value_text(argv[1]);
    app_log("执行cos distance 函数 %s / %s ", (const char *) arg1, (const char *) arg2);

    if (argc != 2 || arg1 == NULL || arg2 == NULL) {
        sqlite3_result_null(context);
        return;
    }

    int count1 = 0;
    int count2 = 0;
    double *vec1 = string_to_double_array((const char *) arg1, &count1);
    double *vec2 = string_to_double_array((const char *) arg2, &count2);

    if (vec1 == NULL || vec2 == NULL || count1 != count2 || count1 == 0) {
        sqlite3_result_null(context);

        if (vec1) free(vec1);
        if (vec2) free(vec2);
        return;
    }

    size_t n = count1;

    const double *v1 = (const double *) vec1;
    const double *v2 = (const double *) vec2;
    double distance = cosine_similarity(v1, v2, n);
    sqlite3_result_double(context, distance);
    app_log("cos distance = %f", distance);

    if (vec1) free(vec1);
    if (vec2) free(vec2);

}

static void nip_distance_func(sqlite3_context *context, int argc, sqlite3_value **argv) {
    const unsigned char *arg1 = sqlite3_value_text(argv[0]);
    const unsigned char *arg2 = sqlite3_value_text(argv[1]);

    app_log("执行 nip distance 函数 %s / %s ", (const char *) arg1, (const char *) arg2);

    if (argc != 2 || arg1 == NULL || arg2 == NULL) {
        sqlite3_result_null(context);
        return;
    }

    int count1 = 0;
    int count2 = 0;
    double *vec1 = string_to_double_array((const char *) arg1, &count1);
    double *vec2 = string_to_double_array((const char *) arg2, &count2);

    if (vec1 == NULL || vec2 == NULL || count1 != count2 || count1 == 0) {
        sqlite3_result_null(context);

        if (vec1) free(vec1);
        if (vec2) free(vec2);
        return;
    }

    size_t n = count1;

    const double *v1 = (const double *) vec1;
    const double *v2 = (const double *) vec2;
    double distance = negative_inner_product_distance(v1, v2, n);
    sqlite3_result_double(context, distance);
    app_log("NIP distance = %f", distance);

    if (vec1) free(vec1);
    if (vec2) free(vec2);

}

// SQLite扩展初始化函数
int sqlite3_extension_init(
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