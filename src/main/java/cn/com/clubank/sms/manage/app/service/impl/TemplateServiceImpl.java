package cn.com.clubank.sms.manage.app.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.Constants;
import cn.com.clubank.sms.common.utils.PageObject;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.app.dao.AppInfoMapper;
import cn.com.clubank.sms.manage.app.dao.AppTemplateMapper;
import cn.com.clubank.sms.manage.app.pojo.AppInfo;
import cn.com.clubank.sms.manage.app.pojo.AppTemplate;
import cn.com.clubank.sms.manage.app.service.TemplateService;
import cn.com.clubank.sms.manage.sys.dao.SysUserMapper;
import cn.com.clubank.sms.manage.sys.pojo.BaseCodeRule;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;
import cn.com.clubank.sms.manage.sys.service.BaseCodeRuleService;

@Service
public class TemplateServiceImpl implements TemplateService {
	
	@Autowired
	private AppTemplateMapper appTemplateMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private AppInfoMapper appInfoMapper;
//	@Autowired
//	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private BaseCodeRuleService baseCodeRuleService;

	/**
	 * 查询模板列表：分页查询
	 */
	@Override
	public ResponseJson selectTemplateListAndPage(String accountId, String tempCode, String appSort, String appName,
			Integer key, Integer pageIndex, Integer pageSize) {
		PageObject<AppTemplate> page = new PageObject<>(pageIndex, pageSize);
		int total = appTemplateMapper.selectTotal(accountId, tempCode, appSort, appName, key);
		List<AppTemplate> list = appTemplateMapper.selectTemplateListAndPage(accountId, tempCode, appSort, appName, key, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

	/**
	 * 查询当前登录用户所属账户下审核通过的应用列表（下拉框）
	 */
	@Override
	public ResponseJson selectAppList(String userId) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数本能为空");
		}
		SysUser user = sysUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if(user == null){
			return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "查询为空");
		}
		List<Map<String, Object>> list = appInfoMapper.selectAppListByAccountId(user.getAccountId());
		return ResponseUtil.buildObjectJson(ResultCode.DATA_NOT_FOUND, list, "查询成功");
	}

	/**
	 * 查询模板信息(编辑用)
	 */
	@Override
	public ResponseJson selectTemplateInfo(Integer tempId) {
		if(StringUtil.isBlank(tempId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数本能为空");
		}
		AppTemplate template = appTemplateMapper.selectByPrimaryKey(tempId);
		return ResponseUtil.buildObjectJson(ResultCode.DATA_NOT_FOUND, template, "查询成功");
	}

	/**
	 * 新增、编辑模板
	 */
	@Override
	public ResponseJson insertOrUpdateTemplate(AppTemplate template) {
		if(template == null){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		
		AppInfo app = appInfoMapper.selectByPrimaryKey(Integer.parseInt(template.getAppId()));
		template.setAppName(app.getAppName());
		template.setAppSort(app.getAppSort());
		
		String msg = null;
		//模板主键为空，执行新增。反之，执行编辑。
		if(StringUtil.isBlank(template.getId())){
			template.setStatus(Constants.INT_0);//模版状态：0-待审核，1-审核通过，2-审核不通过
			template.setCreateDate(new Date());
			template.setTempCode(baseCodeRuleService.getCode(BaseCodeRule.TEMPCODE.getValue()));
			appTemplateMapper.insertSelective(template);
			msg = "新增成功";
		}else{
			template.setModifyDate(new Date());
			appTemplateMapper.updateByPrimaryKeySelective(template);
			msg = "编辑成功";
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, msg);
	}

	/**
	 * 获取模板详情（根据登录用户的角色获得详情内容）
	 */
	@Override
	public ResponseJson selectTemplateDetail(Integer userId, Integer tempId) {
		if(StringUtil.isBlank(userId) || StringUtil.isBlank(tempId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		AppTemplate template = appTemplateMapper.selectByPrimaryKey(tempId);
//		List<Map<String, Object>> list = sysUserRoleMapper.selectUserRoles(String.valueOf(userId));
//		Boolean flag = false;
//		for (Map<String, Object> map : list) {
//			if(Integer.parseInt(map.get("sort").toString()) == 99){//99-系统管理员，2-操作员
//				flag = true;
//			}
//		}
//		if(flag){
			return ResponseUtil.buildObjectJson(ResultCode.SUCC, template, "查询成功");
//		}else{
//			Map<String,Object> templateMap = new HashMap<>();
//			templateMap.put("tempCode", template.getTempCode());//模板编号
//			templateMap.put("appName", template.getAppName());//应用名称
//			templateMap.put("tempContent", template.getTempContent());//模板内容
//			templateMap.put("smsType", template.getSmsType());//短信类型
//			templateMap.put("description", template.getDescription());//备注
//			return ResponseUtil.buildObjectJson(ResultCode.SUCC, templateMap, "查询成功");
//		}
	}

	/**
	 * 删除模板
	 */
	@Override
	public ResponseJson deleteTemplate(JSONArray tempIds) {
		if(tempIds == null || tempIds.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		List<AppTemplate> list = JSON.parseArray(tempIds.toString(), AppTemplate.class);
		for (AppTemplate appTemplate : list) {
			appTemplateMapper.updateDeleteMark(appTemplate.getId());
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, "删除成功");
	}

}
