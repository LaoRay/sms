package cn.com.clubank.sms.manage.sys.dao;

import java.util.List;
import java.util.Map;

import cn.com.clubank.sms.manage.sys.pojo.SysRoleMenu;

public interface SysRoleMenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleMenu record);

    int insertSelective(SysRoleMenu record);

    SysRoleMenu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleMenu record);

    int updateByPrimaryKey(SysRoleMenu record);
    
    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertBatch(List<SysRoleMenu> list);
    
    /**
     * 通过角色id，删除角色菜单关系表中数据
     * @param roleId 角色id
     * @return
     */
    int deleteByRoleId(String roleId);
    
    /**
     * 通过菜单id，删除角色菜单关系表中数据
     * @param menuId 菜单id
     * @return
     */
    int deleteByMenuId(String menuId);
    
    /**
     * 通过角色id，获得角色下菜单列表
     * @param roleId 角色id
     * @return
     */
    List<Map<String,Object>> selectRoleDownMenuList(String roleId);
    
}