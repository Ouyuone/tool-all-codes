package com.oo.tools.spring.boot.entity;

import java.time.LocalDateTime;

/**
 * Created</p>
 *
 * @author Yu.ou
 * @desc: 创建者接口
 * @since: 1.0.0
 */

public interface Created {
    
    void setCreatedBy(Long createdBy);
    
    Long getCreatedBy();
    
    
    void setCreatedAt(LocalDateTime createdAt);
    
    LocalDateTime getCreatedAt();
}
