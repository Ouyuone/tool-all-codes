package com.oo.tools.spring.boot.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

/**
 * 分页请求对象
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2025/01/09 09:46:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PageRequest extends PageParam {

    public static final int DEFAULT_PAGE_SIZE = 20;
    
    private OrgRequest orgRequest;
    
    public PageRequest() {
        super(1, 10);
        this.orgRequest = new OrgRequest() {
            @Override
            public void setTenantId(String tenantId) {
                super.setTenantId(tenantId);
            }
        };
    }
    
    public String getTenantId() {
        return orgRequest.getTenantId();
    }
    
    public void setOrgId(String tenantId) {
        this.orgRequest = new OrgRequest() {
            @Override
            public void setTenantId(String tenantId) {
                super.setTenantId(tenantId);
            }
        };
        orgRequest.setTenantId(tenantId);
    }

    /**
     * 转换成mybatis plus的page对象，用于支持selectPage()方法
     * @return 返回mybatis plus的page对象
     */
    public <T> Page<T> toPage() {
        return Page.of(getPageNum(), getPageSize(), true);
    }

    public <T> Page<T> toPage(boolean searchCount) {
        return Page.of(getPageNum(), getPageSize(), searchCount);
    }

    /**
     * 转换成响应前端的分页对象
     **/
    public <T> PageInfo<T> toPageInfo() {
        return toPageInfo(Collections.emptyList(), 0L);
    }

    /**
     * 转换成响应前端的分页对象
     **/
    public <T> PageInfo<T> toPageInfo(List<T> data, long total) {
        PageInfo<T> pageInfo = PageInfo.of(data);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(getPageNum());
        pageInfo.setPageSize(getPageSize());
        return pageInfo;
    }

    @Override
    public Integer getPageNum() {
        return super.getPageNum() == null || super.getPageNum() < 1 ? 1 : super.getPageNum();
    }

    @Override
    public Integer getPageSize() {
        return super.getPageSize() == null || super.getPageSize() < 1 ? DEFAULT_PAGE_SIZE : super.getPageSize();
    }
    
}
