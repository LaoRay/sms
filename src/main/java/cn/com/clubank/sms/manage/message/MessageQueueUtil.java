package cn.com.clubank.sms.manage.message;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;

import cn.com.clubank.sms.common.redis.JedisClient;
import cn.com.clubank.sms.common.redis.RedisKeyCode;
import cn.com.clubank.sms.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息队列处理
 * @author YangWei
 *
 */
@Slf4j
public class MessageQueueUtil {

	// 待发送短信的消息队列名称
	private String queue_sms_key;
	
	// 待处理结果的消息队列名称
	private String queue_result_key;
	
	// 保存账户在消息队列中待发送的短信数量（按照手机号码计算）
	private String sms_wait_send_amount_key;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private ExecutorPoolUtil executorPoolUtil;
	
	// 线程安全：短信消息队列中的消息总量
	private AtomicInteger queue_sms_amount = null;
	
	/**
	 * 初始化消息队列管理util
	 */
	protected void init() {
		while (!jedisClient.ping()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.info("初始化Redis链接失败！");
		}
		if (queue_sms_amount == null) {
			queue_sms_amount = new AtomicInteger(0);
		}
		// 消息队列名称
		queue_sms_key = RedisKeyCode.MSG_QUEUE.getValue();
		queue_result_key = RedisKeyCode.RESULT_QUEUE.getValue();
		// 短信消息队列中的消息总量
		queue_sms_amount.set(jedisClient.llen(queue_sms_key));
		
		// 保存账户在消息队列中待发送的短信数量（按照手机号码计算）
		sms_wait_send_amount_key = RedisKeyCode.WAIT_SEND_AMOUNT.getValue();
		
		log.info("初始化消息队列处理类成功！当前剩余待发送短信数量：{}", queue_sms_amount.get());
		if (queue_sms_amount.get() > 0) {
			// 调用线程管理的插入事件
			executorPoolUtil.insertEvent(queue_sms_amount.get(), this);
		}
	}
	
	/**
	 * 插入消息-插入第一位
	 * @param msg
	 */
	public synchronized void insertFirst(MessageEntity msg) {
		
		jedisClient.lpush(queue_sms_key, JSON.toJSONString(msg));
		queue_sms_amount.getAndIncrement();
		
		// 插入消息时增加账户在消息队列中的预扣短信数量
		increaseAccountSmsAmount(msg.getType(), msg.getAccountId(), msg.getMobiles());
		
		// 调用线程管理的插入事件
		executorPoolUtil.insertEvent(queue_sms_amount.get(), this);
	}
	
	/**
	 * 插入消息-插入最后一位
	 * @param msg
	 */
	public synchronized void insertLast(MessageEntity msg) {
		
		jedisClient.rpush(queue_sms_key, JSON.toJSONString(msg));
		queue_sms_amount.getAndIncrement();
		
		// 插入消息时增加账户在消息队列中的预扣短信数量
		increaseAccountSmsAmount(msg.getType(), msg.getAccountId(), msg.getMobiles());
		
		// 调用线程管理的插入事件
		executorPoolUtil.insertEvent(queue_sms_amount.get(), this);
	}
	
	/**
	 * 从最后一位插入失败的消息
	 * @param msg
	 */
	public void insertFailLast(MessageEntity msg) {
		
		jedisClient.rpush(queue_sms_key, JSON.toJSONString(msg));
		queue_sms_amount.getAndIncrement();
		
	}
	
	/**
	 * 获取消息-获取最后一位
	 */
	public synchronized MessageEntity get() {
		
//		log.info("获取消息-获取最后一位，当前消息数量：{}", sms_queue_amount);
		String result = jedisClient.rpop(queue_sms_key);
		if (StringUtil.isNotBlank(result)) {
			MessageEntity msg = JSON.parseObject(result, MessageEntity.class);
			queue_sms_amount.getAndDecrement();
			return msg;
		} else {
			queue_sms_amount.set(0);
		}
		return null;
	}
	
	/**
	 * 获取队列当前的消息数量
	 */
	public int getCurrentQueueAmount() {
		return queue_sms_amount.get();
	}
	
	/**
	 * 获取账户待发送的短信数量-预扣短信数量
	 */
	public int getWaitSendSmsAmount(String accountId) {
		String amount = jedisClient.getNoEncode(getRedisKey(accountId));
		return StringUtil.isBlank(amount) ? 0 : Integer.valueOf(amount);
	}
	
	/**
	 * 插入消息时增加账户在消息队列中的预扣短信数量
	 * @param type
	 * @param account
	 */
	private void increaseAccountSmsAmount(int type, String accountId, String mobiles) {
		//增加的数量
		int increment = 1; // 默认验证码：1
		if (type == MessageEntity.type_temp) {
			// 通知类、运营类
			increment = JSON.parseArray(mobiles).size();
		}
		
		jedisClient.incrByNoEncode(getRedisKey(accountId), increment);
	}
	
	/**
	 * 消息消息完成后时减少账户在消息队列中的预扣短信数量
	 * @param type
	 * @param account
	 */
	public void decreaseAccountSmsAmount(int type, String accountId, String mobiles) {
		//减少的数量
		int increment = 1; // 默认验证码：1
		if (type == MessageEntity.type_temp) {
			// 通知类、运营类
			increment = JSON.parseArray(mobiles).size();
		}
		
		jedisClient.decrByNoEncode(getRedisKey(accountId), increment);
	}
	
	/**
	 * 获得redis中保存账户在消息队列中待发送的短信数量的key
	 * @param id
	 * @return
	 */
	private String getRedisKey(String id) {
		return sms_wait_send_amount_key.replace("{id}", id);
	}
	
	/**
	 * 发送结果-插入消息-插入第一位
	 * @param msg
	 */
	public void insertResultFirst(MessageEntity msg) {
		
		jedisClient.lpush(queue_result_key, JSON.toJSONString(msg));
		
		// 调用线程管理的插入事件
		executorPoolUtil.insertResultEvent(this);
	}
	
	/**
	 * 发送结果-获取消息-获取最后一位
	 */
	public synchronized MessageEntity getResult() {
		String result = jedisClient.rpop(queue_result_key);
		if (StringUtil.isNotBlank(result)) {
			return JSON.parseObject(result, MessageEntity.class);
		}
		return null;
	}
}
