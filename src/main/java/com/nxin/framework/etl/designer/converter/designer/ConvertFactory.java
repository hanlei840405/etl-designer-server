package com.nxin.framework.etl.designer.converter.designer;

import java.util.HashMap;
import java.util.Map;

public class ConvertFactory {
    private static ThreadLocal<Map<String, Object>> variable = ThreadLocal.withInitial(() -> new HashMap<>(0));


    public static Map<String, Object> getVariable() {
        return variable.get();
    }

    public static void destroyVariable() {
        variable.remove();
    }
}
