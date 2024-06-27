package com.oo.tools.spring.boot.support;

/**
 * @desc 用来做多数据源上下文的切换
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 11:19:14
 */
public class DynamicSwitchContext {

    private static final ThreadLocal<String> CONTEXT_HOLDER = ThreadLocal.withInitial(() -> null);


    public static String getContext() {
        return CONTEXT_HOLDER.get();
    }

    public static void setContextSwitch(String context) {
        CONTEXT_HOLDER.set(context);
    }

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}
