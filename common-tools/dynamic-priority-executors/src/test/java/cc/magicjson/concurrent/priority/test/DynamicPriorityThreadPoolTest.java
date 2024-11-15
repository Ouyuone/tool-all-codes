package cc.magicjson.concurrent.priority.test;

import cc.magicjson.concurrent.priority.core.DynamicPriorityThreadPool;
import cc.magicjson.concurrent.priority.task.PriorityTask;
import cc.magicjson.concurrent.priority.task.TaskType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicPriorityThreadPoolTest {

    private record TaskExecution(TaskType type, Instant startTime, Instant endTime, int executionOrder) {
    }

    @Test
    @Timeout(6000)
    public void testTaskPriorities() {
        try (var pool = new DynamicPriorityThreadPool.Builder()
            .corePoolSize(0)
            .maximumPoolSize(5)
            .minLightTaskThreads(3)
            .build()) {

            var executionOrder = new AtomicInteger(0);
            var startSignal = new CompletableFuture<Void>();

            System.out.println("Submitting tasks...");
            TaskType[] values = TaskType.values();
         
            List<CompletableFuture<TaskExecution>> taskFutures = Arrays.asList(values, TaskType.values(),TaskType.values()).stream().flatMap(taskTypes -> Arrays.stream(taskTypes))
                .map(taskType -> {
                    System.out.println("Submitting task: " + taskType);
                    return createTask(taskType, executionOrder, startSignal, pool);
                })
                .toList();

            System.out.println("Starting all tasks");
            System.out.println(startSignal.complete(null));
            
            List<TaskExecution> results = CompletableFuture.allOf(taskFutures.toArray(CompletableFuture[]::new))
                .thenApply(v -> taskFutures.stream()
                    .map(CompletableFuture::join)
                    .toList())
                .orTimeout(3000, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    fail("Task execution failed or timed out: " + ex.getMessage());
                    return List.of();
                })
                .join();

            System.out.println("All tasks completed");
            verifyExecutionOrder(results);
            verifyLightWeightTaskExecution(results);
            printExecutionStatistics(results);
            
        
        }
    }

    private CompletableFuture<TaskExecution> createTask(TaskType taskType, AtomicInteger executionOrder, CompletableFuture<Void> startSignal, DynamicPriorityThreadPool pool) {
        return startSignal.thenCompose(ignored -> {
            PriorityTask<TaskExecution> task = PriorityTask.createCallableTask(() -> {
                var start = Instant.now();
                try {
                    // 使用固定的睡眠时间，以避免执行时间影响优先级顺序
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                var end = Instant.now();
                var order = executionOrder.incrementAndGet();
                System.out.printf("Task %s completed. Priority:%d%n Order: %d%n", taskType, taskType.getPriority(),order);
                return new TaskExecution(taskType, start, end, order);
            }, taskType);

            return CompletableFuture.supplyAsync(() -> {
                try {
                    return pool.submit((Callable<TaskExecution>) task).get();
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            });
        });
    }

    private void verifyExecutionOrder(List<TaskExecution> results) {
        for (int i = 0; i < results.size() - 1; i++) {
            var current = results.get(i);
            var next = results.get(i + 1);
            assertTrue(current.type().getPriority() <= next.type().getPriority(),
                () -> String.format("Tasks should be executed in priority order. %s (priority %d) " +
                        "should be executed before or at the same time as %s (priority %d)",
                    current.type(), current.type().getPriority(),
                    next.type(), next.type().getPriority()));
        }

        var firstTask = results.get(0);
        assertTrue(firstTask.type().isLightWeight(),
            () -> "The first executed task should be a light-weight task. Actual: " + firstTask.type());
    }

    private void verifyLightWeightTaskExecution(List<TaskExecution> results) {
        var lightWeightTasks = results.stream()
            .filter(task -> task.type().isLightWeight())
            .toList();

        assertFalse(lightWeightTasks.isEmpty(), "There should be at least one light-weight task");

        var firstTaskStartTime = lightWeightTasks.get(0).startTime();
        for (var task : lightWeightTasks) {
            var executionDelay = Duration.between(firstTaskStartTime, task.startTime());
            assertTrue(executionDelay.toMillis() < 50,
                () -> String.format("Light-weight task %s should start within 50ms of the first task. " +
                    "Actual delay: %d ms", task.type(), executionDelay.toMillis()));
        }
    }

    private void printExecutionStatistics(List<TaskExecution> results) {
        System.out.println("\nTask Execution Statistics:");
        results.forEach(task -> System.out.printf("%s: Order=%d, Duration=%d ms%n",
            task.type(), task.executionOrder(),
            Duration.between(task.startTime(), task.endTime()).toMillis()));
    }
}
