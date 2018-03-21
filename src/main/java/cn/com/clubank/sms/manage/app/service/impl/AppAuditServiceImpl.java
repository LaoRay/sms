package cn.com.clubank.sms.manage.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.app.dao.AppInfoMapper;
import cn.com.clubank.sms.manage.app.pojo.AppInfo;
import cn.com.clubank.sms.manage.app.service.AppAuditService;
import cn.com.clubank.sms.manage.mail.SendMailService;
import cn.com.clubank.sms.manage.sys.dao.SysUserMapper;
import cn.com.clubank.sms.manage.sys.pojo.SysUser;

@Service
public class AppAuditServiceImpl implements AppAuditService {

	@Autowired
	private AppInfoMapper appInfoMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired 
	private SendMailService sendMailService;
	
	/**
	 * 审核应用
	 */
	@Override
	public ResponseJson auditApp(Integer appId, Integer key, String thirdId, String dismissal) {
		if(StringUtil.isBlank(appId) || StringUtil.isBlank(key)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		String msg = null;
		if(key == 1){
			if(StringUtil.isBlank(thirdId)){
				return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "第三方id不能为空");
			}
			msg = "】已经通过审核，感谢您的使用！";
		}else{
			msg = "】没有通过审核，请您登录远古短信公众平台查看具体原因。";
		}
		appInfoMapper.updateAuditApp(appId, key, thirdId, dismissal);
		
		// 应用审核发邮件
		AppInfo app = appInfoMapper.selectByPrimaryKey(appId);
		List<SysUser> list = sysUserMapper.selectUserListByAccountId(app.getAccountId());
		SysUser user = list.get(0);
		sendMailService.sendHtmlMail("西安远古短信平台模板审核通知", "尊敬的远古客户您好！<br>您的应用【" + app.getAppName() + msg, user.getEmail());
		
		return ResponseUtil.buildJson(ResultCode.SUCC, "操作成功");
	}

}
