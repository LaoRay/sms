package cn.com.clubank.sms.manage.app.service.impl;

import java.util.Date;
import java.util.List;

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
import cn.com.clubank.sms.manage.app.service.AppService;
import cn.com.clubank.sms.manage.sys.dao.SysUserMapper;
import cn.com.clubank.sms.manage.sys.pojo.BaseCodeRule;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;
import cn.com.clubank.sms.manage.sys.service.BaseCodeRuleService;

@Service
public class AppServiceImpl implements AppService {

	@Autowired
	private AppInfoMapper appInfoMapper;
//	@Autowired
//	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private BaseCodeRuleService baseCodeRuleService;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private AppTemplateMapper appTemplateMapper;
	
	/**
	 * 查询应用列表：分页查询
	 */
	@Override
	public ResponseJson selectAppListAndPage(String accountId, String appSort, String appName, Integer key, Integer pageIndex,
			Integer pageSize) {
		PageObject<AppInfo> page = new PageObject<>(pageIndex, pageSize);
		int total = appInfoMapper.selectTotal(accountId, appSort, appName, key);
		List<AppInfo> list = appInfoMapper.selectAppListAndPage(accountId, appSort, appName, key, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

	/**
	 * 获得应用信息（编辑用）
	 */
	@Override
	public ResponseJson selectAppInfo(Integer appId) {
		if(StringUtil.isBlank(appId)){
			return ResponseUtil.buildObjectJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		AppInfo app = appInfoMapper.selectByPrimaryKey(appId);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, app, "查询成功");
	}

	/**
	 * 新增、编辑应用
	 */
	@Override
	public ResponseJson insertOrUpdateApp(String userId, AppInfo app) {
		if(app == null){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//验证应用名称的唯一性
		AppInfo appInfo = appInfoMapper.selectAppInfoByAppName(app.getAppName());
		if(appInfo != null && !appInfo.getId().equals(app.getId())){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "应用名称已存在");
		}
		String msg = null;
		//应用主键为空，执行新增。反之，执行编辑。
		if(StringUtil.isBlank(app.getId())){
			SysUser user = sysUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
			app.setAccountId(user.getAccountId());//账户id
			app.setStatus(Constants.INT_0);//应用状态：0-待审核、1-审核通过、2-审核不通过
			app.setCreateDate(new Date());
			app.setAppSort(baseCodeRuleService.getCode(BaseCodeRule.APPCODE.getValue()));
			// 标识key
			app.setAppKey(StringUtil.get32UUID());
			app.setSendTotalQuantity(Constants.INT_0);//发送总条数
			appInfoMapper.insertSelective(app);
			msg = "新增成功";
		}else{
			app.setModifyDate(new Date());
			appInfoMapper.updateByPrimaryKeySelective(app);
			msg = "编辑成功";
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, msg);
	}
	
	/**
	 * 获得应用详情（根据登录用户的角色获得详情内容）
	 */
	@Override
	public ResponseJson selectAppDetail(Integer userId, Integer appId) {
		if(StringUtil.isBlank(userId) || StringUtil.isBlank(appId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		AppInfo app = appInfoMapper.selectByPrimaryKey(appId);
//		List<Map<String, Object>> list = sysUserRoleMapper.selectUserRoles(String.valueOf(userId));
//		Boolean flag = false;
//		for (Map<String, Object> map : list) {
//			if(Integer.parseInt(map.get("sort").toString()) == 99){//99-系统管理员，2-操作员
//				flag = true;
//			}
//		}
//		if(flag){
			return ResponseUtil.buildObjectJson(ResultCode.SUCC, app, "查询成功");
//		}else{
//			Map<String,Object> appMap = new HashMap<>();
//			appMap.put("id", app.getId());//应用id
//			appMap.put("appSort", app.getAppSort());//应用编号
//			appMap.put("appName", app.getAppName());//应用名称
//			appMap.put("signature", app.getSignature());//应用签名
//			appMap.put("description", app.getDescription());//备注
//			return ResponseUtil.buildObjectJson(ResultCode.SUCC, appMap, "查询成功");
//		}
	}
	
	/**
	 * 删除应用
	 */
	@Override
	public ResponseJson deleteApp(JSONArray appIds) {
		if(appIds == null || appIds.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		List<AppInfo> appList=JSON.parseArray(appIds.toJSONString(), AppInfo.class);
		for (AppInfo appInfo : appList) {
			List<AppTemplate> list = null;
			list = appTemplateMapper.selectTempListByAppId(String.valueOf(appInfo.getId()));
			if(list != null && !list.isEmpty()){
				return ResponseUtil.buildJson(ResultCode.FAIL, "应用：【" + list.get(0).getAppName() + "】在使用中不可删除");
			}
			appInfoMapper.updateDeleteMark(appInfo.getId());
		}		
		return ResponseUtil.buildJson(ResultCode.SUCC, "删除成功");
	}

}
