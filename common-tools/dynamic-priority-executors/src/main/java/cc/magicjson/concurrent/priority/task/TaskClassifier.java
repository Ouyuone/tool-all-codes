package cc.magicjson.concurrent.priority.task;

/**
 * TaskClassifier 接口定义了任务分类器的行为。
 * 实现此接口的类负责将提交的 Runnable 任务分类为预定义的 TaskType。
 */
public interface TaskClassifier {

    /**
     * 对给定的任务进行分类。
     *
     * @param task 需要分类的 Runnable 任务
     * @return 分类后的 TaskType
     */
    TaskType classifyTask(Runnable task);
}
