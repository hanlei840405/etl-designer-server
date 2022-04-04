package com.nxin.framework.etl.designer.enums;

import java.util.Calendar;

public enum DateFormatType {
    DAY("day", "_yyyyMMdd"),
    MONTH("month", "_yyyyMM"),
    YEAR("year", "_yyyy"),
    WEEK("week", null);
    private String key;

    private String type;

    DateFormatType(String key, String type) {
        this.key = key;
        this.type = type;
    }

    private String key() {
        return this.key;
    }

    private String type() {
        return this.type;
    }

    public static String getValue(String key) {
        DateFormatType[] datasourceTypes = values();
        for (DateFormatType datasourceType : datasourceTypes) {
            if (datasourceType.key().equalsIgnoreCase(key)) {
                return datasourceType.type();
            }
        }
        return null;
    }
}
