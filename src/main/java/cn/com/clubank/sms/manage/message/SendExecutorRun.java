package cn.com.clubank.sms.manage.message;

import java.util.Date;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;

import cn.com.clubank.sms.manage.message.service.MessageHandleService;
import lombok.extern.slf4j.Slf4j;

/**
 * 单个处理短信发送的线程
 * 
 * @author YangWei
 *
 */
@Slf4j
public class SendExecutorRun implements Callable<Date> {

	private MessageQueueUtil messageQueueUtil;
	
	private ExecutorPoolUtil executorPoolUtil;
	
	private MessageHandleService messageHandleService;
	
	private int index = 0; // 当前第几个线程
	
	@Override
	public Date call() throws Exception {
		try {
			
			log.info("【发送短信新线程】，当前线程数：{}，处理消息发送短信开始!", executorPoolUtil.getCurrentRunningThreadCount());
			boolean end = true;
			do {
				MessageEntity result = messageQueueUtil.get();
				if (null != result) {
					log.info("【发送短信】开始处理消息：{}", JSON.toJSONString(result));
					messageHandleService.sendSms(result, index);
				} else {
					end = false;
				}
			} while (end);
			
		} catch(Exception ex) {
			log.info("执行发送短信任务时发生异常：{}", ex.getMessage());
			ex.printStackTrace();
		} finally {
			executorPoolUtil.callback();
		}
		return new Date();
	}

	public SendExecutorRun(MessageQueueUtil messageQueueUtil, ExecutorPoolUtil executorPoolUtil, MessageHandleService messageHandleService) {
		super();
		this.messageQueueUtil = messageQueueUtil;
		this.executorPoolUtil = executorPoolUtil;
		this.messageHandleService = messageHandleService;
	}
	
	public SendExecutorRun(MessageQueueUtil messageQueueUtil, ExecutorPoolUtil executorPoolUtil, MessageHandleService messageHandleService, Integer index) {
		super();
		this.messageQueueUtil = messageQueueUtil;
		this.executorPoolUtil = executorPoolUtil;
		this.messageHandleService = messageHandleService;
		this.index = index;
	}
	
}
