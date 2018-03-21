package cn.com.clubank.sms.manage.statistics;

import cn.com.clubank.sms.common.response.ResponseJson;

/**
 * 发短信统计
 * @author Liangwl
 *
 */
public interface SmsStatisticsService {

	/**
	 * 账户短信发送量统计（一年）
	 * @param year 年（yyyy）
	 * @param userId 用户id
	 * @param accountId 账户id
	 * @return
	 */
	ResponseJson sendStatisticsOneYear(Integer userId, String accountId, String year);
	
	/**
	 * 账户短信发送量统计（近半年）
	 * @param year 年（yyyy）
	 * @param userId 用户id
	 * @param accountId 账户id
	 * @return
	 */
	ResponseJson sendStatisticsHalfYear(Integer userId, String accountId, String year);
	
	/**
	 * 账户短信类型统计
	 * @param yearMonth 年月（yyyy-MM）
	 * @param userId 用户id
	 * @param accountId 账户id
	 * @return
	 */
	ResponseJson typeStatistics(Integer userId, String accountId);
	
	/**
	 * 账户各应用发送量统计（一年）
	 * @param userId 用户id
	 * @param accountId 账户id
	 * @param year 年（yyyy）
	 * @return
	 */
	ResponseJson appOneYearStatistics(Integer userId, String accountId, String year);
	
	/**
	 * 账户各应用发送量统计（近半年）
	 * @param userId 用户id
	 * @param accountId 账户id
	 * @param year 年（yyyy）
	 * @return
	 */
	ResponseJson appHalfYearStatistics(Integer userId, String accountId, String year);
		
	/**
	 * 获得账户（统计用下拉框）
	 * @param userId 用户id
	 * @return
	 */
	ResponseJson selectAllAccount(Integer userId, String roles);
	
	/**
	 * 各账户发送量统计（一年）（管理员）
	 * @param year 年（yyyy）
	 * @return
	 */
	ResponseJson accountOneYearStatistics(String year);
	
	/**
	 * 各账户发送量统计（近半年）（管理员）
	 * @param year 年（yyyy）
	 * @return
	 */
	ResponseJson accountHalfYearStatistics(String year);
}
