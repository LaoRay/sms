package cn.com.clubank.sms.manage.send.service;

import cn.com.clubank.sms.common.response.ResponseJson;

/**
 * 发送管理：发送记录
 * @author Liangwl
 *
 */
public interface SendRecordService {

	/**
	 * 查询发送记录：分页查询
	 * @param appName 应用名称
	 * @param status  200-成功，201-失败
	 * @param tempCode 模板编号
	 * @param dateOne 时间1
	 * @param dateTwo 时间2
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 * @return
	 */
	ResponseJson selectSendRecordAndPage(String accountId, String appName, Integer status, String tempCode, String dateOne, String dateTwo, Integer pageIndex, Integer pageSize);
	
	/**
	 * 查询发送成功记录详情列表
	 * @param recordId 发送记录id
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 * @return
	 */
	ResponseJson selectSendSuccessRecordDetail(String recordId, Integer pageIndex, Integer pageSize);
	
	/**
	 * 查询发送失败记录详情列表
	 * @param recordId 发送记录id
	 * @param pageIndex 页码下标
	 * @param pageSize 页容量
	 * @return
	 */
	ResponseJson selectSendFailRecordDetail(String recordId, Integer pageIndex, Integer pageSize);
}
