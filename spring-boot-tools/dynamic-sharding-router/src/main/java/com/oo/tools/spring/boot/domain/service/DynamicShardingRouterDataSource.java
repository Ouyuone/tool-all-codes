package com.oo.tools.spring.boot.domain.service;

import com.oo.tools.spring.boot.supports.DBContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 17:01:17
 */
public class DynamicShardingRouterDataSource extends AbstractRoutingDataSource {
    @Value("${defaultDataSource}")
    private String defaultDatasource;

    @Override
    protected Object determineCurrentLookupKey() {

        String dbKey = DBContextHolder.getDBKey();
        if (StringUtils.isBlank(dbKey)) {
            return defaultDatasource;
        }
        return dbKey;
    }
}
