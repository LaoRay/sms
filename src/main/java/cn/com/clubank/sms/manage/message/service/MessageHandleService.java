package cn.com.clubank.sms.manage.message.service;

import cn.com.clubank.sms.manage.message.MessageEntity;

/**
 * 消息处理
 * @author YangWei
 *
 */
public interface MessageHandleService {

	/**
	 * 处理发送短信消息
	 * @param result
	 * @param index
	 */
	public void sendSms(MessageEntity result, Integer index);
	
	/**
	 * 处理发送结果消息
	 * @param result
	 */
	public void handleResult(MessageEntity result);
	
}
