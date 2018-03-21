package cn.com.clubank.sms.manage.statistics.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResponseObjectJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.Constants;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.app.service.TemplateService;
import cn.com.clubank.sms.manage.send.dao.SendRecordMapper;
import cn.com.clubank.sms.manage.statistics.SmsStatisticsService;
import cn.com.clubank.sms.manage.sys.dao.AccountInfoMapper;
import cn.com.clubank.sms.manage.sys.dao.SysUserMapper;
import cn.com.clubank.sms.manage.sys.pojo.AccountInfo;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;

@Service
public class SmsStatisticsServiceImpl implements SmsStatisticsService {
	
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private SendRecordMapper sendRecordMapper;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private AccountInfoMapper accountInfoMapper;

	/**
	 * 账户短信发送量统计（一年）
	 */
	@Override
	public ResponseJson sendStatisticsOneYear(Integer userId, String accountId, String year) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_COMMON);
		if(StringUtil.isBlank(year)){
			year = sdf.format(new Date()).substring(0, 4);
		}
		//构建月份数据
		List<String> monthList = new ArrayList<>();
		for (int i = 1; i < 13; i++) {
			monthList.add(String.valueOf(i));
		}
		//构建发送数量初始数据
		List<Integer> sendList = new ArrayList<>();
		for (int i = 1; i < 13; i++) {
			sendList.add(0);
		}
		//构建发送成功数量初始数据
		List<Integer> successList = new ArrayList<>();
		for (int i = 1; i < 13; i++) {
			successList.add(0);
		}
		
		List<Map<String, Object>> recordList = null;
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		//管理员:user.getAccountId()为null,即为查询所有
		recordList = sendRecordMapper.selectAccountOneYearSends(user.getAccountId(), year);

		if(recordList != null && !recordList.isEmpty()){
			for (Map<String, Object> map : recordList) {
				if(StringUtil.isBlank(map.get("sendTotalQuantity").toString()) || StringUtil.isBlank(map.get("successTotalQuantity").toString())){
					map.put("sendTotalQuantity", 0);
					map.put("successTotalQuantity", 0);
				}
				String sendYearMonth = map.get("sendYearMonth").toString();//发送月份
				sendList.set(Integer.parseInt(sendYearMonth.substring(5, 7)) - 1, Integer.parseInt(map.get("sendTotalQuantity").toString()));
				successList.set(Integer.parseInt(sendYearMonth.substring(5, 7)) - 1, Integer.parseInt(map.get("successTotalQuantity").toString()));
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("month", monthList);
		map.put("sendQuantity", sendList);
		map.put("successQuantity", successList);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "数据获取成功");
	}

	/**
	 * 账户短信发送量统计（近半年）
	 */
	@Override
	public ResponseJson sendStatisticsHalfYear(Integer userId, String accountId, String year) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_COMMON);
		if(StringUtil.isBlank(year)){
			year = sdf.format(new Date()).substring(0, 4);
		}else{
			if(!year.equals(sdf.format(new Date()).substring(0, 4))){
		    	return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "不是本年，请选择查询一年");
		    }
		}
	    
	    List<String> monthList = null;//月份
	    String dateEnd = sdf.format(new Date()).substring(0, 7);//结束时间
	    String dateStart = null;//开始时间
	    //确定开始时间
	    if(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) > Constants.INT_5){
	    	int month = Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - Constants.INT_5;
	    	dateStart = sdf.format(new Date()).substring(0, 4) + "-0" + month;
			//构建月份数据
	    	monthList = new ArrayList<>();
	    	for (int i = 5; i > -1; i--) {
	    		monthList.add(String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i));
			}
	    }else{
	    	int startYear = Integer.parseInt(sdf.format(new Date()).substring(0, 4)) - 1;
	    	int month = Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - Constants.INT_5 + 12;
	    	if(String.valueOf(month).length() == 1){
	    		dateStart = startYear + "-0" + month;
	    	}else{
	    		dateStart = startYear + "-" + month;
	    	}
	    	String a = null;
	    	//构建月份数据
	    	monthList = new ArrayList<>();
	    	for (int i = 5; i > -1; i--) {
	    		if(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i > 0){
	    			a = String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i);
	    			if(a.length() == 1){
	    				monthList.add(year + "-0" + a);
	    			}else{
	    				monthList.add(year + "-" + a);
	    			}
	    		}else{
	    			a = String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i + 12);
	    			if(a.length() == 1){
	    				monthList.add(Integer.parseInt(year) - 1 + "-0" + a);
	    			}else{
	    				monthList.add(Integer.parseInt(year) - 1 + "-" + a);
	    			}
	    		}
			}
	    }
	    
		//构建发送数量初始数据
		List<Integer> sendList = new ArrayList<>();
		for (int i = 1; i < 7; i++) {
			sendList.add(0);
		}
		//构建发送成功数量初始数据
		List<Integer> successList = new ArrayList<>();
		for (int i = 1; i < 7; i++) {
			successList.add(0);
		}
		
		List<Map<String, Object>> recordList = null;
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		//管理员:user.getAccountId()为null,即为查询所有
		recordList = sendRecordMapper.selectAccountHalfYearSends(user.getAccountId(), dateStart, dateEnd);
		
		if(recordList != null && !recordList.isEmpty()){
			for (Map<String, Object> map : recordList) {
				if(StringUtil.isBlank(map.get("sendTotalQuantity").toString()) || StringUtil.isBlank(map.get("successTotalQuantity").toString())){
					map.put("sendTotalQuantity", 0);
					map.put("successTotalQuantity", 0);
				}
				String sendYearMonth = map.get("sendYearMonth").toString();//发送月份
				int i = -1;
				for (String string : monthList) {
					i++;
					if(Integer.parseInt(string.substring(5, 7)) == Integer.parseInt(sendYearMonth.substring(5, 7))){
						sendList.set(i, Integer.parseInt(map.get("sendTotalQuantity").toString()));
						successList.set(i, Integer.parseInt(map.get("successTotalQuantity").toString()));
						break;
					}
				}
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("month", monthList);
		map.put("sendQuantity", sendList);
		map.put("successQuantity", successList);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "数据获取成功");
	}

	/**
	 * 账户短信类型发送量统计
	 */
	@Override
	public ResponseJson typeStatistics(Integer userId, String accountId) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		
		List<Map<String, Object>> recordList = null;
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		//管理员:user.getAccountId()为null,即为查询所有
		recordList = sendRecordMapper.selectSmsTypeSends(user.getAccountId());

		int sendQuantityType1 = 0;
		int sendQuantityType2 = 0;
		int sendQuantityType3 = 0;
		int sendQuantityType4 = 0;
		if(recordList != null && !recordList.isEmpty()){
			for (Map<String, Object> map : recordList) {
				if(StringUtil.isBlank(map.get("sendTotalQuantity").toString())){
					map.put("sendTotalQuantity", 0);
				}
				if(Integer.parseInt(map.get("smsType").toString()) == 1){//1-短信验证码
					sendQuantityType1 = Integer.parseInt(map.get("sendTotalQuantity").toString());
				}else if(Integer.parseInt(map.get("smsType").toString()) == 2){//2-通知类短信
					sendQuantityType2 = Integer.parseInt(map.get("sendTotalQuantity").toString());
				}else if(Integer.parseInt(map.get("smsType").toString()) == 3){//3-运营类短信
					sendQuantityType3 = Integer.parseInt(map.get("sendTotalQuantity").toString());
				}else if(Integer.parseInt(map.get("smsType").toString()) == 4){//4-语音验证码
					sendQuantityType4 = Integer.parseInt(map.get("sendTotalQuantity").toString());
				}
			}
		}
		List<Object> list = new ArrayList<>();
		Map<String,Object> map1 = new HashMap<>();
		map1.put("value", sendQuantityType1);
		map1.put("name", "短信验证码");
		list.add(map1);
		Map<String,Object> map2 = new HashMap<>();
		map2.put("value", sendQuantityType2);
		map2.put("name", "通知类短信");
		list.add(map2);
		Map<String,Object> map3 = new HashMap<>();
		map3.put("value", sendQuantityType3);
		map3.put("name", "运营类短信");
		list.add(map3);
		Map<String,Object> map4 = new HashMap<>();
		map4.put("value", sendQuantityType4);
		map4.put("name", "语音验证码");
		list.add(map4);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, list, "数据获取成功");
	}

	/**
	 * 账户各应用发送量统计（一年）
	 */
	@Override
	public ResponseJson appOneYearStatistics(Integer userId, String accountId, String year) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_COMMON);
		if(StringUtil.isBlank(year)){
			year = sdf.format(new Date()).substring(0, 4);
		}
		
		//获得账户下审核通过的应用
		ResponseObjectJson roj = (ResponseObjectJson) templateService.selectAppList(String.valueOf(userId));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> appList = (List<Map<String, Object>>) roj.getData();
	    if(appList == null || appList.isEmpty()){
	    	return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "此账户无应用");
	    }
		List<String> appNameList = new ArrayList<>();
		for (Map<String, Object> map : appList) {
			appNameList.add(map.get("appName").toString());
		}

		//构建月份数据
		List<String> monthList = new ArrayList<>();
		for (int i = 1; i < 13; i++) {
			monthList.add(String.valueOf(i));
		}
		
		//构建初始数据
		Map<String, Object> appMap = null;
		List<Integer> sendList = null;
		List<Map<String, Object>> list = new ArrayList<>();
		for (Map<String, Object> map : appList) {
			sendList = new ArrayList<>();
			for (int i = 0; i < 12; i++) {
				sendList.add(0);
			}
			appMap = new HashMap<>();
			appMap.put(map.get("appName").toString(), sendList);
			list.add(appMap);
		}
		
		List<Map<String, Object>> recordList = null;
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		recordList = sendRecordMapper.selectAppOneYearSends(user.getAccountId(), year);

		if(recordList != null && !recordList.isEmpty()){
			for (Map<String, Object> map2 : recordList) {
				if(map2.containsKey("appName")){
					if(StringUtil.isBlank(map2.get("sendTotalQuantity").toString())){
						map2.put("sendTotalQuantity", 0);
					}
					String sendYearMonth = map2.get("sendYearMonth").toString();//发送月份
					String appName = map2.get("appName").toString();//应用名称
					for (Map<String, Object> map : list) {
						if(map.containsKey(appName)){
							@SuppressWarnings("unchecked")
							List<Integer> newSendList = (List<Integer>) map.get(appName);
							newSendList.set(Integer.parseInt(sendYearMonth.substring(5, 7)) - 1, Integer.parseInt(map2.get("sendTotalQuantity").toString()));								
							break;
						}
					}
				}
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("appName", appNameList);
		map.put("month", monthList);
		map.put("sendQuantity", list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "数据获取成功");
	}

	/**
	 * 账户各应用发送量统计（近半年）
	 */
	@Override
	public ResponseJson appHalfYearStatistics(Integer userId, String accountId, String year) {
		if(StringUtil.isBlank(userId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_COMMON);
		if(StringUtil.isBlank(year)){
			year = sdf.format(new Date()).substring(0, 4);
		}else{
			if(!year.equals(sdf.format(new Date()).substring(0, 4))){
		    	return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "不是本年，请选择查询一年");
		    }
		}
	    
		//获得账户下审核通过的应用
	    ResponseObjectJson roj = (ResponseObjectJson) templateService.selectAppList(String.valueOf(userId));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> appList = (List<Map<String, Object>>) roj.getData();
	    if(appList == null || appList.isEmpty()){
	    	return ResponseUtil.buildObjectJson(ResultCode.DATA_NOT_FOUND, "此账户无应用");
	    }
		List<String> appNameList = new ArrayList<>();
		for (Map<String, Object> map : appList) {
			appNameList.add(map.get("appName").toString());
		}
	    
	    List<String> monthList = null;//月份
	    String dateEnd = sdf.format(new Date()).substring(0, 7);//结束时间
	    String dateStart = null;//开始时间
	    //确定开始时间
	    if(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) > Constants.INT_5){
	    	int month = Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - Constants.INT_5;
	    	dateStart = sdf.format(new Date()).substring(0, 4) + "-0" + month;
			//构建月份数据
	    	monthList = new ArrayList<>();
	    	for (int i = 5; i > -1; i--) {
	    		monthList.add(String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i));
			}
	    }else{
	    	int startYear = Integer.parseInt(sdf.format(new Date()).substring(0, 4)) - 1;
	    	int month = Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - Constants.INT_5 + 12;
	    	if(String.valueOf(month).length() == 1){
	    		dateStart = startYear + "-0" + month;
	    	}else{
	    		dateStart = startYear + "-" + month;
	    	}
	    	String a = null;
	    	//构建月份数据
	    	monthList = new ArrayList<>();
	    	for (int i = 5; i > -1; i--) {
	    		if(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i > 0){
	    			a = String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i);
	    			if(a.length() == 1){
	    				monthList.add(year + "-0" + a);
	    			}else{
	    				monthList.add(year + "-" + a);
	    			}
	    		}else{
	    			a = String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i + 12);
	    			if(a.length() == 1){
	    				monthList.add(Integer.parseInt(year) - 1 + "-0" + a);
	    			}else{
	    				monthList.add(Integer.parseInt(year) - 1 + "-" + a);
	    			}
	    		}
			}
	    }
	    
	    //构建初始数据
		Map<String, Object> appMap = null;
		List<Integer> sendList = null;
		List<Map<String, Object>> list = new ArrayList<>();
		for (Map<String, Object> map : appList) {
			sendList = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				sendList.add(0);
			}
			appMap = new HashMap<>();
			appMap.put(map.get("appName").toString(), sendList);
			list.add(appMap);
		}
	    
		List<Map<String, Object>> recordList = null;	
		SysUser user = sysUserMapper.selectByPrimaryKey(userId);
		recordList = sendRecordMapper.selectAppHalfYearSends(user.getAccountId(), dateStart, dateEnd);
		
		if(recordList != null && !recordList.isEmpty()){
			for (Map<String, Object> map2 : recordList) {
				if(map2.containsKey("appName")){
					if(StringUtil.isBlank(map2.get("sendTotalQuantity").toString())){
						map2.put("sendTotalQuantity", 0);
					}
					String sendYearMonth = map2.get("sendYearMonth").toString();//发送月份
					String appName = map2.get("appName").toString();//应用名称
					for (Map<String, Object> map : list) {
						if(map.containsKey(appName)){
							int i = -1;
							for (String string : monthList) {
								i++;
								if(Integer.parseInt(string.substring(5, 7)) == Integer.parseInt(sendYearMonth.substring(5, 7))){
									@SuppressWarnings("unchecked")
									List<Integer> newSendList = (List<Integer>) map.get(appName);
									newSendList.set(i, Integer.parseInt(map2.get("sendTotalQuantity").toString()));
									break;
								}
							}
							break;
						}
					}
				}
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("appName", appNameList);
		map.put("month", monthList);
		map.put("sendQuantity", list);
	    return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "数据获取成功");
	}
	
	/**
	 * 获得账户（统计用下拉框）
	 */
	@Override
	public ResponseJson selectAllAccount(Integer userId, String roles) {
		if(StringUtil.isBlank(roles)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		Map<String,Object> map = null;
		List<Map<String,Object>> list = new ArrayList<>();
		//判断用户角色
		if(roles.indexOf(Constants.Sort_99) != -1){
			List<AccountInfo> accountList = accountInfoMapper.selectAllAcount();
			for (AccountInfo accountInfo : accountList) {
				map = new HashMap<>();
				map.put("id", accountInfo.getId());
				map.put("accountName", accountInfo.getAccountName());
				list.add(map);
			}
		}else{
			SysUser user = sysUserMapper.selectByPrimaryKey(userId);
			AccountInfo account = accountInfoMapper.selectByPrimaryKey(Integer.parseInt(user.getAccountId()));
			map = new HashMap<>();
			map.put("id", account.getId());
			map.put("accountName", account.getAccountName());
			list.add(map);
		}
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, list, "查询成功");
	}

	/**
	 * 各账户发送量统计（一年）（管理员）
	 */
	@Override
	public ResponseJson accountOneYearStatistics(String year) {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_COMMON);
		if(StringUtil.isBlank(year)){
			year = sdf.format(new Date()).substring(0, 4);
		}
		
		//获得所有账户
		List<AccountInfo> accountList = accountInfoMapper.selectAllAcount();
		if(accountList == null || accountList.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "当前系统无账户");
		}
		
		//构建账户名数据
		List<String> acNameList = new ArrayList<>();
		for (AccountInfo accountInfo : accountList) {
			acNameList.add(accountInfo.getAccountName());
		}
		
		//构建月份数据
		List<String> monthList = new ArrayList<>();
		for (int i = 1; i < 13; i++) {
			monthList.add(String.valueOf(i));
		}
		
		//构建初始数据
		Map<String, Object> accountMap = null;
		List<Integer> sendList = null;
		List<Map<String, Object>> list = new ArrayList<>();
		for (String string : acNameList) {
			sendList = new ArrayList<>();
			for (int i = 0; i < 12; i++) {
				sendList.add(0);
			}
			accountMap = new HashMap<>();
			accountMap.put(string, sendList);
			list.add(accountMap);
		}
		
		List<Map<String,Object>> recordList = null;
		recordList = sendRecordMapper.selectAllRecordByYear(year);
		
		if(recordList != null && !recordList.isEmpty()){
			for (Map<String, Object> map2 : recordList) {
				if(map2.containsKey("accountName")){
					if(StringUtil.isBlank(map2.get("sendTotalQuantity").toString())){
						map2.put("sendTotalQuantity", 0);
					}
					String sendYearMonth = map2.get("sendYearMonth").toString();//发送月份
					String accountName = map2.get("accountName").toString();//账户名称
					for (Map<String, Object> map : list) {
						if(map.containsKey(accountName)){
							@SuppressWarnings("unchecked")
							List<Integer> newSendList = (List<Integer>) map.get(accountName);
							newSendList.set(Integer.parseInt(sendYearMonth.substring(5, 7)) - 1, Integer.parseInt(map2.get("sendTotalQuantity").toString()));								
							break;
						}
					}
				}
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("accountName", acNameList);
		map.put("month", monthList);
		map.put("sendQuantity", list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "查询成功");
	}

	/**
	 * 各账户发送量统计（近半年）（管理员）
	 */
	@Override
	public ResponseJson accountHalfYearStatistics(String year) {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_COMMON);
		if(StringUtil.isBlank(year)){
			year = sdf.format(new Date()).substring(0, 4);
		}else{
			if(!year.equals(sdf.format(new Date()).substring(0, 4))){
		    	return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "不是本年，请选择查询一年");
		    }
		}
		
		//获得所有账户
		List<AccountInfo> accountList = accountInfoMapper.selectAllAcount();
		if(accountList == null || accountList.isEmpty()){
			return ResponseUtil.buildJson(ResultCode.DATA_NOT_FOUND, "当前系统无账户");
		}
		
		//构建账户名数据
		List<String> acNameList = new ArrayList<>();
		for (AccountInfo accountInfo : accountList) {
			acNameList.add(accountInfo.getAccountName());
		}
		
		List<String> monthList = null;//月份
	    String dateEnd = sdf.format(new Date()).substring(0, 7);//结束时间
	    String dateStart = null;//开始时间
	    //确定开始时间
	    if(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) > Constants.INT_5){
	    	int month = Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - Constants.INT_5;
	    	dateStart = sdf.format(new Date()).substring(0, 4) + "-0" + month;
			//构建月份数据
	    	monthList = new ArrayList<>();
	    	for (int i = 5; i > -1; i--) {
	    		monthList.add(String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i));
			}
	    }else{
	    	int startYear = Integer.parseInt(sdf.format(new Date()).substring(0, 4)) - 1;
	    	int month = Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - Constants.INT_5 + 12;
	    	if(String.valueOf(month).length() == 1){
	    		dateStart = startYear + "-0" + month;
	    	}else{
	    		dateStart = startYear + "-" + month;
	    	}
	    	String a = null;
	    	//构建月份数据
	    	monthList = new ArrayList<>();
	    	for (int i = 5; i > -1; i--) {
	    		if(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i > 0){
	    			a = String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i);
	    			if(a.length() == 1){
	    				monthList.add(year + "-0" + a);
	    			}else{
	    				monthList.add(year + "-" + a);
	    			}
	    		}else{
	    			a = String.valueOf(Integer.parseInt(sdf.format(new Date()).substring(5, 7)) - i + 12);
	    			if(a.length() == 1){
	    				monthList.add(Integer.parseInt(year) - 1 + "-0" + a);
	    			}else{
	    				monthList.add(Integer.parseInt(year) - 1 + "-" + a);
	    			}
	    		}
			}
	    }
	    
		//构建初始数据
		Map<String, Object> accountMap = null;
		List<Integer> sendList = null;
		List<Map<String, Object>> list = new ArrayList<>();
		for (String string : acNameList) {
			sendList = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				sendList.add(0);
			}
			accountMap = new HashMap<>();
			accountMap.put(string, sendList);
			list.add(accountMap);
		}
		
		List<Map<String,Object>> recordList = null;
		recordList = sendRecordMapper.selectAllSendRecord(dateStart, dateEnd);
		
		if(recordList != null && !recordList.isEmpty()){
			for (Map<String, Object> map2 : recordList) {
				if(map2.containsKey("accountName")){
					if(StringUtil.isBlank(map2.get("sendTotalQuantity").toString())){
						map2.put("sendTotalQuantity", 0);
					}
					String sendYearMonth = map2.get("sendYearMonth").toString();//发送月份
					String accountName = map2.get("accountName").toString();//账户名称
					for (Map<String, Object> map : list) {
						if(map.containsKey(accountName)){
							int i = -1;
							for (String string : monthList) {
								i++;
								if(Integer.parseInt(string.substring(5, 7)) == Integer.parseInt(sendYearMonth.substring(5, 7))){
									@SuppressWarnings("unchecked")
									List<Integer> newSendList = (List<Integer>) map.get(accountName);
									newSendList.set(i, Integer.parseInt(map2.get("sendTotalQuantity").toString()));
									break;
								}
							}
							break;
						}
					}
				}
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("accountName", acNameList);
		map.put("month", monthList);
		map.put("sendQuantity", list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, map, "查询成功");
	}

}
