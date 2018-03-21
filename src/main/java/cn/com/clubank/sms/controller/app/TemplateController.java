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
import cn.com.clubank.sms.manage.app.pojo.AppTemplate;
import cn.com.clubank.sms.manage.app.service.TemplateAuditService;
import cn.com.clubank.sms.manage.app.service.TemplateService;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;
import cn.com.clubank.sms.manage.sys.service.AccountService;

/**
 * 模板管理
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("template")
public class TemplateController {

	@Autowired
	private TemplateService templateService;
	@Autowired
	private TemplateAuditService templateAuditService;
	@Autowired
	private AccountService accountService;
	
	/**
	 * 查询模板列表：分页查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainTemplateListPage(HttpServletRequest request, @RequestBody JSONObject param){
		if(request.getHeader("roles").indexOf(Constants.Sort_99) != -1){//99-系统管理员
			return templateService.selectTemplateListAndPage(null, param.getString("tempCode"), param.getString("appSort"), param.getString("appName"), param.getInteger("key"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
		}
		ResponseObjectJson roj = (ResponseObjectJson) accountService.selectUserInfo(request.getIntHeader("userId"));
		SysUser user = (SysUser) roj.getData(); 
		return templateService.selectTemplateListAndPage(user.getAccountId(), param.getString("tempCode"), param.getString("appSort"), param.getString("appName"), param.getInteger("key"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
	}
	
	/**
	 * 获得审核通过应用列表（下拉框）
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/apps", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainApps(HttpServletRequest request){
		 
		return templateService.selectAppList(request.getHeader("userId"));
	}
	
	/**
	 * 获得模板信息（编辑用）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/info", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainTemplateInfo(@RequestBody JSONObject param){
		 
		return templateService.selectTemplateInfo(param.getInteger("tempId"));
	}
	
	/**
	 * 新增、编辑模板
	 * @param template
	 * @return
	 */
	@RequestMapping(value = "/save" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson addOrEditTemplate(@RequestBody AppTemplate template){
		
		return templateService.insertOrUpdateTemplate(template);
	}
	
	/**
	 * 获得模板详情（根据登录用户的角色获得详情内容）
	 * @param request
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/detail" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainTemplateDetail(HttpServletRequest request, @RequestBody JSONObject param){
		
		return templateService.selectTemplateDetail(request.getIntHeader("userId"), param.getInteger("tempId"));
	}
	
	/**
	 * 删除模板
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson deleteTemplate(@RequestBody JSONObject param){
		
		return templateService.deleteTemplate(param.getJSONArray("tempIds"));
	}
	
	/**
	 * 审核模板
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/audit" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson auditTemplate(@RequestBody JSONObject param){
		
		return templateAuditService.auditTemplate(param.getInteger("tempId"), param.getInteger("key"), param.getString("templateId"), param.getString("dismissal"));
	}
}
