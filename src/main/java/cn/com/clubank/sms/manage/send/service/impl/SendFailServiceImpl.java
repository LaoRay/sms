package cn.com.clubank.sms.manage.send.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.PageObject;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.manage.send.dao.SendRecordMapper;
import cn.com.clubank.sms.manage.send.service.SendFailService;

@Service
public class SendFailServiceImpl implements SendFailService {

	@Autowired
	private SendRecordMapper sendRecordMapper;
	
	/**
	 * 查询发送失败列表：分页查询
	 */
	@Override
	public ResponseJson selectSendFailListAndPage(String accountId, String appName, String tempCode, String mobile, String dateOne, String dateTwo,
			Integer pageIndex, Integer pageSize) {
		PageObject<Map<String, Object>> page = new PageObject<>(pageIndex, pageSize);
		int total = sendRecordMapper.selectFailRecordTotal(accountId, appName, tempCode, mobile, dateOne, dateTwo);
		List<Map<String, Object>> list = sendRecordMapper.selectSendFailRecordList(accountId, appName, tempCode, mobile, dateOne, dateTwo, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

}
