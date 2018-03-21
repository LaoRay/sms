package cn.com.clubank.sms.controller.app;

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
import cn.com.clubank.sms.manage.app.pojo.AppInfo;
import cn.com.clubank.sms.manage.app.service.AppAuditService;
import cn.com.clubank.sms.manage.app.service.AppService;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;
import cn.com.clubank.sms.manage.sys.service.AccountService;

/**
 * 应用管理
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("/app")
public class AppController {

	@Autowired
	private AppService appService;
	@Autowired
	private AppAuditService appAuditService;
	@Autowired
	private AccountService accountService;
	
	/**
	 * 查询应用列表：分页查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainAppListPage(HttpServletRequest request, @RequestBody JSONObject param){
		if(request.getHeader("roles").indexOf(Constants.Sort_99) != -1){//99-系统管理员
			return appService.selectAppListAndPage(null, param.getString("appSort"), param.getString("appName"), param.getInteger("key"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
		}
		ResponseObjectJson roj = (ResponseObjectJson) accountService.selectUserInfo(request.getIntHeader("userId"));
		SysUser user = (SysUser) roj.getData();
		return appService.selectAppListAndPage(user.getAccountId(), param.getString("appSort"), param.getString("appName"), param.getInteger("key"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
	}
	
	/**
	 * 获得应用信息（编辑用）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/info", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainAppInfo(@RequestBody JSONObject param){
		 
		return appService.selectAppInfo(param.getInteger("appId"));
	}
	
	/**
	 * 新增、编辑应用
	 * @param app
	 * @return
	 */
	@RequestMapping(value = "/save" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson addOrEditApp(HttpServletRequest request, @RequestBody AppInfo app){
		
		return appService.insertOrUpdateApp(request.getHeader("userId"), app);
	}
	
	/**
	 * 获得应用详情（根据登录用户的角色获得详情内容）
	 * @param request
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/detail" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainAppDetail(HttpServletRequest request, @RequestBody JSONObject param){
		
		return appService.selectAppDetail(request.getIntHeader("userId"), param.getInteger("appId"));
	}
	
	/**
	 * 删除应用
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson deleteApp(@RequestBody JSONObject param){
		
		return appService.deleteApp(param.getJSONArray("appIds"));
	}
	
	/**
	 * 审核应用
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/audit" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson auditApp(@RequestBody JSONObject param){
		
		return appAuditService.auditApp(param.getInteger("appId"), param.getInteger("key"), param.getString("thirdId"), param.getString("dismissal"));
	}
}
