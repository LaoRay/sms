package cn.com.clubank.sms.controller.login;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.utils.VerificationCodeUtil;
import cn.com.clubank.sms.manage.sys.service.AccountService;

/**
 * 登录、注销
 * @author Liangwl
 *
 */
@Controller
public class LoginController {

	@Autowired
	private AccountService accountService;
	
	/**
	 * 用户登录
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/login" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson userLogin(@RequestBody JSONObject param, HttpSession session){
//		if(!param.getString("verifyCode").equals(session.getAttribute("verifyCodeValue"))){
//			return ResponseUtil.buildJson(ResultCode.FAIL, "验证码错误");
//		}
		return accountService.userLogin(param.getString("loginName"), param.getString("password"));
	}
	
	/**
	 * 用户注销
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson userExit(HttpServletRequest request){
		
		return accountService.userLogout(request.getHeader("token"));
	}
	
    /**
     * 获取验证码  
     * @param response
     * @param session
     */
    @RequestMapping(value = "/getVerifyCode" , method = RequestMethod.POST)  
    public void generate(HttpServletResponse response, HttpSession session) {  
        ByteArrayOutputStream output = new ByteArrayOutputStream();  
        String verifyCodeValue = VerificationCodeUtil.drawImg(output);  
  
        session.setAttribute("verifyCodeValue", verifyCodeValue);  
  
        try {  
            ServletOutputStream out = response.getOutputStream();  
            output.writeTo(out);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
}
