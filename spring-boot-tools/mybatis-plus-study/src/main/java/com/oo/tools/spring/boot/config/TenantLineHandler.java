package com.oo.tools.spring.boot.config;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.oo.tools.spring.boot.annotation.TenantDisable;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TenantLineHandler</p>
 *
 * @author Yu.ou
 * @desc:
 * @since: 1.0.0
 */

public class TenantLineHandler implements com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler {
    
    private static final Logger log = LoggerFactory.getLogger(TenantLineHandler.class);
    private final Map<String, Boolean> tenantCache = new ConcurrentHashMap<>();
    
    @Override
    public Expression getTenantId() {
        return new StringValue("");
    }
    
    /**
     * 判断表是否需要进行租户过滤
     *
     * @param tableName 表名
     * @return false-需要租户过滤，true-不需要
     */
    @Override
    public boolean ignoreTable(String tableName) {
        
        return this.tenantCache.computeIfAbsent(tableName, k -> {
            
            // 是否有@TenantDisable注解
            boolean hasAnnotation = TableInfoHelper.getTableInfo(tableName) == null || Objects.nonNull(
                    AnnotatedElementUtils.findMergedAnnotation(
                            TableInfoHelper.getTableInfo(tableName).getEntityType(),
                            TenantDisable.class
                    )
            
            );
            
            // 是否有getTenantIdColumn()字段
            boolean noTenantIdField = TableInfoHelper.getTableInfo(tableName) == null || !TableInfoHelper.getTableInfo(tableName).getFieldList()
                    .stream()
                    .anyMatch(field -> field.getColumn().equals(this.getTenantIdColumn()));
            
            boolean ignore = hasAnnotation || noTenantIdField;
            log.debug("判断表[{}]是否需要租户过滤: {}", tableName, ignore);
            return ignore;
        });
    }
    
    @Override
    public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
        return com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler.super.ignoreInsert(columns, tenantIdColumn);
    }
}
