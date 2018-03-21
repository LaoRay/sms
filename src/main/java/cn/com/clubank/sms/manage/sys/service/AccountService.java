package cn.com.clubank.sms.manage.sys.service;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;

/**
 * 账户管理
 * @author Liangwl
 *
 */
public interface AccountService {

	/**
	 * 查询用户列表：分页查询
	 * @param loginName 登录账号
	 * @param realname 姓名
	 * @param mobile 手机
	 * @param enabledMark 用户状态：有效标志（1-有效、0-无效）
	 * @param pageIndex 页码下标
	 * @param pageSize  页容量
	 * @return
	 */
	ResponseJson selectUserListAndPage(String loginName, String realname, String mobile, Integer enabledMark, Integer pageIndex, Integer pageSize);
	
	/**
	 * 获得用户信息（编辑用）
	 * @param userId 用户id
	 * @return
	 */
	ResponseJson selectUserInfo(Integer userId);
	
	/**
	 * 新增、编辑用户
	 * @param user
	 * @return
	 */
	ResponseJson insertOrUpdateUser(SysUser user);
	
	/**
	 * 获取用户详情
	 * @param userId 用户id
	 * @return
	 */
	ResponseJson selectUserDetail(Integer userId);
	
	/**
	 * 修改密码
	 * @param userId 用户id
	 * @param password 密码
	 * @return
	 */
	ResponseJson updateUserPassword(Integer userId, String password);
	
	/**
	 * 给用户分配角色时获得角色列表
	 * @param userId 角色id
	 * @return
	 */
	ResponseJson selectRoles(Integer userId);
	
	/**
	 * 分配角色
	 * @param userId 用户id
	 * @param roleIds 角色id数组
	 * @return
	 */
	ResponseJson assigningRoles(String userId, JSONArray roleIds);
	
	/**
	 * 删除用户
	 * @param userIds 用户id数组
	 * @return
	 */
	ResponseJson deleteUser(JSONArray userIds);
	
	/**
	 * 设置用户状态（有效标志：1-有效，0-无效）
	 * @param userId 用户id
	 * @param enabledMark 有效标志：1-有效，0-无效
	 * @return
	 */
	ResponseJson setstate(Integer userId, Integer enabledMark);
	
	/**
	 * 获得用户所属账户余额
	 * @param userId 用户id
	 * @return
	 */
	ResponseJson selectAccountBalance(Integer userId);
	
	/**
	 * 充值
	 * @param userId 用户id
	 * @param rechargeAmount 充值金额
	 * @param singlePrice 短信单价
	 * @return
	 */
	ResponseJson recharge(Integer userId, BigDecimal rechargeAmount, BigDecimal singlePrice);
	
	/**
	 * 查询账户充值记录：分页查询
	 * @param accountName 账户名称
	 * @param dateOne 充值时间1
	 * @param dateTwo 充值时间2
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 * @return
	 */
	ResponseJson selectRechargeListAndPage(String accountName, String dateOne , String dateTwo, Integer pageIndex, Integer pageSize);
	
	/**
	 * 用户登录
	 * @param loginName 登录账号
	 * @param password 登录密码
	 * @return
	 */
	ResponseJson userLogin(String loginName, String password);
	
	/**
	 * 用户注销
	 * @param token
	 * @return
	 */
	ResponseJson userLogout(String token);
	
	/**
	 * 概况（管理员首页）
	 * @return
	 */
	ResponseJson selectAllAccountNumber();
	
	/**
	 * 查询账户列表（管理员首页）
	 * @return
	 */
	ResponseJson selectAccountList();
}
