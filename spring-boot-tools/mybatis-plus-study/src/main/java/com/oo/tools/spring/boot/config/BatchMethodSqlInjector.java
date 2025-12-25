package com.oo.tools.spring.boot.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * BatchMethodSqlInjector
 *
 * @author Yu.ou
 * @date: 2025/03/17 22:44:14
 * @desc: Mybatis-plus 方法注入
 * @since: 1.0.0
 */

public class BatchMethodSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        // methodList.add(new BatchInsertMethod());
        // methodList.add(new BatchUpdateMethod());
        // methodList.add(new DeleteAbsoluteMethod());
        // methodList.add(new DeleteAbsoluteAllMethod());
        return methodList;
    }
}