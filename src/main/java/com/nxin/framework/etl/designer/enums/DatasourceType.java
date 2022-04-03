package com.nxin.framework.etl.designer.enums;

public enum DatasourceType {
    MYSQL("mysql", "MySQL"),
    ORACLE("oracle", "ORACLE");
    private String key;

    private String type;

    DatasourceType(String key, String type) {
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
        DatasourceType[] datasourceTypes = values();
        for (DatasourceType datasourceType : datasourceTypes) {
            if (datasourceType.key().equalsIgnoreCase(key)) {
                return datasourceType.type();
            }
        }
        return null;
    }
}
