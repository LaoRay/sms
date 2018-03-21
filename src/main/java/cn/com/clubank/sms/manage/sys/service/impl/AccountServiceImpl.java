package cn.com.clubank.sms.manage.sys.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.redis.JedisClient;
import cn.com.clubank.sms.common.redis.RedisKeyCode;
import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.Constants;
import cn.com.clubank.sms.common.utils.DESUtils;
import cn.com.clubank.sms.common.utils.ObjectUtils;
import cn.com.clubank.sms.common.utils.PageObject;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.app.dao.AppInfoMapper;
import cn.com.clubank.sms.manage.app.dao.AppTemplateMapper;
import cn.com.clubank.sms.manage.sys.dao.AccountInfoMapper;
import cn.com.clubank.sms.manage.sys.dao.AccountRechargeMapper;
import cn.com.clubank.sms.manage.sys.dao.SysRoleMapper;
import cn.com.clubank.sms.manage.sys.dao.SysUserMapper;
import cn.com.clubank.sms.manage.sys.dao.SysUserRoleMapper;
import cn.com.clubank.sms.manage.sys.pojo.AccountInfo;
import cn.com.clubank.sms.manage.sys.pojo.AccountRecharge;
import cn.com.clubank.sms.manage.sys.pojo.BaseCodeRule;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;
import cn.com.clubank.sms.manage.sys.pojo.SysUserRole;
import cn.com.clubank.sms.manage.sys.service.AccountService;
import cn.com.clubank.sms.manage.sys.service.BaseCodeRuleService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private AccountInfoMapper accountInfoMapper;
	@Autowired
	private AccountRechargeMapper accountRechargeMapper;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private BaseCodeRuleService baseCodeRuleService;
	@Autowired
	private AppInfoMapper appInfoMapper;
	@Autowired
	private AppTemplateMapper appTemplateMapper;
	
	@Value("${token.expire}")
	private Integer token_expire;

	/**
	 * 查询用户列表：分页查询
	 */
	@Override
	public ResponseJson selectUserListAndPage(String loginName, String realname, String mobile, Integer enabledMark, Integer pageIndex, Integer pageSize) {
		PageObject<SysUser> page = new PageObject<>(pageIndex, pageSize);
		int total = sysUserMapper.selectTotal(loginName, realname, mobile, enabledMark);
		List<SysUser> list = sysUserMapper.selectUserListAndPage(loginName, realname, mobile, enabledMark, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}
	
	/**
	 * 获得用户信息（编辑用）
	 */
	@Override
	public ResponseJson selectUserInfo(Integer userId) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		//对密码进行解密
		user.setPassword(DESUtils.smsDecode(user.getPassword()));
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, user, "查询成功");
	}

	/**
	 * 新增、编辑用户
	 */
	@Override
	public ResponseJson insertOrUpdateUser(SysUser user) {	
		if(user == null){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//验证登录账号的唯一性
		SysUser sysUser = sysUserMapper.selectUserInfoByLoginName(user.getLoginName());
		if(sysUser != null && !sysUser.getId().equals(user.getId())){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "登录账号不唯一");
		}
		// TODO 验证邮箱格式是否正确
		if(!user.getEmail().matches("[a-zA-Z0-9_]+@[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)+")){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "邮箱格式不正确");
		}
		//对登录密码进行加密
		user.setPassword(DESUtils.smsEncode(user.getPassword()));
		String msg = null;
		//用户主键为空，执行新增。反之，执行编辑。
		if(StringUtil.isBlank(user.getId())){
			
			//验证鉴权账号的唯一性
//			if(accountInfoMapper.selectByAuthAccount(account.getAuthAccount()) != null){
//				return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "鉴权账号不唯一");
//			}
			
			//账户信息表对象
			AccountInfo account = new AccountInfo();
			// 账户名称
			account.setAccountName(user.getLoginName());
			// 账户编号
			account.setAccountSort(baseCodeRuleService.getCode(BaseCodeRule.ACCOUNT.getValue()));
			
			// 鉴权账号
			account.setAuthAccount(baseCodeRuleService.getCode(BaseCodeRule.AUTH_ACCOUNT.getValue()));
			// 对鉴权密码进行加密
			account.setAuthPwd(DESUtils.smsEncode(account.getAuthAccount()));
			
			account.setCreateDate(new Date());
			//向账户信息表添加数据
			accountInfoMapper.insertSelective(account);
			//获得账户信息的id
			user.setAccountId(String.valueOf(accountInfoMapper.selectByAccountName(account.getAccountName()).getId()));
			user.setCreateDate(new Date());
			//向用户信息表添加数据
			sysUserMapper.insertSelective(user);
			msg = "新增成功";
		}else{
			user.setModifyDate(new Date());
			//更新用户信息表中数据
			sysUserMapper.updateByPrimaryKeySelective(user);
			msg = "编辑成功";
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, msg);
	}

	/**
	 * 获取用户详情
	 */
	@Override
	public ResponseJson selectUserDetail(Integer userId) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		Map<String, Object> map=sysUserMapper.selectUserInfo(userId);
		map.put("password", DESUtils.smsDecode((String) map.get("password")));
		List<Map<String, Object>> roleList = sysUserRoleMapper.selectUserRoles(String.valueOf(userId));
		map.put("theirRole", roleList);//所属角色
		if(map == null || map.isEmpty()){
			return ResponseUtil.buildObjectJson(ResultCode.DATA_NOT_FOUND, map, "查询为空");
		}
		List<AccountRecharge> rechargeList = accountRechargeMapper.selectRechargeListByAccountId((String) map.get("accountId"));
		//总余额
		BigDecimal totalBalance = new BigDecimal(0.00);
		if(rechargeList == null || rechargeList.isEmpty()){
			map.put("totalBalance", totalBalance);
		}else{
			for (AccountRecharge accountRecharge : rechargeList) {
				//每条记录的余额：充值金额-（已发送条数*短信单价）
				accountRecharge.setBalance(accountRecharge.getRechargeAmount().subtract(accountRecharge.getSinglePrice().multiply(new BigDecimal(accountRecharge.getSendQuantity()))));
				//总余额
				totalBalance = totalBalance.add(accountRecharge.getBalance());
			}
			map.put("totalBalance", totalBalance);
		}
		//剩余总条数
		int surplusTotalQuantity = 0;
		surplusTotalQuantity = (int)map.get("smsTotalQuantity") + (int)map.get("handselTotalQuantity") - (int)map.get("sendTotalQuantity");
		map.put("surplusTotalQuantity", surplusTotalQuantity);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "查询成功");
	}

	/**
	 * 修改密码
	 */
	@Override
	public ResponseJson updateUserPassword(Integer userId, String password) {
		
		return null;
	}

	/**
	 * 给用户分配角色时获得角色列表
	 */
	@Override
	public ResponseJson selectRoles(Integer userId) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//获得用户所属角色
		List<Map<String, Object>> userRoleList = sysUserRoleMapper.selectUserRoles(String.valueOf(userId));
		//获得所有角色
		List<Map<String, Object>> allRoleList = sysRoleMapper.selectAllRoles();
		if(userRoleList == null || userRoleList.isEmpty()){
			for (Map<String, Object> map : allRoleList) {
				map.put("flag", false);
			}
			return ResponseUtil.buildObjectJson(ResultCode.SUCC, allRoleList, "查询成功");
		}
		for (Map<String, Object> allMap : allRoleList) {
			allMap.put("flag", false);
			for (Map<String, Object> map : userRoleList) {
				if(map.get("roleName").toString().equals(allMap.get("roleName").toString())){
					allMap.put("flag", true);
				}
			}
		}
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, allRoleList, "查询成功");
	}
	
	/**
	 * 分配角色
	 */
	@Override
	public ResponseJson assigningRoles(String userId, JSONArray roleIds) {
		if (StringUtil.isBlank(userId) || roleIds == null || roleIds.isEmpty()) {
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//先删除此用户下关联的角色
		sysUserRoleMapper.deleteByUserId(userId);
		
		List<SysUserRole> list=JSON.parseArray(roleIds.toJSONString(), SysUserRole.class);
		for (SysUserRole sysUserRole : list) {
			sysUserRole.setUserId(userId);
		}
		sysUserRoleMapper.insertBatch(list);
		
		return ResponseUtil.buildJson(ResultCode.SUCC, "分配角色成功");
	}

	/**
	 * 删除用户
	 */
	@Override
	public ResponseJson deleteUser(JSONArray userIds) {
		if(userIds == null || userIds.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		List<SysUser> userList=JSON.parseArray(userIds.toJSONString(), SysUser.class);
		for (SysUser sysUser : userList) {
			// TODO 判断此用户是否是账户下唯一用户
//			//1.获得用户信息
//			SysUser user = sysUserMapper.selectByPrimaryKey(sysUser.getId());
//			//2.通过账户id,获得账户下用户列表
//			List<SysUser> list = sysUserMapper.selectUserListByAccountId(user.getAccountId());
//			//3.判断此用户是否是账户下唯一用户
//			if(list != null && list.size() == Constants.INT_1){
//				return ResponseUtil.buildJson(ResultCode.FAIL, "此用户是账户下唯一用户不能删除");
//			}
			
			// TODO 删除账户信息表中数据
			//1.获得用户信息
			SysUser user = sysUserMapper.selectByPrimaryKey(sysUser.getId());
			//2.通过账户id,获得账户信息
			AccountInfo account = accountInfoMapper.selectByPrimaryKey(Integer.parseInt(user.getAccountId()));
			if(account.getRechargeTotal().intValue() != 0){
				return ResponseUtil.buildJson(ResultCode.FAIL, "账户：【"+account.getAccountName()+"】在使用不可删除");
			}
			accountInfoMapper.updateDeleteMark(Integer.parseInt(user.getAccountId()));
			
			sysUserMapper.updateDeleteMark(sysUser.getId());
			//删除用户角色关系表中数据
			sysUserRoleMapper.deleteByUserId(String.valueOf(sysUser.getId()));
		}
		return ResponseUtil.buildJson(ResultCode.SUCC, "删除成功");
	}

	/**
	 * 设置用户状态（有效标志：1-有效，0-无效）
	 */
	@Override
	public ResponseJson setstate(Integer userId, Integer enabledMark) {
		if(StringUtil.isBlank(userId) || StringUtil.isBlank(enabledMark)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//1.获得用户信息
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		//2.通过账户id,获得账户下用户列表
		List<SysUser> list = sysUserMapper.selectUserListByAccountId(user.getAccountId());
		//3.判断此用户是否是账户下唯一用户
		if(list != null && list.size() == Constants.INT_1){
			return ResponseUtil.buildJson(ResultCode.FAIL, "此用户是账户下唯一用户不能设置为无效");
		}
		sysUserMapper.updateEnabledMark(userId, enabledMark);
		return ResponseUtil.buildJson(ResultCode.SUCC, "设置成功");
	}
	
	/**
	 * 获得用户所属账户余额
	 */
	@Override
	public ResponseJson selectAccountBalance(Integer userId) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		//1.获得用户信息
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		List<AccountRecharge> rechargeList = accountRechargeMapper.selectRechargeListByAccountId(user.getAccountId());
		Map<String, Object> map = new HashMap<>();
		//总余额
		BigDecimal totalBalance = new BigDecimal(0.00);
		if(rechargeList == null || rechargeList.isEmpty()){
			map.put("totalBalance", totalBalance);
		}else{
			for (AccountRecharge accountRecharge : rechargeList) {
				//每条记录的余额：充值金额-（已发送条数*短信单价）
				accountRecharge.setBalance(accountRecharge.getRechargeAmount().subtract(accountRecharge.getSinglePrice().multiply(new BigDecimal(accountRecharge.getSendQuantity()))));
				//总余额
				totalBalance = totalBalance.add(accountRecharge.getBalance());
			}
			map.put("totalBalance", totalBalance);
		}
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "查询成功");
	}

	/**
	 * 充值
	 */
	@Override
	public ResponseJson recharge(Integer userId, BigDecimal rechargeAmount, BigDecimal singlePrice) {
		if(StringUtil.isBlank(userId) || StringUtil.isBlank(rechargeAmount.intValue()) || StringUtil.isBlank(singlePrice.intValue())){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "输入值不能为空");
		}
		if(rechargeAmount.compareTo(singlePrice) == -1){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "充值金额必须大于短信单价");
		}
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		AccountInfo account = accountInfoMapper.selectByPrimaryKey(Integer.parseInt(user.getAccountId()));
		//充值记录
		AccountRecharge recharg = new AccountRecharge();
		recharg.setSerialNumber(baseCodeRuleService.getCode(BaseCodeRule.SERIAL.getValue()));//充值流水号
		recharg.setAccountId(String.valueOf(account.getId()));//账户id
		recharg.setAccountName(account.getAccountName());//账户名称
		recharg.setRechargeAmount(rechargeAmount);//充值金额
		recharg.setSinglePrice(singlePrice);//短信单价
		recharg.setSmsQuantity(rechargeAmount.divide(singlePrice, 0, RoundingMode.HALF_UP).intValue());//充值条数
		// TODO 赠送条数
		recharg.setHandselQuantity(0);
		recharg.setCreateDate(new Date());//充值时间
		recharg.setSendQuantity(0);//已发送条数
		recharg.setLastHandselQuantity(recharg.getHandselQuantity());//剩余赠送条数
		recharg.setLastQuantity(recharg.getSmsQuantity());//剩余充值条数
		accountRechargeMapper.insertSelective(recharg);
		
		//账户信息
		account.setRechargeTotal(account.getRechargeTotal().add(rechargeAmount));//充值总额
		account.setSmsTotalQuantity(account.getSmsTotalQuantity() + recharg.getSmsQuantity());//短信总条数
		// TODO 赠送总条数
		account.setHandselTotalQuantity(account.getHandselTotalQuantity() + recharg.getHandselQuantity());
		accountInfoMapper.updateByPrimaryKeySelective(account);
		return ResponseUtil.buildJson(ResultCode.SUCC, "充值成功");
	}

	/**
	 * 查询账户充值记录：分页查询
	 */
	@Override
	public ResponseJson selectRechargeListAndPage(String accountName, String dateOne , String dateTwo, Integer pageIndex, Integer pageSize) {
		PageObject<AccountRecharge> page = new PageObject<>(pageIndex, pageSize);
		int total = accountRechargeMapper.selectTotal(accountName, dateOne, dateTwo);
		List<AccountRecharge> rechargeList = accountRechargeMapper.selectRechargeListAndPage(accountName, dateOne, dateTwo, page.getStart(), pageSize);
		
		ArrayList<AccountRecharge> list = null;
		if(rechargeList != null && !rechargeList.isEmpty()){
			list = new ArrayList<>();
			for (AccountRecharge accountRecharge : rechargeList) {
				//每条记录的余额：充值金额-（已发送条数*短信单价）
				if(StringUtil.isBlank(accountRecharge.getSendQuantity())){
					accountRecharge.setSendQuantity(0);//已发送条数
				}
				accountRecharge.setBalance(accountRecharge.getRechargeAmount().subtract(accountRecharge.getSinglePrice().multiply(new BigDecimal(accountRecharge.getSendQuantity()))));
				list.add(accountRecharge);
			}
		}
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

	/**
	 * 用户登录
	 */
	@Override
	public ResponseJson userLogin(String loginName, String password) {
		log.info("登录账号：loginName：{}，登录时间：loginDate：{}", loginName, new Date());
		SysUser user = sysUserMapper.selectUserInfoByLoginName(loginName);
		if(user == null){
			return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "账号不存在");
		}
		if(user.getEnabledMark() == Constants.INT_0){
			return ResponseUtil.buildJson(ResultCode.FAIL, "账号已无效");
		}
		List<Map<String, Object>> list = sysUserRoleMapper.selectUserRoles(String.valueOf(user.getId()));
		//判断用户有无权限
		if(list == null || list.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "您没有访问权限");
		}
		//判断密码是否正确
		if(!DESUtils.smsEncode(password).equals(user.getPassword())){
			return ResponseUtil.buildJson(ResultCode.ACCOUNT_ERROR, "密码错误");
		}
		String token = UUID.randomUUID().toString();
		//session中保存的内容
		JSONObject session = new JSONObject();
		session.put("userMobile", user.getMobile());//用户手机号
		session.put("userName", user.getRealname());//用户名
		// 将登录的session保存在redis中
		jedisClient.set(RedisKeyCode.LOGIN_TOKEN.getValue() + token, session.toString());
		jedisClient.expire(RedisKeyCode.LOGIN_TOKEN.getValue() + token, token_expire); // 设置过期时间(秒)
		
		Map<String, Object> map = new HashMap<>();
		map.put("userId", user.getId());
		map.put("token", token);
		map.put("roles", getUserRoleSorts(String.valueOf(user.getId())));
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "登录成功");
	}
	
	/**
	 * 获得用户角色序号
	 * @param userId 用户id
	 * @return
	 */
	private String getUserRoleSorts(String userId) {
		String sorts = "";
		List<Map<String, Object>> list = sysUserRoleMapper.selectUserRoles(userId);
		for (Map<String, Object> map : list) {
			sorts += map.get("sort") + ",";
		}
		return sorts.length() > 0 ? sorts.substring(0, sorts.length() - 1) : "";
	}

	/**
	 * 用户注销
	 */
	@Override
	public ResponseJson userLogout(String token) {
		log.info("用户注销：token：{}", token);
		if(StringUtil.isBlank(token)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "注销失败，token为空");
		}
		Long result = jedisClient.del(RedisKeyCode.LOGIN_TOKEN.getValue() + token);
		log.info("用户注销，删除redis的key【{}】，删除结果：{}", RedisKeyCode.LOGIN_TOKEN.getValue() + token, result);
		return ResponseUtil.buildJson(ResultCode.SUCC, "注销成功");
	}

	/**
	 * 概况（管理员首页）
	 */
	@Override
	public ResponseJson selectAllAccountNumber() {
		//统计账户个数
		int accountNumber = accountInfoMapper.selectAccountNumber();
		//统计应用个数
		int appNumber = appInfoMapper.selectAppNumber();
		
		//1.待审核应用个数
		int checkApp = appInfoMapper.selectCheckPending();
		//2.待审核模板个数
		int checkTemp = appTemplateMapper.selectCheckPending();
		//统计待办事项个数
		int backlog = checkApp + checkTemp;
		
		Map<String, Object> map = new HashMap<>();
		map.put("accountNumber", accountNumber);
		map.put("appNumber", appNumber);
		map.put("backlog", backlog);
		map.put("checkApp", checkApp);
		map.put("checkTemp", checkTemp);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "查询成功");
	}

	/**
	 * 查询账户列表（管理员首页）
	 */
	@Override
	public ResponseJson selectAccountList() {
		List<AccountInfo> accountList = accountInfoMapper.selectAccountList();
		
		List<Map<String, Object>> mapList = new ArrayList<>();
		if (accountList != null && accountList.size() > 0) {
			List<Map<String, Object>> list = ObjectUtils.objectsToMaps(accountList);
			
			for (Map<String, Object> map : list) {
				map.put("surplusQuantity", (int)map.get("smsTotalQuantity") + (int)map.get("handselTotalQuantity") - (int)map.get("sendTotalQuantity"));
				mapList.add(map);
			}
		}
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, mapList, "查询成功");
	}

}

