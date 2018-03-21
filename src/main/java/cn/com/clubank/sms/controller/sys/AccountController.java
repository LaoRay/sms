package cn.com.clubank.sms.controller.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;
import cn.com.clubank.sms.manage.sys.service.AccountService;

/**
 * 系统设置：账户管理
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("/user")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	/**
	 * 按条件获得用户列表并分页
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/list",method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainUserListAndPage(@RequestBody JSONObject param){
		
		return accountService.selectUserListAndPage(param.getString("loginName"), param.getString("realname"), param.getString("mobile"), param.getInteger("enabledMark"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
	}
	
	/**
	 * 获得用户详情
	 */
	@RequestMapping(value = "/detail" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainUserDetail(@RequestBody JSONObject param){
		
		return accountService.selectUserDetail(param.getInteger("userId"));
	}
	
	/**
	 * 获得用户信息（编辑用）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/info" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainUserInfo(@RequestBody JSONObject param){
		
		return accountService.selectUserInfo(param.getInteger("userId"));
	}
	
	/**
	 * 新增、编辑用户
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/save" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson addOrEditUser(@RequestBody SysUser user){
		
		return accountService.insertOrUpdateUser(user);
	}
	
	/**
	 * 给用户分配角色时获得角色列表
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/roles" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainRoles(@RequestBody JSONObject param){
		
		return accountService.selectRoles(param.getInteger("userId"));
	}
	
	/**
	 * 给用户分配角色
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/assigningRoles" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson assigningRoles(@RequestBody JSONObject param){
		
		return accountService.assigningRoles(param.getString("userId"), param.getJSONArray("roleIds"));
	}
	
	/**
	 * 删除用户
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson deleteUser(@RequestBody JSONObject param){
		
		return accountService.deleteUser(param.getJSONArray("userIds"));
	}
	
	/**
	 * 设置用户状态（1-有效，0-无效）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/setstate" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson setstate(@RequestBody JSONObject param){
		
		return accountService.setstate(param.getInteger("userId"), param.getInteger("enabledMark"));
	}

	/**
	 * 充值前账户余额查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/balance" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainAccountBalance(@RequestBody JSONObject param){
		
		return accountService.selectAccountBalance(param.getInteger("userId"));
	}
	
	/**
	 * 充值
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/pay" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson recharge(@RequestBody JSONObject param){
		
		return accountService.recharge(param.getInteger("userId"), param.getBigDecimal("rechargeAmount"), param.getBigDecimal("singlePrice"));
	}
	
	/**
	 * 获得账户充值记录并分页
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/recharge",method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainRechargeListAndPage(@RequestBody JSONObject param){
		
		return accountService.selectRechargeListAndPage(param.getString("accountName"), param.getString("dateOne"), param.getString("dateTwo"), param.getInteger("pageIndex"), param.getInteger("pageSize"));
	}
}
