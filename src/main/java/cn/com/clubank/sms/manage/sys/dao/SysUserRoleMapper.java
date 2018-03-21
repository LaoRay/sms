package cn.com.clubank.sms.manage.sys.dao;

import java.util.List;
import java.util.Map;

import cn.com.clubank.sms.manage.sys.pojo.SysUserRole;

public interface SysUserRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserRole record);
    
    int insertBatch(List<SysUserRole> list);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);
    
    /**
     * 查询用户所属角色
     * @param userId 用户id
     * @return
     */
    List<Map<String,Object>> selectUserRoles(String userId);
    
    /**
     * 查询角色下用户
     * @param roleId 角色id
     * @return
     */
    List<Map<String,Object>> selectRoleUsers(String roleId);
    
    /**
     * 通过用户id，删除用户角色关系表中数据
     * @param userId 用户id
     * @return
     */
    int deleteByUserId(String userId);
    
    /**
     * 通过角色id，删除用户角色关系表中数据
     * @param roleId 角色id
     * @return
     */
    int deleteByRoleId(String roleId);
}