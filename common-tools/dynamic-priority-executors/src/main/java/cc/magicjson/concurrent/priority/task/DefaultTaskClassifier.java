package cc.magicjson.concurrent.priority.task;

/**
 * DefaultTaskClassifier 是 TaskClassifier 接口的默认实现。
 * 它通过检查任务类名中的关键字来确定任务类型。
 */
public class DefaultTaskClassifier implements TaskClassifier {

    /**
     * 根据任务的类名对任务进行分类。
     *
     * @param task 需要分类的 Runnable 任务
     * @return 分类后的 TaskType
     */
    @Override
    public TaskType classifyTask(Runnable task) {
        // 获取任务的简单类名
        String taskName = task.getClass().getSimpleName();
        System.out.println("获取任务的简单类名:" +taskName);
        // 根据类名中的关键字判断任务类型
        if (taskName.contains("FileRead")) {
            return TaskType.FILE_READ;
        } else if (taskName.contains("DNS")) {
            return TaskType.DNS_LOOKUP;
        } else if (taskName.contains("Database")) {
            return TaskType.DATABASE_QUERY;
        } else if (taskName.contains("Network")) {
            return TaskType.NETWORK_IO;
        }
        
        if(task instanceof PriorityFutureTask futureTask){
            TaskType taskType = futureTask.getPriorityTask().getTaskType();
           return switch (taskType){
                case FILE_READ -> TaskType.FILE_READ;
                case DNS_LOOKUP ->  TaskType.DNS_LOOKUP;
                case DATABASE_QUERY ->  TaskType.DATABASE_QUERY;
                case NETWORK_IO ->  TaskType.NETWORK_IO;
                default ->  TaskType.COMPUTATION;
            };
        }

        // 如果没有匹配到特定类型，默认为计算任务
        return TaskType.COMPUTATION;
    }
}
