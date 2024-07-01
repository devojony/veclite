//
// Created by devo on 2024/3/5.
//


#include "sqlite3ext.h"
SQLITE_EXTENSION_INIT1

#include "assert.h"
#include "method/l2_distance.c"


static void distanceFunc(sqlite3_context *context, int argc, sqlite3_value **argv) {
    // check that we have four arguments (lat1, lon1, lat2, lon2)
    assert(argc == 4);
    // check that all four arguments are non-null
    if (sqlite3_value_type(argv[0]) == SQLITE_NULL || sqlite3_value_type(argv[1]) == SQLITE_NULL ||
        sqlite3_value_type(argv[2]) == SQLITE_NULL || sqlite3_value_type(argv[3]) == SQLITE_NULL) {
        sqlite3_result_null(context);
        return;
    }
    // get the four argument values
    double lat1 = sqlite3_value_double(argv[0]);
    double lng1 = sqlite3_value_double(argv[1]);
    double lat2 = sqlite3_value_double(argv[2]);
    double lng2 = sqlite3_value_double(argv[3]);
    sqlite3_result_int(context, distance(lat1, lng1, lat2, lng2));
}


#ifdef _WIN32
__declspec(dllexport)
#endif

int sqlite3_veclite_init(sqlite3 *db, char **pzErrMsg, const sqlite3_api_routines *pApi){
    int rc = SQLITE_OK;
    SQLITE_EXTENSION_INIT2(pApi);
    (void)pzErrMsg;  /* Unused parameter */
    rc = sqlite3_create_function(db, "distance", 4, SQLITE_UTF8, 0, distanceFunc, 0, 0);
    return rc;
}