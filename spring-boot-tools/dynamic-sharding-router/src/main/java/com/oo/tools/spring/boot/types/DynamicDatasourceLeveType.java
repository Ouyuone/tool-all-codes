package com.oo.tools.spring.boot.types;

public enum DynamicDatasourceLeveType {
    MASTER("master"),
    SLAVE("slave");


    private String code;

    DynamicDatasourceLeveType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}