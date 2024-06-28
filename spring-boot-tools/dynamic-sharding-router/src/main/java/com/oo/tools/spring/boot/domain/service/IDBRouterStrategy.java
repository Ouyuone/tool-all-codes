package com.oo.tools.spring.boot.domain.service;

import com.oo.tools.spring.boot.supports.DBRouterConfig;
import com.oo.tools.spring.boot.supports.StringUtils;

import java.util.Map;

/**
 *
    哈希路由
 */
public interface IDBRouterStrategy {

    /**
     * 路由计算
     * @param dbKeyAttr 路由字段
     * @param dynamicDBKey 动态路由字段key
     */
    void doRouter(String dbKeyAttr, String dynamicDBKey);

    /**
     * 手动设置分库路由
     *
     * @param dbIdx 路由库，需要在配置范围内
     */
    void setDBKey(int dbIdx,String dynamicDBKey);

    /**
     * 手动设置分表路由
     *
     * @param tbIdx 路由表，需要在配置范围内
     */
    void setTBKey(int tbIdx);

    /**
     * 获取分库数
     *
     * @return 数量
     */
    int dbCount(String dynamicDBKey);

    /**
     * 获取分表数
     *
     * @return 数量
     */
    int tbCount(String dynamicDBKey);

    /**
     * 清除路由
     */
    void clear();

}