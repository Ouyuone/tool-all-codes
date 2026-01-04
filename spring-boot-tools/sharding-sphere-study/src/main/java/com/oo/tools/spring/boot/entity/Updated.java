package com.oo.tools.spring.boot.entity;

import java.time.LocalDateTime;

/**
 * Updated</p>
 *
 * @author Yu.ou
 * @desc: 更新者接口
 * @since: 1.0.0
 */
public interface Updated {
    
    
    void setUpdatedBy(Long updatedBy);
    
    Long getUpdatedBy();
    
    void setUpdatedAt(LocalDateTime updatedAt);
    
    LocalDateTime getUpdatedAt();
}
