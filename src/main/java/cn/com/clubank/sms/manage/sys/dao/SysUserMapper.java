package cn.com.clubank.sms.manage.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.sys.pojo.SysUser;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);
    
    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    Map<String,Object> selectUserInfo(Integer userId);
    
    /**
     * 删除用户：将删除标记设置为1
     * @param userId 用户id
     * @return
     */
    int updateDeleteMark(Integer userId);
    
    /**
     * 通过登录账号、姓名、手机模糊查询，状态查询到的总条数
     * @param loginName 登录账号
     * @param realname 姓名
     * @param mobile 手机
     * @param enabledMark 状态：有效标志（1-有效、0-无效）
     * @return
     */
    int selectTotal(@Param("loginName")String loginName, @Param("realname")String realname, @Param("mobile")String mobile, @Param("enabledMark")Integer enabledMark);
    
    /**
     * 通过登录账号、姓名、手机模糊查询，状态查询用户列表并分页
     * @param loginName 登录账号
     * @param realname 姓名
     * @param mobile 手机
     * @param enabledMark 状态：有效标志（1-有效、0-无效）
     * @param startIndex 起始索引
     * @param pageSize 页容量
     * @return
     */
    List<SysUser> selectUserListAndPage(@Param("loginName")String loginName, @Param("realname")String realname, @Param("mobile")String mobile, @Param("enabledMark")Integer enabledMark, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 设置用户状态（有效标志：1-有效，0-无效）
     * @param userId 用户id
     * @param enabledMark 有效标志：1-有效，0-无效
     * @return
     */
    int updateEnabledMark(@Param("userId")Integer userId, @Param("enabledMark")Integer enabledMark);
    
    /**
     * 通过账户id，获得账户下用户列表
     * @param accountId 账户id
     * @return
     */
    List<SysUser> selectUserListByAccountId(String accountId);
    
    /**
     * 通过登录账号，获得用户信息
     * @param loginName 登录账号
     * @return
     */
    SysUser selectUserInfoByLoginName(String loginName);
}