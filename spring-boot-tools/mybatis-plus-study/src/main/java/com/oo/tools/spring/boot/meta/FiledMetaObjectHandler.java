package com.oo.tools.spring.boot.meta;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.oo.tools.spring.boot.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * FiledMetaObjectHandler
 *
 * @author Yu.ou
 * @date: 2025/05/15 14:12:32
 * @desc: 基础字段自动填充
 * @since: 1.0.0
 */
public class FiledMetaObjectHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        String userId = Constants.userId;
        
        String tenantId = Constants.tenantId;
        
        Long userIdLong = StringUtils.isBlank(userId) ? null : NumberUtils.isCreatable(userId) ? NumberUtils.createLong(userId) : null;
        
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createdBy", String.class, userId);
        this.strictInsertFill(metaObject, "createdBy", Long.class, userIdLong);
        this.strictInsertFill(metaObject, "updatedBy", String.class, userId);
        this.strictInsertFill(metaObject, "updatedBy", Long.class, userIdLong);
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "deleted", Boolean.class, Boolean.FALSE);
        this.strictInsertFill(metaObject, "tenantId", String.class, tenantId);
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        String userId = Constants.userId;
        this.strictUpdateFill(metaObject, "updatedBy", String.class, userId);
        this.strictUpdateFill(metaObject, "updatedBy", Long.class, StringUtils.isBlank(userId) ? null : NumberUtils.isCreatable(userId) ? NumberUtils.createLong(userId) : null);
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
    
    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        
        if (StringUtils.equalsAny(fieldName, "updatedBy", "updatedAt") || Objects.isNull(metaObject.getValue(fieldName))) {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }
        
        return this;
    }
    
}