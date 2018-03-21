package cn.com.clubank.sms.manage.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.sys.pojo.SysMenu;

public interface SysMenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysMenu record);

    int insertSelective(SysMenu record);

    SysMenu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysMenu record);

    int updateByPrimaryKey(SysMenu record);
    
    /**
     * 按菜单名称模糊查询，状态查询到的条数
     * @param menuName 菜单名称
     * @param enabledMark 状态：有效标志（1-有效、0-无效）
     * @return
     */
    int selectTotal(@Param("menuName")String menuName, @Param("enabledMark")Integer enabledMark);
    
    /**
     * 按菜单名称模糊查询，状态查询菜单列表并分页
     * @param menuName 菜单名称
     * @param enabledMark 状态：有效标志（1-有效、0-无效）
     * @param startIndex 起始索引
     * @param pageSize 页容量
     * @return
     */
    List<SysMenu> selectMenuListAndPage(@Param("menuName")String menuName, @Param("enabledMark")Integer enabledMark, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 删除菜单：将删除标记设置为1
     * @param menuId 菜单id
     * @return
     */
    int updateDeleteMark(Integer menuId);
    
    /**
     * 设置菜单状态（有效标志：1-有效，0-无效）
     * @param menuId 菜单id
     * @param enabledMark 有效标志：1-有效，0-无效
     * @return
     */
    int updateEnabledMark(@Param("menuId")Integer menuId, @Param("enabledMark")Integer enabledMark);

    /**
     * 查询父菜单
     */
    List<Map<String, Object>> selectParentMenu();
    
    /**
     * 查询所有子菜单
     */
    List<Map<String, Object>> selectAllSubmenu();
    
    /**
     * 通过角色sorts，获得角色具有权限的父菜单列表
     * @param roles 角色sorts
     * @return
     */
    List<SysMenu> selectParentMenuByRoleSorts(@Param("roles") String[] roles);
    
    /**
     * 通过角色sorts和父菜单id，获得角色具有权限的子菜单列表
     * @param roles 角色sorts
     * @return
     */
    List<SysMenu> selectChildMenuByParentAndRoleSorts(@Param("parentId") int parentId, @Param("roles") String[] roles);
}