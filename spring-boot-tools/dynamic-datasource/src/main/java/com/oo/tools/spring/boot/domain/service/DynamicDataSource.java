package com.oo.tools.spring.boot.domain.service;

import com.oo.tools.spring.boot.support.DynamicSwitchContext;
import com.oo.tools.spring.boot.types.DynamicDatasourceLeveType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源切换
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/26 18:15:08
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String datasourceName = DynamicSwitchContext.getContext();
        if (StringUtils.isBlank(datasourceName)) {
            return DynamicDatasourceLeveType.MASTER.getCode();
        }
        return datasourceName;
    }
}
