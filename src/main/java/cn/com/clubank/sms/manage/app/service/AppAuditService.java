package cn.com.clubank.sms.manage.app.service;

import cn.com.clubank.sms.common.response.ResponseJson;

/**
 * 应用审核
 * @author Liangwl
 *
 */
public interface AppAuditService {

	/**
	 * 审核应用
	 * @param appId 应用id
	 * @param key 1-审核通过、2-审核不通过
	 * @param thirdId 第三方id
	 * @param dismissal 驳回原因
	 * @return
	 */
	ResponseJson auditApp(Integer appId, Integer key, String thirdId, String dismissal);
}
