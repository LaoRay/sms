package cn.com.clubank.sms.manage.send.service;

import cn.com.clubank.sms.common.response.ResponseJson;

/**
 * 发送管理：发送失败
 * @author Liangwl
 *
 */
public interface SendFailService {

	/**
	 * 查询发送失败列表：分页查询
	 * @param accountId 账户id
	 * @param appName 应用名称
	 * @param tempCode 模板编号
	 * @param mobile 手机号
	 * @param dateOne 时间1
	 * @param dateTwo 时间2
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 */
	ResponseJson selectSendFailListAndPage(String accountId, String appName, String tempCode, String mobile, String dateOne, String dateTwo, Integer pageIndex, Integer pageSize);
}
