package cn.com.clubank.sms.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.handle.SmsHandleService;

/**
 * 提供给第三方应用的接口
 * 
 * @author YangWei
 *
 */
@Controller
public class ClubankSmsController {

	@Autowired
	private SmsHandleService smsHandleService;
	
	/**
	 * 验证
	 * @return
	 */
	@RequestMapping(value = "/sendcheck", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson sendcheck(HttpServletRequest req) {
		
		return smsHandleService.sendcheck(req);
	}
	
	/**
	 * 发送验证码
	 * @return
	 */
	@RequestMapping(value = "/sendcode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson sendcode(HttpServletRequest req) {
		
		return smsHandleService.sendcode(req);
	}
	
	/**
	 * 发送通知类短信
	 * @return
	 */
	@RequestMapping(value = "/sendtemplate", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson sendtemplate(HttpServletRequest req) {
		
		return smsHandleService.sendtemplate(req);
	}
	
}
