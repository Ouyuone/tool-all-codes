package com.oo.tools.spring.boot.types;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/26 17:31:25
 */

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
