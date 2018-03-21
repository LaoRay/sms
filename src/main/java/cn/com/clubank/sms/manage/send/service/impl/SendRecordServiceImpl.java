package cn.com.clubank.sms.manage.send.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResultCode;
import cn.com.clubank.sms.common.utils.PageObject;
import cn.com.clubank.sms.common.utils.ResponseUtil;
import cn.com.clubank.sms.common.utils.StringUtil;
import cn.com.clubank.sms.manage.send.dao.SendRecordMapper;
import cn.com.clubank.sms.manage.send.pojo.SendRecord;
import cn.com.clubank.sms.manage.send.service.SendRecordService;

@Service
public class SendRecordServiceImpl implements SendRecordService {

	@Autowired
	private SendRecordMapper sendRecordMapper;
	
	/**
	 * 查询发送记录：分页查询
	 */
	@Override
	public ResponseJson selectSendRecordAndPage(String accountId, String appName, Integer status, String tempCode, String dateOne, String dateTwo,
			Integer pageIndex, Integer pageSize) {
		if (status == null) {
			status = 0;
		}
		PageObject<SendRecord> page = new PageObject<>(pageIndex, pageSize);
		int total = sendRecordMapper.selectTotal(accountId, appName, status, tempCode, dateOne, dateTwo);
		List<SendRecord> list = sendRecordMapper.selectSendRecordList(accountId, appName, status, tempCode, dateOne, dateTwo, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

	/**
	 * 查询发送成功记录详情列表
	 */
	@Override
	public ResponseJson selectSendSuccessRecordDetail(String recordId, Integer pageIndex, Integer pageSize) {
		if(StringUtil.isBlank(recordId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		PageObject<Map<String, Object>> page = new PageObject<>(pageIndex, pageSize);
		int total = sendRecordMapper.selectSuccessTotal(recordId);
		List<Map<String, Object>> list = sendRecordMapper.selectSendSuccessList(recordId, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

	/**
	 * 查询发送失败记录详情列表
	 */
	@Override
	public ResponseJson selectSendFailRecordDetail(String recordId, Integer pageIndex, Integer pageSize) {
		if(StringUtil.isBlank(recordId)){
			return ResponseUtil.buildJson(ResultCode.PARAM_ERROR, "参数不能为空");
		}
		PageObject<Map<String, Object>> page = new PageObject<>(pageIndex, pageSize);
		int total = sendRecordMapper.selectFailTotal(recordId);
		List<Map<String, Object>> list = sendRecordMapper.selectSendFailList(recordId, page.getStart(), pageSize);
		page.setRows(total);
		page.setDataList(list);
		return ResponseUtil.buildObjectJson(ResultCode.SUCC, page, "查询成功");
	}

}
