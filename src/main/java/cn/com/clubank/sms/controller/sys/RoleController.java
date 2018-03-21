package cn.com.clubank.sms.controller.sys;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.sys.pojo.SysRole;
import cn.com.clubank.sms.manage.sys.service.RoleService;

/**
 * 系统设置：角色管理
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("/role")
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	/**
	 * 按条件获得角色列表并分页
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/list" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson selectRoleListAndPage(@RequestBody JSONObject param){
		
		return roleService.selectRoleListAndPage(param.getString("roleName"), param.getInteger("enabledMark"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
	}
	
	/**
	 * 获得角色信息（编辑用）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/info" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainRoleInfo(@RequestBody JSONObject param){
		
		return roleService.selectRoleInfo(param.getInteger("roleId"));
	}
	
	/**
	 * 新增、编辑角色
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/save" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson addOrEditRole(@RequestBody SysRole role){
		
		return roleService.insertOrUpdateRole(role);
	}
	
	/**
	 * 获得角色详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/detail" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainRoleDetail(@RequestBody JSONObject param){
		
		return roleService.selectRoleDetail(param.getInteger("roleId"));
	}
	
	/**
	 * 给角色分配菜单时获得菜单列表
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/menus" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainMenus(@RequestBody JSONObject param){
		
		return roleService.selectMenus(param.getInteger("roleId"));
	}
	
	/**
	 * 给角色分配菜单
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/assigningMenus" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson assigningMenus(@RequestBody JSONObject param){
		
		return roleService.assigningMenus(param.getString("roleId"), param.getJSONArray("menuIds"));
	}
	
	/**
	 * 删除角色
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson deleteRole(@RequestBody JSONObject param){
		
		return roleService.deleteRole(param.getJSONArray("roleIds"));
	}
	
	/**
	 * 设置角色状态（1-有效，0-无效）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/setstate" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson setstate(@RequestBody JSONObject param){
		
		return roleService.setstate(param.getInteger("roleId"), param.getInteger("enabledMark"));
	}
	
	/**
	 * 获得角色下菜单列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/menusList" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainRoleDownMenus(HttpServletRequest request){
		return roleService.selectRoleDownMenuList(request.getHeader("roles"));
	}
}
