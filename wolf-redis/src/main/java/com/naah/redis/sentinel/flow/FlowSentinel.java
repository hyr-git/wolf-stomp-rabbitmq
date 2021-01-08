package com.naah.redis.sentinel.flow;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import lombok.extern.slf4j.Slf4j;

/**
 * sentinel 流量控制
 * 原理是监控应用流量的QPS或者并发线程数等指标,当达到指定的阈值时对流量进行控制,已避免瞬间的高流量击垮,从而保障应用的高可用性
 * @author mlj
 *
 */
@Slf4j
public class FlowSentinel {

	/**
	 * 加载限流规则
	 * -- 当每秒的请求数达到5秒之后,就会直接拒绝当前时间窗口的后续请求
	 * @param resource
	 */
	public static void loadFlowRules(String resource){
	    FlowRule rule = new FlowRule();
	    //资源名称,可以是任意字符串
	    rule.setResource(resource);
	    
	    //限流阈值
	    rule.setCount(5);
	    
	    //限流阈值类型,设置为QPS。即每秒QPS大于5时，触发限流
	    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
	    
	    //针对的调用来源
	    rule.setLimitApp("default");
	    
	    //调用关系限流策略,默认按照资源本身
	    rule.setStrategy(RuleConstant.STRATEGY_DIRECT);
	    
	    //限流效果，默认直接拒绝
	    rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
	    
	    //是否集群限流
	    rule.setClusterMode(false);
	    
	    FlowRuleManager.loadRules(Collections.singletonList(rule));
	}
	
	public static void main(String[] args) throws InterruptedException {
		String resource = "orderService";
		
		AtomicInteger count = new AtomicInteger(0);
		boolean stop = false;
		
		//加载限流规则
	    loadFlowRules(resource);
	    
	    while (!stop){
	        count.incrementAndGet();
	        Entry entry = null;
	        try {
	            entry = SphU.entry(resource);
	            log.info("业务操作...{}",count.get());
	        } catch (BlockException e) {
	        	log.error("请求被限流...{}",count.get());
	            Thread.sleep(1000);
	        } finally {
	            if (entry != null) {
	                entry.exit();
	            }
	            if (count.get()>=20){
	                stop = true;
	            }
	        }
	    }
	}
}
