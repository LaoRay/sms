package cn.com.clubank.sms.manage.mail.impl;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import cn.com.clubank.sms.manage.mail.SendMailService;
import lombok.extern.slf4j.Slf4j;

/**
 * 发邮件
 * @author Liangwl
 *
 */
@Slf4j
@Service
public class SendMailServiceImpl implements SendMailService {

    @Autowired  
    JavaMailSender javaMailSender;  
    @Autowired  
    SimpleMailMessage simpleMailMessage;
	
    /**
     * 普通文本发邮件形式
     */
	@Override
	public void sendSimpleMail(String subject, String content, String toMail) {
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(content);
		simpleMailMessage.setTo(toMail);
		javaMailSender.send(simpleMailMessage);
		log.info("邮件发送成功"); 
	}

	/**
	 * html发邮件形式
	 */
	@Override
	public void sendHtmlMail(String subject, String content, String toMail) {
		MimeMessage mailMessage = javaMailSender.createMimeMessage();   
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);
        try {
        	messageHelper.setFrom(simpleMailMessage.getFrom());//发件人邮箱
			messageHelper.setSubject(subject);
			messageHelper.setText("<html><head></head><body><div style='color: #606266; font-size:15px;'>"+content+"</div></body></html>",true);
			messageHelper.setTo(toMail);
			javaMailSender.send(mailMessage);
			log.info("邮件发送成功");
		} catch (MessagingException e) {
			log.error("发送失败", e);
		}

	}

	/**
	 * 带图片发邮件形式
	 */
	@Override
	public void sendPictureMail(String subject, String content, String toMail, String picturePath) {
		MimeMessage mailMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper;
		try {
			messageHelper = new MimeMessageHelper(mailMessage,true);
			messageHelper.setFrom(simpleMailMessage.getFrom());//发件人邮箱
			messageHelper.setSubject(subject);
			messageHelper.setText("<html><head></head><body><h1>" + content + "</h1><img src=\"cid:image\"/></body></html>",true);
			messageHelper.setTo(toMail);
			FileSystemResource img = new FileSystemResource(new File(picturePath)); 
			messageHelper.addInline("image",img);//跟cid一致
			javaMailSender.send(mailMessage);
	        log.info("邮件发送成功");
		} catch (MessagingException e) {
			log.error("发送失败", e);
		} 

	}

	/**
	 * 带附件发邮件形式
	 */
	@Override
	public void sendMailTakeAccessory(String subject, String content, String toMail, String accessoryPath,
			String accessoryName) {
		MimeMessage mailMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper;
		try {
			messageHelper = new MimeMessageHelper(mailMessage,true,"utf-8");
			messageHelper.setFrom(simpleMailMessage.getFrom());//发件人邮箱
			messageHelper.setTo(toMail);  
			messageHelper.setSubject(subject);   
			messageHelper.setText("<html><head></head><body><h1>"+content+"</h1></body></html>",true);   
			FileSystemResource file = new FileSystemResource(new File(accessoryPath));   
			messageHelper.addAttachment(accessoryName,file);  
			javaMailSender.send(mailMessage);   
			log.info("邮件发送成功");
		} catch (MessagingException e) {
			log.error("发送失败", e);
		} 

	}

}
