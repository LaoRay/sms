package cn.com.clubank.sms.manage.app.service;

import cn.com.clubank.sms.common.response.ResponseJson;

/**
 * 模板审核
 * @author Liangwl
 *
 */
public interface TemplateAuditService {

	/**
	 * 审核模板
	 * @param tempId 模板id
	 * @param key 1-审核通过、2-审核不通过
	 * @param templateId 第三方模板id
	 * @param dismissal 驳回原因
	 * @return
	 */
	ResponseJson auditTemplate(Integer tempId, Integer key, String templateId, String dismissal);
}
