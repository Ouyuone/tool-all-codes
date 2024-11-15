package cc.magicjson.concurrent.priority.task;

import lombok.Getter;

import java.util.concurrent.FutureTask;

/**
 * PriorityFutureTask 类扩展了 FutureTask，并实现了 Comparable 接口。
 * 这个类用于在线程池中包装 PriorityTask，使其可以根据优先级进行排序。
 *
 * @param <T> 任务结果的类型
 */
public class PriorityFutureTask<T> extends FutureTask<T> implements Comparable<PriorityFutureTask<T>> {

    /**
     * 被包装的 PriorityTask 实例
     */
    @Getter
    private final PriorityTask<T> priorityTask;

    /**
     * 构造一个新的 PriorityFutureTask
     *
     * @param task 要包装的 PriorityTask
     */
    public PriorityFutureTask(PriorityTask<T> task) {
        super(task);
        this.priorityTask = task;
    }

    /**
     * 比较此 PriorityFutureTask 与另一个 PriorityFutureTask 的优先级
     *
     * @param o 要比较的其他 PriorityFutureTask
     * @return 负数表示此任务优先级更高，正数表示其他任务优先级更高，0 表示优先级相同
     */
    @Override
    public int compareTo(PriorityFutureTask<T> o) {
        // 委托给内部 PriorityTask 的 compareTo 方法
        return this.priorityTask.compareTo(o.priorityTask);
    }
}
