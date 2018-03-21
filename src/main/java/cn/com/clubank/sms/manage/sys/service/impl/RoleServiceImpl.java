package cn.com.clubank.sms.manage.sys.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.Constants;
import cn.com.clubank.sms.common.utils.PageObject;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.sys.dao.SysMenuMapper;
import cn.com.clubank.sms.manage.sys.dao.SysRoleMapper;
import cn.com.clubank.sms.manage.sys.dao.SysRoleMenuMapper;
import cn.com.clubank.sms.manage.sys.dao.SysUserRoleMapper;
import cn.com.clubank.sms.manage.sys.pojo.SysMenu;
import cn.com.clubank.sms.manage.sys.pojo.SysRole;
import cn.com.clubank.sms.manage.sys.pojo.SysRoleMenu;
import cn.com.clubank.sms.manage.sys.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private SysRoleMenuMapper sysRoleMenuMapper;
	@Autowired
	private SysMenuMapper sysMenuMapper;

	/**
	 * 查询角色列表：分页查询
	 */
	@Override
	public ResponseJson selectRoleListAndPage(String roleName, Integer enabledMark, Integer pageIndex, Integer pageSize) {
		PageObject<SysRole> page = new PageObject<>(pageIndex, pageSize);
		int total = sysRoleMapper.selectTotal(roleName, enabledMark);
		List<SysRole> list = sysRoleMapper.selectRoleListAndPage(roleName, enabledMark, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

	/**
	 * 查询角色信息（编辑用）
	 */
	@Override
	public ResponseJson selectRoleInfo(Integer roleId) {
		if(StringUtil.isBlank(roleId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SysRole role = sysRoleMapper.selectByPrimaryKey(roleId);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, role, "查询成功");
	}
	
	/**
	 * 新增、编辑角色
	 */
	@Override
	public ResponseJson insertOrUpdateRole(SysRole role) {
		if(role == null){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		String msg = null;

		//角色主键为空，执行新增。反之，执行编辑。
		if(StringUtil.isBlank(role.getId())){
			role.setSort(sysRoleMapper.selectNumber() + 1);
			role.setCreateDate(new Date());
			sysRoleMapper.insertSelective(role);
			msg = "新增成功";
		}else{
			role.setModifyDate(new Date());
			sysRoleMapper.updateByPrimaryKeySelective(role);
			msg = "编辑成功";
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, msg);
	}

	/**
	 * 获取角色详情
	 */
	@Override
	public ResponseJson selectRoleDetail(Integer roleId) {
		if(StringUtil.isBlank(roleId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SysRole role = sysRoleMapper.selectByPrimaryKey(roleId);
		if(role == null){
			return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "查询为空");
		}
		HashMap<String,Object> map = new HashMap<>();
		map.put("roleInfo", role);
		List<Map<String, Object>> list = sysUserRoleMapper.selectRoleUsers(String.valueOf(roleId));
		map.put("theirUser", list);//角色下用户
		
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "查询成功");
	}

	/**
	 * 给角色分配菜单时获得菜单列表
	 */
	@Override
	public ResponseJson selectMenus(Integer roleId) {
		if(StringUtil.isBlank(roleId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//获得此角色拥有的菜单
		List<Map<String, Object>> roleMenuList = sysRoleMenuMapper.selectRoleDownMenuList(String.valueOf(roleId));
		//获得所有子菜单
		List<Map<String, Object>> allSubmenuList = sysMenuMapper.selectAllSubmenu();
		if(roleMenuList == null || roleMenuList.isEmpty()){
			for (Map<String, Object> map : allSubmenuList) {
				map.put("flag", false);
			}
		}
		for (Map<String, Object> allMap : allSubmenuList) {
			allMap.put("flag", false);
			for (Map<String, Object> map : roleMenuList) {
				if(map.get("menuId").toString().equals(allMap.get("id").toString())){
					allMap.put("flag", true);
				}
			}
		}
		//获得父菜单名
		for (Map<String, Object> map : allSubmenuList) {
			SysMenu menu = sysMenuMapper.selectByPrimaryKey((Integer) map.get("parentId"));
			map.put("parentName", menu.getMenuName());
		}
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, allSubmenuList, "查询成功");
	}
	
	/**
	 * 给角色分配菜单
	 */
	@Override
	public ResponseJson assigningMenus(String roleId, JSONArray menuIds) {
		if(StringUtil.isBlank(roleId) || menuIds == null || menuIds.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		if(sysRoleMapper.selectByPrimaryKey(Integer.parseInt(roleId)).getSort() == 99){
			return ResponseUtil.buildJson(ResultCode.FAIL, "此为系统管理员所拥有的菜单，只可查看");
		}
		//先删除此角色下关联的菜单
		sysRoleMenuMapper.deleteByRoleId(roleId);
		
		List<SysRoleMenu> list = JSON.parseArray(menuIds.toJSONString(), SysRoleMenu.class);
		for (SysRoleMenu sysRoleMenu : list) {
			sysRoleMenu.setRoleId(roleId);
		}
		sysRoleMenuMapper.insertBatch(list);
		
		return ResponseUtil.buildJson(ResultCode.SUCC, "分配菜单成功");
	}

	/**
	 * 删除角色
	 */
	@Override
	public ResponseJson deleteRole(JSONArray roleIds) {
		if(roleIds == null || roleIds.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		List<SysRole> userList=JSON.parseArray(roleIds.toJSONString(), SysRole.class);
		for (SysRole sysRole : userList) {
			//1.获得角色序号
			Integer sort = sysRoleMapper.selectByPrimaryKey(sysRole.getId()).getSort();
			//2.判断是否为初始角色（1-系统管理员，2-操作员）
			if(sort == Constants.INT_1 || sort == Constants.INT_2){
				return ResponseUtil.buildJson(ResultCode.FAIL, "初始角色不可删除");
			}
			sysRoleMapper.updateDeleteMark(sysRole.getId());
			//删除用户角色关系表中数据
			sysUserRoleMapper.deleteByRoleId(String.valueOf(sysRole.getId()));
			//删除角色菜单关系表中数据
			sysRoleMenuMapper.deleteByRoleId(String.valueOf(sysRole.getId()));
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, "删除成功");
	}

	/**
	 * 设置角色状态（有效标志：1-有效，0-无效）
	 */
	@Override
	public ResponseJson setstate(Integer roleId, Integer enabledMark) {
		if(StringUtil.isBlank(roleId) || StringUtil.isBlank(enabledMark)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//1.获得角色序号
		Integer sort = sysRoleMapper.selectByPrimaryKey(roleId).getSort();
		//2.判断是否为初始角色（1-系统管理员，2-操作员）
		if(sort == Constants.INT_1 || sort == Constants.INT_2){
			return ResponseUtil.buildJson(ResultCode.FAIL, "初始角色不可设置无效");
		}
		sysRoleMapper.updateEnabledMark(roleId, enabledMark);
		return ResponseUtil.buildJson(ResultCode.SUCC, "设置成功");
	}

	/**
	 * 获得角色下菜单列表
	 */
	@Override
	public ResponseJson selectRoleDownMenuList(String roles) {
		if(StringUtil.isBlank(roles)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		// 结果
		JSONArray result = new JSONArray();
		// 父菜单对象
		JSONObject parentJson = null;
		// 子菜单列表
		JSONArray childJsons = null;
		// 子菜单对象
		JSONObject childJson = null;
		// 拥有访问权限的父类菜单
		List<SysMenu> parents = sysMenuMapper.selectParentMenuByRoleSorts(roles.split(","));
		for (SysMenu parentMenu : parents) {
			parentJson = new JSONObject();
			//父菜单下的子菜单列表
			childJsons = new JSONArray();
			List<SysMenu> childs = sysMenuMapper.selectChildMenuByParentAndRoleSorts(parentMenu.getId(), roles.split(","));
			for (SysMenu childMenu : childs) {
				//子菜单对象
				childJson = new JSONObject();
				childJson.put("menuName", childMenu.getMenuName());
				childJson.put("menuUrl", childMenu.getMenuUrl());
				childJson.put("sort", childMenu.getSort());
				childJson.put("parentId", childMenu.getParentId());
				childJsons.add(childJson);
			}
			parentJson.put("childs", childJsons);
			parentJson.put("menuName", parentMenu.getMenuName());
			result.add(parentJson);
		}
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, result, "查询成功");
	}

	/**
	 * 通过角色序号获的角色信息
	 */
	@Override
	public ResponseJson selectRoleInfoBySort(Integer sort) {
		if(StringUtil.isBlank(sort)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SysRole role = sysRoleMapper.selectRoleInfoBySort(sort);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, role, "查询成功");
	}

}
