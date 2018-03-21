package cn.com.clubank.sms.manage.handle.impl;

import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.com.clubank.sms.common.redis.JedisClient;
import cn.com.clubank.sms.common.redis.RedisKeyCode;
import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResponseObjectJson;
import cn.com.clubank.sms.common.response.ResponseTokenJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.Constants;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.app.dao.AppInfoMapper;
import cn.com.clubank.sms.manage.app.dao.AppTemplateMapper;
import cn.com.clubank.sms.manage.app.pojo.AppInfo;
import cn.com.clubank.sms.manage.app.pojo.AppTemplate;
import cn.com.clubank.sms.manage.handle.SmsHandleService;
import cn.com.clubank.sms.manage.message.MessageEntity;
import cn.com.clubank.sms.manage.message.MessageQueueUtil;
import cn.com.clubank.sms.manage.send.dao.SendSuccessRecordMapper;
import cn.com.clubank.sms.manage.sys.dao.AccountInfoMapper;
import cn.com.clubank.sms.manage.sys.dao.AccountRechargeMapper;
import cn.com.clubank.sms.manage.sys.pojo.AccountInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 第三方发送短信处理
 * 
 * @author YangWei
 *
 */
@Slf4j
@Service
public class SmsHandleServiceImpl implements SmsHandleService {

	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private MessageQueueUtil messageQueueUtil;
	
	@Autowired
	private AppInfoMapper appInfoMapper;
	
	@Autowired
	private AccountInfoMapper accountInfoMapper;
	
	@Autowired
	private AppTemplateMapper appTemplateMapper;
	
	@Autowired
	private AccountRechargeMapper accountRechargeMapper;
	
	@Autowired
	private SendSuccessRecordMapper sendSuccessRecordMapper;
	
	@Value("${token.expire}")
	private Integer tokenExpire; // token过期时间
	
	@Value("${clubank.dayCodeSmsMostSend}")
	private Integer dayCodeSmsMostSend; // 单个手机号每天验证码短信的最大发送量
	
	@Override
	public ResponseJson sendcheck(HttpServletRequest req) {
		String appKey = req.getHeader("appKey");
		
		String authAccount = req.getParameter("authAccount");
		String authPwd = req.getParameter("authPwd");
		log.info("【获取授权码】-appKey:{}, authAccount:{}", appKey, authAccount);
		if (StringUtil.isBlank(appKey) || StringUtil.isBlank(authAccount) || StringUtil.isBlank(authPwd)) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		
		//根据appKey获取到app及其账户信息，验证账户的鉴权帐号及密码
		AppInfo app = appInfoMapper.selectByKey(appKey);
		if (app == null) {
			return new ResponseJson(ResultCode.DATA_NOT_FOUND, "没有找到应用的信息");
		}
		if (app.getStatus() != Constants.INT_1) {
			return new ResponseJson(ResultCode.FAIL, "应用未审核或未通过");
		}
		AccountInfo account = accountInfoMapper.selectByPrimaryKey(Integer.valueOf(app.getAccountId()));
		if (account == null) {
			return new ResponseJson(ResultCode.DATA_NOT_FOUND, "没有找到帐户的信息");
		}
		if (!authAccount.equals(account.getAuthAccount()) || !authPwd.equals(account.getAuthPwd())) {
			return new ResponseJson(ResultCode.ACCOUNT_ERROR, "鉴权账号或鉴权密码错误");
		}
		
		// 验证短信数量
		ResponseJson verResult = verSmsAmount(account.getId()+"", Constants.INT_0);
		if (verResult.getCode() != ResultCode.SUCC.getValue()) {
			return verResult;
		}
		
		//生成token，保存至redis
		String token = StringUtil.get32UUID();
		jedisClient.set(RedisKeyCode.ACCESS_TOKEN.getValue() + token, appKey);
		jedisClient.expire(RedisKeyCode.ACCESS_TOKEN.getValue() + token, tokenExpire); // 设置过期时间(秒)
		log.info("【获取授权码】获取成功-token:{}", token);
		return new ResponseTokenJson(ResultCode.SUCC, token, "鉴权成功");
	}

	@Override
	public ResponseJson sendcode(HttpServletRequest req) {
		String tempCode = req.getParameter("tempCode");
		String mobile = req.getParameter("mobile");
		String authCode = req.getParameter("authCode");
		if (StringUtil.isBlank(tempCode) || StringUtil.isBlank(mobile) || StringUtil.isBlank(authCode)) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		// 验证手机号码格式
		if (!verMobile(mobile)) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "手机号码格式错误");
		}
		// 验证验证码格式：必须是数字，长度4-10
		Pattern codePattern = Pattern.compile("^-?[0-9]+");
		if (!codePattern.matcher(authCode).matches() || authCode.length() < 4 || authCode.length() > 10) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "验证码格式错误");
		}
		
		// 验证appKey和授权码的合法性
		ResponseJson verResult = verToken(req.getHeader("appKey"), req.getHeader("token"));
		if (verResult.getCode() != ResultCode.SUCC.getValue()) {
			return verResult;
		}
		AppTemplate temp = appTemplateMapper.selectByCode(tempCode);
		if (temp == null) {
			return new ResponseJson(ResultCode.DATA_NOT_FOUND, "模版信息不存在");
		}
		if (temp.getSmsType() != AppTemplate.type_code && temp.getSmsType() != AppTemplate.type_voice) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "模版类型错误");
		}
		
		// 验证当天验证码发送量
		int sendCodeNums = sendSuccessRecordMapper.getThatDayCodeCount(mobile);
		if (sendCodeNums >= dayCodeSmsMostSend) {
			return new ResponseJson(ResultCode.AMOUNT_LIMIT, "单个手机号每天最多发送"+dayCodeSmsMostSend+"条验证码短信");
		}
		
		// 验证剩余短信数量
		synchronized (this) {
			verResult = verSmsAmount(temp.getAccountId(), Constants.INT_1);
			if (verResult.getCode() != ResultCode.SUCC.getValue()) {
				return verResult;
			}
			
			// 插入消息队列
			messageQueueUtil.insertFirst(new MessageEntity(MessageEntity.type_code, temp.getTemplateId(), temp.getAccountId(), tempCode, mobile, authCode));
		}
		
		return new ResponseObjectJson(ResultCode.SUCC, authCode, "操作成功");
	}
	
	@Override
	public ResponseJson sendtemplate(HttpServletRequest req) {
		String tempCode = req.getParameter("tempCode");
		String mobiles = req.getParameter("mobiles");
		String params = req.getParameter("params");
		if (StringUtil.isBlank(tempCode) || StringUtil.isBlank(mobiles)) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		
		JSONArray mobileArray = null;
		JSONArray paramArray = null;
		try {
			mobileArray = JSON.parseArray(mobiles);
			paramArray = JSON.parseArray(params);
			
			// 手机号码数量限制
			if (mobileArray.size() > 10000) {
				return new ResponseJson(ResultCode.PARAM_LENGTH, "手机号码最多10000个");
			}
		} catch (Exception e) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "参数格式错误");
		}
		
		// 验证appKey和授权码的合法性
		ResponseJson verResult = verToken(req.getHeader("appKey"), req.getHeader("token"));
		if (verResult.getCode() != ResultCode.SUCC.getValue()) {
			return verResult;
		}
		AppTemplate temp = appTemplateMapper.selectByCode(tempCode);
		if (temp == null) {
			return new ResponseJson(ResultCode.DATA_NOT_FOUND, "模版信息不存在");
		}
		if (temp.getSmsType() != AppTemplate.type_notice && temp.getSmsType() != AppTemplate.type_operate) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "模版类型错误");
		}
		
		// 验证传入参数个数和模版中参数个数的匹配
		if (getContentParamNum(temp.getTempContent()) != paramArray.size()) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "参数个数和模版中需要的个数不一致");
		}
		
		// 验证传入的单个参数长度不能超过30
		for (int i = 0;i < paramArray.size(); i++) {
			if (paramArray.getString(i).length() > 30) {
				return new ResponseJson(ResultCode.PARAM_LENGTH, "模版参数超过长度限制");
			}
		}
		// 手机号码格式验证
		for (int i = 0;i < mobileArray.size(); i++) {
			if (!verMobile(mobileArray.getString(i))) {
				return new ResponseJson(ResultCode.PARAM_ERROR, "手机号码'"+mobileArray.getString(i)+"'格式错误");
			}
		}
		
		// 验证短信数量
		synchronized (this) {
			verResult = verSmsAmount(temp.getAccountId(), mobileArray.size());
			if (verResult.getCode() != ResultCode.SUCC.getValue()) {
				return verResult;
			}
			
			// 解析参数，将手机号码解析成长度为100的数组
			int size = 100; // 按照网易云规定：每次发送短信最多100个手机号码
			// 计算出一共解析成几个数组
			int count = mobileArray.size() % size == 0 ? mobileArray.size() / size : mobileArray.size() / size + 1;
			for (int i = 0; i < count; i++) {
				int toIndex = i == count - 1 ? mobileArray.size() : (i + 1) * size;
				List<Object> list = mobileArray.subList(i * size, toIndex);
				
				// 插入消息队列
				messageQueueUtil.insertFirst(new MessageEntity(MessageEntity.type_temp, temp.getTemplateId(), temp.getAccountId(), tempCode, JSON.toJSONString(list), params));
			}
		}
		
		return ResponseUtil.buildJson(ResultCode.SUCC, "操作成功");
	}

	/**
	 * 验证appKey和授权码的合法性
	 * @param appKey
	 * @param token
	 * @return
	 */
	private ResponseJson verToken(String appKey, String token) {
		// 验证appKey和授权码的合法性
		String key = jedisClient.get(RedisKeyCode.ACCESS_TOKEN.getValue() + token);
		if (StringUtil.isBlank(key)) {
			return new ResponseJson(ResultCode.TOKEN_INVALID, "token已失效，请重新获取");
		} else if (!key.equals(appKey)) {
			return new ResponseJson(ResultCode.PARAM_ERROR, "参数错误");
		}
		return new ResponseJson(ResultCode.SUCC, "验证通过");
	}
	
	/**
	 * 验证短信数量
	 * @param amount
	 * @return
	 */
	private ResponseJson verSmsAmount(String accountId, Integer amount) {
		// 剩余短信总数：数据库中剩余条数  - 消息队列中待发送的短信条数
		int last = accountRechargeMapper.totalLastQuantity(accountId) - messageQueueUtil.getWaitSendSmsAmount(accountId);
		log.info("【验证帐户短信数量】帐户{}的短信剩余量：{}", accountId, last);
		if (last < amount) {
			// 短信数量不够
			return new ResponseJson(ResultCode.FAIL, "短信余额不够，请充值");
		}
		return new ResponseJson(ResultCode.SUCC, "验证通过");
	}
	
	/**
	 * 验证手机号码格式
	 * @return
	 */
	private boolean verMobile(String mobile) {
		Pattern mobilePattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
		return mobilePattern.matcher(mobile).matches();
	}
	
	/**
	 * 获取模版中参数的个数
	 * @param content
	 * @return
	 */
	private int getContentParamNum(String content) {
		if (content.endsWith("%s")) {
			return content.split("%s").length;
		} else {
			return content.split("%s").length - 1;
		}
	}
	
}
