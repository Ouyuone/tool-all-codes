package cc.magicjson.concurrent.priority.task;

import java.util.concurrent.Callable;


/**
 * PriorityTask 类表示一个具有优先级的任务。
 * 它实现了 Callable 接口，同时也实现了 Comparable 接口以支持优先级比较。
 *
 * @param <V> 任务执行结果的类型
 */
public class PriorityTask<V> implements Runnable, Callable<V>, Comparable<PriorityTask<?>> {

    // 存储 Callable 类型的任务
    private final Callable<V> task;

    // 任务的类型，用于确定任务的特性（如是否为轻量级任务）
    private final TaskType taskType;

    // 任务的优先级，数值越小优先级越高
    private final int priority;

    // 任务创建的时间戳，用于在优先级相同时进行比较
    private final long creationTime;

    public PriorityTask(Callable<V> task, TaskType taskType) {
        this.task = task;
        this.taskType = taskType;
        this.priority = taskType.getPriority();
        this.creationTime = System.nanoTime();
    }

    /**
     * 实现 Callable 接口的 call 方法
     *
     * @return 任务执行的结果
     * @throws Exception 如果任务执行过程中抛出异常
     */
    @Override
    public V call() throws Exception {
        return task.call();
    }

    /**
     * 实现 Runnable 接口的 run 方法
     *
     * @throws Exception 如果任务执行过程中抛出异常
     */
    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取任务类型
     *
     * @return 任务类型
     */
    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * 获取任务优先级
     *
     * @return 任务优先级
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 获取任务创建时间
     *
     * @return 任务创建时间（纳秒）
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * 实现 Comparable 接口的 compareTo 方法
     * 首先比较优先级，优先级相同时比较创建时间
     *
     * @param other 要比较的其他 PriorityTask
     * @return 比较结果：负数表示此任务优先级更高，正数表示其他任务优先级更高，0表示优先级相同
     */
    @Override
    public int compareTo(PriorityTask<?> other) {
        // 优先级数字越小，优先级越高
        int priorityCompare = Integer.compare(this.getTaskType().getPriority(), other.getTaskType().getPriority());
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        // 如果优先级相同，比较创建时间
        return Long.compare(this.getCreationTime(), other.getCreationTime());
    }

    /**
     * 创建 Callable 版本的 PriorityTask 的工厂方法
     *
     * @param callable Callable 任务
     * @param taskType 任务类型
     * @param <T> Callable 的返回类型
     * @return 包装了 Callable 的 PriorityTask
     */
    public static <T> PriorityTask<T> createCallableTask(Callable<T> callable, TaskType taskType) {
        return new PriorityTask<>(callable, taskType);
    }

    /**
     * 创建 Runnable 版本的 PriorityTask 的工厂方法
     *
     * @param runnable Runnable 任务
     * @param taskType 任务类型
     * @return 包装了 Runnable 的 PriorityTask
     */
    public static PriorityTask<Void> createRunnableTask(Runnable runnable, TaskType taskType) {
        return new PriorityTask<>(() -> {
            runnable.run();
            return null;
        }, taskType);
    }

    public Callable<V> asCallable() {
        return this;
    }
}
