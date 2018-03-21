package cn.com.clubank.sms.manage.sys.service;

import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.sys.pojo.SysMenu;

/**
 * 菜单管理
 * @author Liangwl
 *
 */
public interface MenuService {

	/**
	 * 查询菜单列表：分页查询
	 * @param menuName 菜单名称
	 * @param enabledMark 状态：有效标志（1-有效、0-无效）
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 * @return
	 */
	ResponseJson selectMenuListAndPage(String menuName, Integer enabledMark, Integer pageIndex, Integer pageSize);
	
	/**
	 * 获得父菜单
	 * @return
	 */
	ResponseJson selectParentMenu();
	
	/**
	 * 获得菜单信息(编辑用)
	 * @param menuId 菜单id
	 * @return
	 */
	ResponseJson selectMenuInfo(Integer menuId);
	
	/**
	 * 新增、编辑菜单
	 * @param menu
	 * @return
	 */
	ResponseJson insertOrUpdateMenu(SysMenu menu);
	
	/**
	 * 删除菜单
	 * @param menuIds 菜单id数组
	 * @return
	 */
	ResponseJson deleteMenu(JSONArray menuIds);
	
	/**
	 * 设置菜单状态（有效标志：1-有效，0-无效）
	 * @param menuId 菜单id
	 * @param enabledMark 有效标志：1-有效，0-无效
	 * @return
	 */
	ResponseJson setstate(Integer menuId, Integer enabledMark);
}
