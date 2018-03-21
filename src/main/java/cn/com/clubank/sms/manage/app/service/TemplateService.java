package cn.com.clubank.sms.manage.app.service;

import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.app.pojo.AppTemplate;

/**
 * 模板管理
 * @author Liangwl
 *
 */
public interface TemplateService {

	/**
	 * 查询模板列表：分页查询
	 * @param tempCode 模板编号
	 * @param appSort 应用编号
	 * @param appName 应用名称
	 * @param key 1-（待审核）2-（待审核、审核通过、审核不通过）
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 * @return
	 */
	ResponseJson selectTemplateListAndPage(String accountId, String tempCode, String appSort, String appName, Integer key, Integer pageIndex, Integer pageSize);
	
	/**
	 * 查询当前登录用户所属账户下的应用列表（下拉框）
	 * @param userId 用户id
	 * @return
	 */
	ResponseJson selectAppList(String userId);
	
	/**
	 * 查询模板信息(编辑用)
	 * @param tempId 模板id
	 * @return
	 */
	ResponseJson selectTemplateInfo(Integer tempId);
	
	/**
	 * 新增、编辑模板
	 * @param template
	 * @return
	 */
	ResponseJson insertOrUpdateTemplate(AppTemplate template);
	
	/**
	 * 获取模板详情（根据登录用户的角色获得详情内容）
	 * @param tempId 模板id
	 * @param userId 用户id
	 * @return
	 */
	ResponseJson selectTemplateDetail(Integer userId, Integer tempId);
	
	/**
	 * 删除模板
	 * @param tempIds 模板id数组
	 * @return
	 */
	ResponseJson deleteTemplate(JSONArray tempIds);
}
