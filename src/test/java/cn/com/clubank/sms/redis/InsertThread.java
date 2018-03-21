package cn.com.clubank.sms.redis;

import cn.com.clubank.sms.manage.message.MessageEntity;
import cn.com.clubank.sms.manage.message.MessageQueueUtil;

public class InsertThread extends Thread {

	private MessageQueueUtil messageQueueUtil;
	
	public void run() {  
		String tempCode = "002";
		String mobile = "13600530000";
		String authCode = "1234";
		String templateId = "templateId";
		String accountId = "1";
		
		for (int i = 0; i < 40; i++) {
			// 插入消息队列
			messageQueueUtil.insertFirst(new MessageEntity(MessageEntity.type_code, templateId, accountId, tempCode, mobile, authCode));
		}
	}

	public InsertThread(MessageQueueUtil messageQueueUtil) {
		super();
		this.messageQueueUtil = messageQueueUtil;
	}
	
}
