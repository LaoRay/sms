package cn.com.clubank.sms.manage.app.service;

import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.app.pojo.AppInfo;

/**
 * 应用管理
 * @author Liangwl
 *
 */
public interface AppService {

	/**
	 * 查询应用列表：分页查询
	 * @param appSort 应用编号
	 * @param appName 应用名称
	 * @param key 1-（待审核）2-（待审核、审核通过、审核不通过）
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 * @return
	 */
	ResponseJson selectAppListAndPage(String accountId, String appSort, String appName, Integer key, Integer pageIndex, Integer pageSize);

	/**
	 * 获得应用信息（编辑用）
	 * @param appId 应用id
	 * @return
	 */
	ResponseJson selectAppInfo(Integer appId);
	
	/**
	 * 新增、编辑应用
	 * @param app
	 * @return
	 */
	ResponseJson insertOrUpdateApp(String userId, AppInfo app);
	
	/**
	 * 获得应用详情（根据登录用户的角色获得详情内容）
	 * @param appId 应用id
	 * @param userId 用户id
	 * @return
	 */
	ResponseJson selectAppDetail(Integer userId, Integer appId);
	
	/**
	 * 删除应用
	 * @param appIds 应用id数组
	 * @return
	 */
	ResponseJson deleteApp(JSONArray appIds);
}
