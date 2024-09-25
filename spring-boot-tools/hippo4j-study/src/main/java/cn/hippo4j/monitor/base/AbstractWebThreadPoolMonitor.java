package cn.hippo4j.monitor.base;

import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import jakarta.annotation.Resource;

public abstract class AbstractWebThreadPoolMonitor implements WebThreadPoolMonitor {
    
    @Resource
    private WebThreadPoolService webThreadPoolService;
    
    /**
     * Execute collection thread pool running data.
     *
     * @param webThreadPoolRunStateInfo web thread-pool run state info
     */
    protected abstract void execute(ThreadPoolRunStateInfo webThreadPoolRunStateInfo);
    
    @Override
    public void collect() {
        ThreadPoolRunStateInfo webThreadPoolRunStateInfo = webThreadPoolService.getWebRunStateInfo();
        execute(webThreadPoolRunStateInfo);
    }
}