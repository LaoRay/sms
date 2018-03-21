package cn.com.clubank.sms.redis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.clubank.sms.TestBase;
import cn.com.clubank.sms.manage.message.MessageQueueUtil;

/**
 * redis测试
 * @author YangWei
 *
 */
public class RedisTest extends TestBase {

	@Autowired
	private MessageQueueUtil messageQueueUtil;
	
	@Test
	public void lpush() {
		System.out.println("消息数量：" + messageQueueUtil.getCurrentQueueAmount());
		
		InsertThread th1 = new InsertThread(messageQueueUtil);
		InsertThread th2 = new InsertThread(messageQueueUtil);
		InsertThread th3 = new InsertThread(messageQueueUtil);
		InsertThread th4 = new InsertThread(messageQueueUtil);
		th1.start();
		th2.start();
		th3.start();
		th4.start();
		
		System.out.println("消息数量：" + messageQueueUtil.getCurrentQueueAmount());
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("消息数量：" + messageQueueUtil.getCurrentQueueAmount());
	}
	
}
