package com.naah.redis.sentinel.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 除了流量控制以外，对调用链路中不稳定的资源进行熔断降级也是保障高可用的重要措施之一。
 * Sentinel
 * 熔断降级会在调用链路中某个资源出现不稳定状态时（例如调用超时或异常比例升高），对这个资源的调用进行限制，让请求快速失败，避免影响到其它的资源而导致级联错误。
 * 那怎么来衡量资源是否稳定呢？
 * Sentinel提供了三种方式，平均响应时间、异常比例和异常数。
 * 我们拿平均响应时间为例，先来定义它的规则。
 * 作者：清幽之地 链接：https://www.jianshu.com/p/79a97cd6e3d1 来源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 * @author mlj
 *
 */
@Slf4j
public class LimitSentinel {

	/**
	 * 1秒内的5个请求，平均响应时间大于10ms，接下来的3秒内都会自动熔断。
	 * @param resourceName
	 */
	public static void loadDegradeRule(String resourceName) {
		List<DegradeRule> rules = new ArrayList<>();
		DegradeRule rule = new DegradeRule();
		// 资源名称
		rule.setResource(resourceName);
		// 阈值 - 10ms
		rule.setCount(10);
		// 熔断策略 - RT模式
		rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
		// 时间窗口 - 3s
		rule.setTimeWindow(3);
		// RT模式下,1秒内连续多少个请求的平均RT超出阈值，才可以触发熔断
		rule.setRtSlowRequestAmount(5);
		rules.add(rule);
		DegradeRuleManager.loadRules(rules);
	}

	/**
	 * 在上面的代码中，我们一共有20个请求。我们让线程停顿15ms使平均RT超过阈值，也就是超过10ms。
	 * 我们定义的规则里面是1秒内连续5个请求的平均RT超出阈值，就可以触发熔断，所以当第6个请求到达时，就会触发熔断。
	 * 熔断多久呢？就在3秒的时间窗口。
	 * 
	 * 上面的测试代码中，在触发熔断之后，我们又手动让线程停顿了 1000ms ，所以每次熔断的请求会有3个。
	 * 
	 * 作者：清幽之地 链接：https://www.jianshu.com/p/79a97cd6e3d1
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		String resource = "orderService";

		AtomicInteger count = new AtomicInteger(0);
		boolean stop = false;
		loadDegradeRule(resource);
		
		while (!stop) {
			count.incrementAndGet();
			Entry entry = null;
			try {
				entry = SphU.entry(resource);
				log.info("业务操作...{}", count.get());
				Thread.sleep(15);
			} catch (BlockException e) {
				if (e instanceof DegradeException) {
					log.error("触发熔断机制...{}", count.get());
					Thread.sleep(500);
				}
			} finally {
				if (entry != null) {
					entry.exit();
				}
				if (count.get() >= 20) {
					stop = true;
				}
			}
		}
		log.info("----------------------------");
	}
}
