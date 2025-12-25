package com.oo.tools.spring.boot.model;


import com.oo.tools.spring.boot.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/12/13 15:37:45
 */
@Data
public abstract class OrgRequest {

    /**
     * 获取组织id
     * @return
     */
    private String tenantId;

    public String getTenantId() {
        return StringUtils.isBlank(this.tenantId) ? Constants.tenantId : this.tenantId;
    }
    
}
