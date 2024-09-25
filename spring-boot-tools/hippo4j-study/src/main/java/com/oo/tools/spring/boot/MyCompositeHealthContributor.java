package com.oo.tools.spring.boot;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lyy
 */
@Component
public class MyCompositeHealthContributor implements CompositeHealthContributor {
	
	private Map<String, HealthContributor> map = new HashMap<String, HealthContributor>();
	
	@Resource
	private ThreadPoolExecutor messageConsumeDynamicExecutor;
	
	@PostConstruct
	public void init() {
		MyThreadPollHealthContributor threadPool = new MyThreadPollHealthContributor(messageConsumeDynamicExecutor);
		map.put("threadPoll", threadPool);
		addTask();
	}
	
	@Override
	public HealthContributor getContributor(String name) {
		return map.get(name);
	}
	
	@Override
	public Iterator<NamedContributor<HealthContributor>> iterator() {
		List<NamedContributor<HealthContributor>> contributors = new ArrayList<NamedContributor<HealthContributor>>();
		
		map.forEach((name, c) -> {
			contributors.add(NamedContributor.of(name, c));
		});
		
		return contributors.iterator();
	}
	
	

	public void addTask() {
		AtomicLong finishTaskNum = new AtomicLong();
		new Thread(() -> {
			
			while (true) {
				try {
					messageConsumeDynamicExecutor.execute(() -> {
						try {
							TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 40));
							System.out.println("完成任务.." + finishTaskNum.getAndIncrement());
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e2) {
				}
				
				
			}
		}).start();
		
	}
	
}