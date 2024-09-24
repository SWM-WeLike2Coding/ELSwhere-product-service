package com.wl2c.elswhereproductservice.global.config.redis;

public class RedisKeys {
    public static final String KEY_DELIMITER = ":";
    public static final String LIKE_KEY = "like";
    public static final String LIKE_COUNT_KEY = "likeCount";
    public static final String LIKE_USERS_KEY = "likeUsers";
    public static final String LIKE_PRODUCTS_KEY = "likeProducts";


    public static String combine(Object key1, Object key2) {
        return key1 + KEY_DELIMITER + key2;
    }

    public static String combine(Object key1, Object key2, Object key3) {
        return key1 + KEY_DELIMITER + key2 + KEY_DELIMITER + key3;
    }

    public static String combine(Object... keys) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i]);
            if (i < keys.length - 1) {
                sb.append(KEY_DELIMITER);
            }
        }
        return sb.toString();
    }
}
