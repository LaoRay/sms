package cn.com.clubank.sms.manage.message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.com.clubank.sms.manage.message.service.MessageHandleService;
import lombok.extern.slf4j.Slf4j;

/**
 * 线程池管理
 * 
 * @author YangWei
 *
 */
@Slf4j
public class ExecutorPoolUtil {

	// 线程池初始数量
	@Value("${thread.pool.size}")
	private int thread_pool_size;
	
	// 单个线程对应消息数量
	@Value("${thread.single.num}")
	private int thread_single_num;
	
	// 处理发送短信的线程池
	private ExecutorService executorService;
	
	// 处理发送结果的线程池
	private ExecutorService resultExecutorService;
	
	protected void init() {
		executorService = Executors.newFixedThreadPool(thread_pool_size);
		resultExecutorService = Executors.newFixedThreadPool(1);
		log.info("初始化线程处理类成功！");
	}
	
	@Autowired
	private MessageHandleService messageHandleService;
	
	/**
	 * 待发送短信消息插入事件
	 * @param smsQueueAmount	当前消息队列中的消息总量
	 */
	public void insertEvent(int smsQueueAmount, MessageQueueUtil messageQueueUtil) {
		// 最终需要启动的线程数
		int finalnum = 0;
		
		if (getCurrentRunningThreadCount() < thread_pool_size) {
			// 算出按照当前数量应该需要几个线程, 减去当前正在运行的线程数得出需要新启动几个线程
			int newnum = (smsQueueAmount / thread_single_num + 1) - getCurrentRunningThreadCount();
			// 最多还能启动几个线程
			int mostnum = thread_pool_size - getCurrentRunningThreadCount();
			// 最终需要启动的线程数
			finalnum = newnum > mostnum ? mostnum : newnum;
		}
		
		for (int i = 0; i < finalnum; i++) {
			SendExecutorRun run = new SendExecutorRun(messageQueueUtil, this, messageHandleService, getCurrentRunningThreadCount());
			executorService.submit(run);
		}
	}
	
	/**
	 * 待处理结果消息插入事件
	 * @param smsQueueAmount	当前消息队列中的消息总量
	 */
	public void insertResultEvent(MessageQueueUtil messageQueueUtil) {
		if (getResultCurrentRunningThreadCount() == 0) {
			ResultExecutorRun run = new ResultExecutorRun(messageQueueUtil, this, messageHandleService);
			resultExecutorService.submit(run);
		}
	}
	
	/**
	 * 发送短信线程执行完毕回调
	 */
	public void callback() {
		log.info("发送短信线程执行完毕，当前剩余线程数：{}", getCurrentRunningThreadCount() - 1);
	}
	
	/**
	 * 处理结果线程执行完毕回调
	 */
	public void callbackResult() {
		log.info("处理结果线程执行完毕，当前剩余线程数：{}", getResultCurrentRunningThreadCount() - 1);
	}
	
	/**
	 * 发送短信-获取当前运行的线程数量
	 * @return
	 */
	public int getCurrentRunningThreadCount() {
		return ((ThreadPoolExecutor) executorService).getActiveCount();
	}
	
	/**
	 * 处理结果-获取当前运行的线程数量
	 * @return
	 */
	public int getResultCurrentRunningThreadCount() {
		return ((ThreadPoolExecutor) resultExecutorService).getActiveCount();
	}
	
}
