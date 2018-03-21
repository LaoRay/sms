package cn.com.clubank.sms.manage.sys.service;

/**
 * 编号规则项目
 *
 */
public interface BaseCodeRuleService {
	/** 
	 * 获取单号
	 * @param itemCode
	 * @return
	 */
	String getCode(String itemCode);
}
