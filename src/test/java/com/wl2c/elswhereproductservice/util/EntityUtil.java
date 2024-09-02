package com.wl2c.elswhereproductservice.util;

public class EntityUtil {

    public static <T> void injectId(Class<T> clazz, T obj, Long id) {
        FieldReflector.inject(clazz, obj, "id", id);
    }

}
