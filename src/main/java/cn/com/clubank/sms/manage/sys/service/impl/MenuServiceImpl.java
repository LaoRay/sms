package cn.com.clubank.sms.manage.sys.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.PageObject;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.sys.dao.SysMenuMapper;
import cn.com.clubank.sms.manage.sys.dao.SysRoleMenuMapper;
import cn.com.clubank.sms.manage.sys.pojo.SysMenu;
import cn.com.clubank.sms.manage.sys.service.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	private SysMenuMapper sysMenuMapper;
	@Autowired
	private SysRoleMenuMapper sysRoleMenuMapper;
	
	/**
	 * 查询菜单列表：分页查询
	 */
	@Override
	public ResponseJson selectMenuListAndPage(String menuName, Integer enabledMark, Integer pageIndex, Integer pageSize) {
		PageObject<SysMenu> page = new PageObject<>(pageIndex, pageSize);
		int total = sysMenuMapper.selectTotal(menuName, enabledMark);
		List<SysMenu> menus = sysMenuMapper.selectMenuListAndPage(menuName, enabledMark, page.getStart(), pageSize);
		
		List<SysMenu> list = new ArrayList<>();
		for (SysMenu sysMenu : menus) {
			//通过父菜单id获得父菜单名称
			SysMenu menu = sysMenuMapper.selectByPrimaryKey(sysMenu.getParentId());
			sysMenu.setParentName(menu.getMenuName());
			list.add(sysMenu);
		}
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}
	
	/**
	 * 获得父菜单
	 */
	@Override
	public ResponseJson selectParentMenu() {
		List<Map<String, Object>> list = sysMenuMapper.selectParentMenu();
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, list, "查询成功");
	}

	/**
	 * 获得菜单信息(编辑用)
	 */
	@Override
	public ResponseJson selectMenuInfo(Integer menuId) {
		if(StringUtil.isBlank(menuId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SysMenu menu = sysMenuMapper.selectByPrimaryKey(menuId);
		if(menu == null){
			return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "查询为空");
		}
		SysMenu parentMenu = sysMenuMapper.selectByPrimaryKey(menu.getParentId());
		if(parentMenu == null){
			menu.setParentName(null);
		}else{
			menu.setParentName(parentMenu.getMenuName());
		}
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, menu, "查询成功");
	}

	/**
	 * 新增、编辑菜单
	 */
	@Override
	public ResponseJson insertOrUpdateMenu(SysMenu menu) {
		if(menu == null){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		String msg = null;
		//角色主键为空，执行新增。反之，执行编辑。
		if(StringUtil.isBlank(menu.getId())){
			menu.setCreateDate(new Date());
			sysMenuMapper.insertSelective(menu);
			msg = "新增成功";
		}else{
			menu.setModifyDate(new Date());//修改日期
			sysMenuMapper.updateByPrimaryKeySelective(menu);
			msg = "编辑成功";
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, msg);
	}

	/**
	 * 删除菜单
	 */
	@Override
	public ResponseJson deleteMenu(JSONArray menuIds) {
		if(menuIds == null || menuIds.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		List<SysMenu> userList=JSON.parseArray(menuIds.toJSONString(), SysMenu.class);
		for (SysMenu sysMenu : userList) {
			sysMenuMapper.updateDeleteMark(sysMenu.getId());
			//删除角色菜单关系表中数据
			sysRoleMenuMapper.deleteByMenuId(String.valueOf(sysMenu.getId()));
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, "删除成功");
	}

	/**
	 * 设置菜单状态（有效标志：1-有效，0-无效）
	 */
	@Override
	public ResponseJson setstate(Integer menuId, Integer enabledMark) {
		if(StringUtil.isBlank(menuId) || StringUtil.isBlank(enabledMark)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		sysMenuMapper.updateEnabledMark(menuId, enabledMark);
		return ResponseUtil.buildJson(ResultCode.SUCC, "设置成功");
	}

}
