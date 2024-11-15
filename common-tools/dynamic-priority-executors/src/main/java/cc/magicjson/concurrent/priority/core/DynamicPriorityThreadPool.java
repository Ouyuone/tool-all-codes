package cc.magicjson.concurrent.priority.core;

import cc.magicjson.concurrent.priority.task.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态优先级线程池，扩展了ThreadPoolExecutor，实现了自动关闭功能。
 * 这个线程池能够根据任务类型动态调整执行优先级，并保证最小数量的线程用于处理轻量级任务。
 */
public class DynamicPriorityThreadPool extends ThreadPoolExecutor implements AutoCloseable {

    // 追踪当前正在执行轻量级任务的线程数
    private final AtomicInteger lightTaskThreads = new AtomicInteger(0);
    // 保证处理轻量级任务的最小线程数
    private final int minLightTaskThreads;
    // 用于分类提交的任务
    private final TaskClassifier taskClassifier;

    /**
     * 构造函数
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime 空闲线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @param threadFactory 线程工厂
     * @param handler 拒绝策略
     * @param minLightTaskThreads 最小轻量级任务线程数
     * @param taskClassifier 任务分类器
     */
    public DynamicPriorityThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit,
                                     @NotNull BlockingQueue<Runnable> workQueue, @NotNull ThreadFactory threadFactory,
                                     @NotNull RejectedExecutionHandler handler, int minLightTaskThreads,
                                     @NotNull TaskClassifier taskClassifier) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.minLightTaskThreads = minLightTaskThreads;
        this.taskClassifier = taskClassifier;
    }

    /**
     * 在任务执行前调用，用于追踪轻量级任务的执行情况
     */
    @Override
    protected void beforeExecute(@NotNull Thread t, @NotNull Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof PriorityTask<?> task && task.getTaskType().isLightWeight()) {
            lightTaskThreads.incrementAndGet();
        }
    }

    /**
     * 在任务执行后调用，用于更新轻量级任务的计数
     */
    @Override
    protected void afterExecute(@NotNull Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r instanceof PriorityTask<?> task && task.getTaskType().isLightWeight()) {
            lightTaskThreads.decrementAndGet();
        }
    }

    /**
     * 执行提交的任务，确保轻量级任务得到优先处理
     */
    @Override
    public void execute(Runnable command) {
        PriorityTask<?> task;
        if (command instanceof PriorityTask<?>) {
            task = (PriorityTask<?>) command;
        } else {
            TaskType taskType = taskClassifier.classifyTask(command);
            task = PriorityTask.createRunnableTask(command, taskType);
        }

        if (task.getTaskType().isLightWeight() && lightTaskThreads.get() < minLightTaskThreads) {
            class tempRunnable implements Runnable,Comparable<PriorityTask> {
                @Override
                public void run() {
                    
                    lightTaskThreads.incrementAndGet();
                    try {
                        task.run();
                    } finally {
                        lightTaskThreads.decrementAndGet();
                    }
                    
                }
                
                @Override
                public int compareTo(@NotNull PriorityTask o) {
                    return task.compareTo(o);
                }
            }
      
            super.execute(new tempRunnable());
        } else {
            super.execute(task);
        }
    }

    /**
     * 创建新的RunnableFuture任务
     */
    @NotNull
    @Override
    protected <T> RunnableFuture<T> newTaskFor(@NotNull Runnable runnable, T value) {
        if (runnable instanceof PriorityTask<?>) {
            @SuppressWarnings("unchecked")
            PriorityTask<T> castedTask = (PriorityTask<T>) runnable;
            return new PriorityFutureTask<>(castedTask);
        }
        TaskType taskType = taskClassifier.classifyTask(runnable);
        return new PriorityFutureTask<>(PriorityTask.createCallableTask(() -> {
            runnable.run();
            return value;
        }, taskType));
    }

    /**
     * 创建新的RunnableFuture任务（针对Callable）
     */
    @NotNull
    @Override
    protected <T> RunnableFuture<T> newTaskFor(@NotNull Callable<T> callable) {
        if (callable instanceof PriorityTask<?> priorityTask) {
            @SuppressWarnings("unchecked")
            PriorityTask<T> castedTask = (PriorityTask<T>) priorityTask;
            return new PriorityFutureTask<>(castedTask);
        }
        TaskType taskType = taskClassifier.classifyTask(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return new PriorityFutureTask<>(new PriorityTask<>(callable, taskType));
    }

    /**
     * 关闭线程池，实现AutoCloseable接口
     */
    @Override
    public void close() {
        shutdown();
        try {
            if (!awaitTermination(60, TimeUnit.SECONDS)) {
                shutdownNow();
                if (!awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static class Builder {
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maximumPoolSize = corePoolSize * 2;
        private long keepAliveTime = 60L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private BlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<>();
        private ThreadFactory threadFactory = Executors.defaultThreadFactory();
        private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        private int minLightTaskThreads = 1;
        private TaskClassifier taskClassifier = new DefaultTaskClassifier();

        public Builder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder keepAliveTime(long keepAliveTime, TimeUnit unit) {
            this.keepAliveTime = keepAliveTime;
            this.unit = unit;
            return this;
        }

        public Builder workQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder rejectedExecutionHandler(RejectedExecutionHandler handler) {
            this.handler = handler;
            return this;
        }

        public Builder minLightTaskThreads(int minLightTaskThreads) {
            this.minLightTaskThreads = minLightTaskThreads;
            return this;
        }

        public Builder taskClassifier(TaskClassifier taskClassifier) {
            this.taskClassifier = taskClassifier;
            return this;
        }

        public DynamicPriorityThreadPool build() {
            return new DynamicPriorityThreadPool(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, handler, minLightTaskThreads, taskClassifier);
        }
    }
}
