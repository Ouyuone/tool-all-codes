package cc.magicjson.concurrent.priority.task;

import lombok.Getter;

/**
 * TaskType 枚举定义了系统中不同类型的任务及其优先级和轻量级标志。
 * 这个枚举用于在动态优先级线程池中对任务进行分类和优先级排序。
 */
public enum TaskType {
    /**
     * 文件读取任务
     * 优先级最高（1），被视为轻量级任务
     * 通常用于快速的文件I/O操作
     */
    FILE_READ(1, true),

    /**
     * DNS查询任务
     * 优先级最低（5），不被视为轻量级任务
     * 用于网络相关的DNS解析操作
     */
    DNS_LOOKUP(5, false),

    /**
     * 数据库查询任务
     * 优先级中等（3），不被视为轻量级任务
     * 用于数据库操作，可能涉及复杂查询或事务
     */
    DATABASE_QUERY(3, false),

    /**
     * 计算任务
     * 优先级较低（4），不被视为轻量级任务
     * 用于CPU密集型的计算操作
     */
    COMPUTATION(4, false),

    /**
     * 网络I/O任务
     * 优先级较高（2），不被视为轻量级任务
     * 用于一般的网络通信操作
     */
    NETWORK_IO(2, false);

    /**
     * 任务的优先级
     * 数值越小，优先级越高
     * -- GETTER --
     *  获取任务的优先级
     *
     */
    @Getter
    private final int priority;

    /**
     * 标志是否为轻量级任务
     * 轻量级任务可能会得到特殊处理，如优先执行
     */
    private final boolean isLightWeight;

    /**
     * TaskType 的构造函数
     *
     * @param priority 任务的优先级，数值越小优先级越高
     * @param isLightWeight 是否为轻量级任务的标志
     */
    TaskType(int priority, boolean isLightWeight) {
        this.priority = priority;
        this.isLightWeight = isLightWeight;
    }

    /**
     * 判断任务是否为轻量级任务
     *
     * @return 如果是轻量级任务返回 true，否则返回 false
     */
    public boolean isLightWeight() {
        return isLightWeight;
    }
    
}
