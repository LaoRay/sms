package cn.com.clubank.sms.manage.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.sys.pojo.SysRole;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);
    
    /**
     * 删除角色：将删除标记设置为1
     * @param roleId 角色id
     * @return
     */
    int updateDeleteMark(Integer roleId);
    
    /**
     * 按角色名称模糊查询，状态查询到的条数
     * @param roleName 角色名称
     * @param enabledMark 状态：有效标志（1-有效、0-无效）
     * @return
     */
    int selectTotal(@Param("roleName")String roleName, @Param("enabledMark")Integer enabledMark);
    
    /**
     * 按角色名称模糊查询，状态查询角色列表并分页
     * @param roleName 角色名称
     * @param enabledMark 状态：有效标志（1-有效、0-无效）
     * @param startIndex 起始索引
     * @param pageSize 页容量
     * @return
     */
    List<SysRole> selectRoleListAndPage(@Param("roleName")String roleName, @Param("enabledMark")Integer enabledMark, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 设置角色状态（有效标志：1-有效，0-无效）
     * @param roleId 角色id
     * @param enabledMark 有效标志：1-有效，0-无效
     * @return
     */
    int updateEnabledMark(@Param("roleId")Integer roleId, @Param("enabledMark")Integer enabledMark);
    
    /**
     * 查询所有角色列表
     * @return
     */
    List<Map<String, Object>> selectAllRoles();
    
    /**
     * 通过角色序号，获得角色信息
     * @param sort 角色序号
     * @return
     */
    SysRole selectRoleInfoBySort(Integer sort);
    
    /**
     * 统计角色个数
     * @return
     */
    int selectNumber();
}