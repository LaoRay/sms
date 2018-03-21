package cn.com.clubank.sms.manage.sys.service;

import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.sys.pojo.SysRole;

/**
 * 角色管理
 * @author Liangwl
 *
 */
public interface RoleService {

	/**
	 * 查询角色列表：分页查询
	 * @param roleName 角色名称
	 * @param enabledMark 状态：有效标志（1-有效、0-无效）
	 * @param pageIndex 页码下标
	 * @param pageSize  页容量
	 * @return
	 */
	ResponseJson selectRoleListAndPage(String roleName, Integer enabledMark, Integer pageIndex, Integer pageSize);
	
	/**
	 * 新增、编辑角色
	 * @param role
	 * @return
	 */
	ResponseJson insertOrUpdateRole(SysRole role);
	
	/**
	 * 获取角色详情
	 * @param roleId 角色id
	 * @return
	 */
	ResponseJson selectRoleDetail(Integer roleId);
	
	/**
	 * 给角色分配菜单时获得菜单列表
	 * @param roleId 角色id
	 * @return
	 */
	ResponseJson selectMenus(Integer roleId);
	
	/**
	 * 分配菜单
	 * @param roleId 角色id
	 * @param menuIds 菜单id数组
	 * @return
	 */
	ResponseJson assigningMenus(String roleId, JSONArray menuIds);
	
	/**
	 * 删除角色
	 * @param roleIds 角色id数组
	 * @return
	 */
	ResponseJson deleteRole(JSONArray roleIds);
	
	/**
	 * 设置角色状态（有效标志：1-有效，0-无效）
	 * @param roleId 角色id
	 * @param enabledMark 有效标志：1-有效，0-无效
	 * @return
	 */
	ResponseJson setstate(Integer roleId, Integer enabledMark);
	
	/**
	 * 获得角色下菜单列表
	 * @param roles 角色序号，逗号间隔
	 * @return
	 */
	ResponseJson selectRoleDownMenuList(String roles);
	
	/**
	 * 查询角色信息（编辑用）
	 * @param roleId 角色id
	 * @return
	 */
	ResponseJson selectRoleInfo(Integer roleId);
	
	/**
	 * 通过角色序号获的角色信息
	 * @param sort 角色序号
	 * @return
	 */
	ResponseJson selectRoleInfoBySort(Integer sort);
}
