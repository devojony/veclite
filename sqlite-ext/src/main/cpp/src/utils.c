//
// Created by devo on 2024/7/10.
//

#include <stdlib.h>
#include <android/log.h>
#include <string.h>

static void app_log(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    __android_log_vprint(ANDROID_LOG_INFO, "veclite_extension", fmt, args);
    va_end(args);
}

// 假设字符串中的数字以逗号分隔
static double *string_to_double_array(const char *str, int *count) {
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
