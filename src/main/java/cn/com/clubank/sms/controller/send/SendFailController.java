package cn.com.clubank.sms.controller.send;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResponseObjectJson;
import cn.com.clubank.sms.common.utils.Constants;
import cn.com.clubank.sms.manage.send.service.SendFailService;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;
import cn.com.clubank.sms.manage.sys.service.AccountService;

/**
 * 发送失败
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("/fail")
public class SendFailController {

	@Autowired
	private SendFailService sendFailService;
	@Autowired
	private AccountService accountService;
	
	/**
	 * 按条件获得发送失败记录列表并分页
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/list",method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainSendFailListAndPage(HttpServletRequest request,@RequestBody JSONObject param){
		if(request.getHeader("roles").indexOf(Constants.Sort_99) != -1){//99-系统管理员
			return sendFailService.selectSendFailListAndPage(null, param.getString("appName"), param.getString("tempCode"), param.getString("mobile"), param.getString("dateOne"), param.getString("dateTwo"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
		}
		ResponseObjectJson roj = (ResponseObjectJson) accountService.selectUserInfo(request.getIntHeader("userId"));
		SysUser user = (SysUser) roj.getData();
		return sendFailService.selectSendFailListAndPage(user.getAccountId(), param.getString("appName"), param.getString("tempCode"), param.getString("mobile"), param.getString("dateOne"), param.getString("dateTwo"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
	}
}
