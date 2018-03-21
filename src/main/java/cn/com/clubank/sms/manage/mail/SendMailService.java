package cn.com.clubank.sms.manage.mail;

/**
 * 发送邮件接口
 * @author Liangwl
 *
 */
public interface SendMailService {

	/**
	 * 普通文本发邮件形式
	 * @param subject 主题
	 * @param content 正文
	 * @param toMail  收件人邮箱
	 */
	void sendSimpleMail(String subject,String content,String toMail);
	
	/**
	 * html发邮件形式 
	 * @param subject 主题
	 * @param content 正文
	 * @param toMail  收件人邮箱
	 */
	void sendHtmlMail(String subject,String content,String toMail);
	
	/**
	 * 带图片发邮件形式
	 * @param subject 主题
	 * @param content 正文
	 * @param toMail  收件人邮箱
	 * @param picturePath 图片路径
	 */
	void sendPictureMail(String subject,String content,String toMail,String picturePath);
	
	/**
	 * 带附件发邮件形式
	 * @param subject 主题
	 * @param content 正文
	 * @param toMail  收件人邮箱
	 * @param accessoryPath 附件路径
	 * @param accessoryName 附件名
	 */
	void sendMailTakeAccessory(String subject,String content,String toMail,String accessoryPath,String accessoryName);
}
