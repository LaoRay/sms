package cn.com.clubank.sms.manage.handle;

import javax.servlet.http.HttpServletRequest;

import cn.com.clubank.sms.common.response.ResponseJson;

/**
 * 第三方发送短信处理
 * 
 * @author YangWei
 *
 */
public interface SmsHandleService {

	/**
	 * 验证
	 * @param req
	 * @return
	 */
	ResponseJson sendcheck(HttpServletRequest req);
	
	/**
	 * 验证码短信发送
	 * @param req
	 * @return
	 */
	ResponseJson sendcode(HttpServletRequest req);
	
	/**
	 * 通知类短信发送
	 * @param req
	 * @return
	 */
	ResponseJson sendtemplate(HttpServletRequest req);
	
}
