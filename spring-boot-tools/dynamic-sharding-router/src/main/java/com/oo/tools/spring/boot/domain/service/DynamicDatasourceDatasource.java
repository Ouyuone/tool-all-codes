package com.oo.tools.spring.boot.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 17:00:47
 */
public class DynamicDatasourceDatasource extends AbstractRoutingDataSource {

    @Value("${defaultDataSource}")
    private String defaultDatasource;

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }
}
