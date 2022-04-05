package com.nxin.framework.etl.designer.enums;

public enum CronType {
    DAY("day", "0 0 16 * * ? *"),
    WEEK("week", "0 0 16 ? * L"),
    MONTH("month", "0 0 16 L * ?"),
    YEAR("year", "0 0 16 L 12 ?"),
    MANUAL("manual", null);
    private String key;

    private String type;

    CronType(String key, String type) {
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
        CronType[] datasourceTypes = values();
        for (CronType datasourceType : datasourceTypes) {
            if (datasourceType.key().equalsIgnoreCase(key)) {
                return datasourceType.type();
            }
        }
        return null;
    }
}
