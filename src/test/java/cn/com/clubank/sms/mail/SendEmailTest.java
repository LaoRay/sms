package cn.com.clubank.sms.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.com.clubank.sms.manage.mail.SendMailService;

/**
 * 邮件发送测试类
 * @author Liangwl
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SendEmailTest {

	@Autowired
	private SendMailService sendMailService;
	
	@Test
	public void testSend(){
		String subject = "带图片邮件发送";
		String content = "你好";
		String toMail = "1341850130@qq.com";
		String picturePath = "E://360downloads//wpcache//srvsetwp//295362.jpg";
		sendMailService.sendPictureMail(subject, content, toMail, picturePath);
	}
}
