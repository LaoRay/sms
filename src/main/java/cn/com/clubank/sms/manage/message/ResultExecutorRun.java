package cn.com.clubank.sms.manage.message;

import java.util.Date;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;

import cn.com.clubank.sms.manage.message.service.MessageHandleService;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理发送结果的线程
 * 
 * @author YangWei
 *
 */
@Slf4j
public class ResultExecutorRun implements Callable<Date> {

	private MessageQueueUtil messageQueueUtil;
	
	private ExecutorPoolUtil executorPoolUtil;
	
	private MessageHandleService messageHandleService;
	
	@Override
	public Date call() throws Exception {
		try {
			
			log.info("【处理结果新线程】，当前线程数：{}，处理发送结果开始!", executorPoolUtil.getResultCurrentRunningThreadCount());
			// 当前任务的执行
			boolean end = true;
			do {
				MessageEntity result = messageQueueUtil.getResult();
				if (null != result) {
					log.info("【处理结果】开始处理消息：{}", JSON.toJSONString(result));
					messageHandleService.handleResult(result);
				} else {
					end = false;
				}
			} while (end);
			
		} catch(Exception ex) {
			log.info("执行处理发送结果任务时发生异常：{}", ex.getMessage());
			ex.printStackTrace();
		} finally {
			executorPoolUtil.callbackResult();
		}
		return new Date();
	}

	public ResultExecutorRun(MessageQueueUtil messageQueueUtil, ExecutorPoolUtil executorPoolUtil, MessageHandleService messageHandleService) {
		super();
		this.messageQueueUtil = messageQueueUtil;
		this.executorPoolUtil = executorPoolUtil;
		this.messageHandleService = messageHandleService;
	}
	
}
