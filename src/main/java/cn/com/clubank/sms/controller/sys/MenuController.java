package cn.com.clubank.sms.controller.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.sys.pojo.SysMenu;
import cn.com.clubank.sms.manage.sys.service.MenuService;

/**
 * 系统设置：菜单管理
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("/menu")
public class MenuController {
	
	@Autowired
	private MenuService menuService;
	
	/**
	 * 按条件获得菜单列表并分页
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/list" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson selectMenuListAndPage(@RequestBody JSONObject param){
		
		return menuService.selectMenuListAndPage(param.getString("menuName"), param.getInteger("enabledMark"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
	}
	
	/**
	 * 获得父菜单
	 * @return
	 */
	@RequestMapping(value = "/parent" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainParentMenu(){
		
		return menuService.selectParentMenu();
	}
	
	/**
	 * 获得菜单信息(编辑用)
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/info" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainMenuInfo(@RequestBody JSONObject param){
		
		return menuService.selectMenuInfo(param.getInteger("menuId"));
	}
	
	/**
	 * 新增、编辑菜单
	 * @param menu
	 * @return
	 */
	@RequestMapping(value = "/save" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson addOrEditMenu(@RequestBody SysMenu menu){
		
		return menuService.insertOrUpdateMenu(menu);
	}
	
	/**
	 * 删除菜单
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson deleteMenu(@RequestBody JSONObject param){
		
		return menuService.deleteMenu(param.getJSONArray("menuIds"));
	}
	
	/**
	 * 设置菜单状态（1-有效，0-无效）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/setstate" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson setstate(@RequestBody JSONObject param){
		
		return menuService.setstate(param.getInteger("menuId"), param.getInteger("enabledMark"));
	}
}
