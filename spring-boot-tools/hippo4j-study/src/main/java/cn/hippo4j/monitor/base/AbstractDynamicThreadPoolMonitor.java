package cn.hippo4j.monitor.base;

import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractDynamicThreadPoolMonitor implements DynamicThreadPoolMonitor {

    @Resource
    private ThreadPoolRunStateHandler threadPoolRunStateHandler;

    /**
     * Execute collection thread pool running data.
     *
     * @param dynamicThreadPoolRunStateInfo dynamic thread-pool run state info
     */
    protected abstract void execute(ThreadPoolRunStateInfo dynamicThreadPoolRunStateInfo);

    @Override
    public void collect() {
        List<String> listDynamicThreadPoolId = GlobalThreadPoolManage.listThreadPoolId();
//        listDynamicThreadPoolId.forEach(each -> execute(threadPoolRunStateHandler.getPoolRunState(each)));
        Arrays.asList("message-consume").forEach(each -> execute(threadPoolRunStateHandler.getPoolRunState(each)));
    }
}