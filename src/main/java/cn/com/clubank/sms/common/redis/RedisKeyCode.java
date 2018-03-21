package cn.com.clubank.sms.common.redis;

/**
 * redis中的key
 * 
 * @author YangWei
 *
 */
public enum RedisKeyCode {
	
	/**
	 * 客户端登录的token
	 */
	LOGIN_TOKEN("token:login:"),
	
	/**
	 * 第三方发送短信的token
	 */
	ACCESS_TOKEN("token:access:"),
	
	/**
	 * 待发送短信的消息队列
	 */
	MSG_QUEUE("queue:msg"),
	
	/**
	 * 待处理结果的消息队列
	 */
	RESULT_QUEUE("queue:result"),
	
	/**
	 * 编号
	 */
	BASE_CODE("basecode:sms:"),
	
	/**
	 * 保存账户在消息队列中待发送的短信数量（按照手机号码计算）
	 */
	WAIT_SEND_AMOUNT("amount:waitSendSms:{id}");
	
	private String value; 
	
	private RedisKeyCode(String value){
		this.value = value;
	}
	
	public String getValue() {
        return value;
    }
	
}
