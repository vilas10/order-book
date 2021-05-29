package com.stock.orderbook.utils;

import lombok.experimental.UtilityClass;

/***
 * Utility Class for Common Utilities in the project
 *
 */
@UtilityClass
public class CommonUtil {
    public static final Integer TIMESTAMP_DOT_SEPARATOR_INDEX = 19;
    public static final String TIMESTAMP_999_MILLIS_SUFFIX = ".999Z";
    public static final String TIMESTAMP_000_MILLIS_SUFFIX = ".000Z";

    public String getCurrentSecond(String timestamp) {
        return timestamp.substring(0, TIMESTAMP_DOT_SEPARATOR_INDEX);
    }

    public String nextSecondWithMillis(String timestamp_without_millis) {
        return timestamp_without_millis + TIMESTAMP_999_MILLIS_SUFFIX;
    }
}
