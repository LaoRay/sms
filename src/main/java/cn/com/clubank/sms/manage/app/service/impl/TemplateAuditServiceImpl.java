package cn.com.clubank.sms.manage.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.app.dao.AppInfoMapper;
import cn.com.clubank.sms.manage.app.dao.AppTemplateMapper;
import cn.com.clubank.sms.manage.app.pojo.AppInfo;
import cn.com.clubank.sms.manage.app.pojo.AppTemplate;
import cn.com.clubank.sms.manage.app.service.TemplateAuditService;
import cn.com.clubank.sms.manage.mail.SendMailService;
import cn.com.clubank.sms.manage.sys.dao.SysUserMapper;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;

@Service
public class TemplateAuditServiceImpl implements TemplateAuditService {

	@Autowired
	private AppTemplateMapper appTemplateMapper;
	@Autowired
	private AppInfoMapper appInfoMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private SendMailService sendMailService;
	
	/**
	 * 审核模板
	 */
	@Override
	public ResponseJson auditTemplate(Integer tempId, Integer key, String templateId, String dismissal) {
		if(StringUtil.isBlank(tempId) || StringUtil.isBlank(key)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		String msg = null;
		if(key == 1){
			if(StringUtil.isBlank(templateId)){
				return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "第三方id不能为空");
			}
			msg = "】已经通过审核，感谢您的使用！";
		}else{
			msg = "】没有通过审核，请您登录远古短信公众平台查看具体原因。";
		}
		appTemplateMapper.updateAuditTemplate(tempId, key, templateId, dismissal);
		
		// 模板审核发邮件
		AppTemplate template = appTemplateMapper.selectByPrimaryKey(tempId);
		AppInfo app = appInfoMapper.selectByPrimaryKey(Integer.parseInt(template.getAppId()));
		List<SysUser> list = sysUserMapper.selectUserListByAccountId(app.getAccountId());
		SysUser user = list.get(0);
		sendMailService.sendHtmlMail("西安远古短信平台模板审核通知", "尊敬的远古客户您好！<br>您的应用【" + app.getAppName() + "】下的短信模板【" + template.getTempCode() + msg, user.getEmail());

		return ResponseUtil.buildJson(ResultCode.SUCC, "操作成功");
	}

}
