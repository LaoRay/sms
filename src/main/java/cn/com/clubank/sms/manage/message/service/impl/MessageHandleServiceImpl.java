package cn.com.clubank.sms.manage.message.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.manage.app.dao.AppTemplateMapper;
import cn.com.clubank.sms.manage.app.pojo.AppTemplate;
import cn.com.clubank.sms.manage.message.MessageEntity;
import cn.com.clubank.sms.manage.message.MessageQueueUtil;
import cn.com.clubank.sms.manage.message.WYiCloudMsgUtil;
import cn.com.clubank.sms.manage.message.service.MessageHandleService;
import cn.com.clubank.sms.manage.send.dao.SendFailRecordMapper;
import cn.com.clubank.sms.manage.send.dao.SendRecordMapper;
import cn.com.clubank.sms.manage.send.dao.SendSuccessRecordMapper;
import cn.com.clubank.sms.manage.send.pojo.SendFailRecord;
import cn.com.clubank.sms.manage.send.pojo.SendRecord;
import cn.com.clubank.sms.manage.send.pojo.SendSuccessRecord;
import cn.com.clubank.sms.manage.sys.dao.AccountInfoMapper;
import cn.com.clubank.sms.manage.sys.dao.AccountRechargeMapper;
import cn.com.clubank.sms.manage.sys.pojo.AccountInfo;
import cn.com.clubank.sms.manage.sys.pojo.AccountRecharge;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息处理
 * @author YangWei
 *
 */
@Slf4j
@Service
public class MessageHandleServiceImpl implements MessageHandleService {

	@Autowired
	private AppTemplateMapper appTemplateMapper;
	
	@Autowired
	private SendRecordMapper sendRecordMapper;
	
	@Autowired
	private SendSuccessRecordMapper sendSuccessRecordMapper;
	
	@Autowired
	private SendFailRecordMapper sendFailRecordMapper;
	
	@Autowired
	private AccountInfoMapper accountInfoMapper;
	
	@Autowired
	private AccountRechargeMapper accountRechargeMapper;
	
	@Autowired
	private WYiCloudMsgUtil wYiCloudMsgUtil;
	
	@Autowired
	private MessageQueueUtil messageQueueUtil;
	
	@Override
	public void sendSms(MessageEntity result, Integer index) {
		// 调用网易发送短信接口
		JSONObject wyiResult = null;
		try {
			String res = "";
			if (result.getType() == MessageEntity.type_code) {
				// 验证码
				res = wYiCloudMsgUtil.sendCode(result.getTemplateId(), result.getMobiles(), result.getParams());
			} else {
				// 通知类、运营类
				res = wYiCloudMsgUtil.sendTemplete(result.getTemplateId(), result.getMobiles(), result.getParams());
			}
			log.info("【网易接口】调用网易云短信接口发送成功:{}", res);
			wyiResult = JSON.parseObject(res);
		} catch (Exception e) {
			log.info("【网易接口】调用网易云短信接口发送失败！");
			e.printStackTrace();
		}

		if (wyiResult.getInteger("code") != 200 && result.getTimes() < 3) {
			// 发送失败，重新插入队列发送
//			log.info("【{}】第{}次发送失败：{}", index, result.getTimes(), JSON.toJSONString(result));
			result.setTimes(result.getTimes() + 1);
			messageQueueUtil.insertFailLast(result);
		} else {
			// 插入发送结果消息队列
			result.setWyiResult(wyiResult);
//			log.info("【{}】插入发送结果的消息队列：{}", index, JSON.toJSONString(result));
			messageQueueUtil.insertResultFirst(result);
		}
	}

	@Override
	public void handleResult(MessageEntity result) {
		// 保存发送记录信息
		AppTemplate temp = appTemplateMapper.selectByCode(result.getTempCode());
		JSONObject wyiResult = result.getWyiResult();
		
		SendRecord sendRecord = new SendRecord();
		sendRecord.setAccountId(temp.getAccountId());
		sendRecord.setAppId(temp.getAppId());
		sendRecord.setAppName(temp.getAppName());
		sendRecord.setParams(result.getParams());
		if (wyiResult.getInteger("code") == 200) {
			sendRecord.setSendId(result.getType() == MessageEntity.type_code ? wyiResult.getString("msg") : wyiResult.getString("obj"));
			sendRecord.setWyiMsg("Sent successfully");
		} else {
			sendRecord.setSendId("Fail " + wyiResult.getInteger("code"));
			sendRecord.setWyiMsg(wyiResult.getString("msg"));
		}
		sendRecord.setWyiCode(wyiResult.getInteger("code"));
		sendRecord.setSinglePrice(null); // 短信单价
		sendRecord.setSmsType(temp.getSmsType());
		sendRecord.setTempCode(result.getTempCode());
		sendRecord.setSendQuantity(result.getMobileNum()); // 发送数量
		// 发送内容
		String content = temp.getTempContent();
		if (temp.getSmsType() == AppTemplate.type_code || temp.getSmsType() == AppTemplate.type_voice) {
			// 验证码
			content = content.replace("%s", result.getParams());
		} else {
			// 通知运营类
			content = String.format(content, JSON.parseArray(result.getParams()).toArray());
		}
		sendRecord.setTempContent(content);
		sendRecordMapper.insert(sendRecord);
		
		try {
			if (wyiResult.getInteger("code") == 200) {
				// 根据网易的sendid查询发送状态（解决状态延迟：查询三次，最后一次休息一秒）
				JSONArray statusResults = null;
				int times = 0; //循环查询的次数
				do {
					String res = wYiCloudMsgUtil.sendStatus(sendRecord.getSendId());
					log.info("【网易接口】调用网易云接口查询短信发送状态成功:{}", res);
					JSONObject statusResult = JSON.parseObject(res);
					if (statusResult.getInteger("code") != 200) {
						statusResults = new JSONArray();
						break;
					}
					statusResults = statusResult.getJSONArray("obj");
					times++;
					if (times > 1) {
						// 休息一秒
						TimeUnit.SECONDS.sleep(1);
					}
				} while (times < 3 && statusResults.getJSONObject(0).getInteger("status") == 0);
				
				// 单个号码状态对象
				JSONObject status = null;
				// 成功数量
				int successQuantity = 0;
				// 失败数量
				int failQuantity = 0;
				
				// 解析查询结果更新至发送成功表和失败表
				for (int i = 0;i < statusResults.size();i++) {
					status = statusResults.getJSONObject(i);
					if (status.getInteger("status") == 1) {
						//成功
						sendSuccessRecordMapper.insert(new SendSuccessRecord(status.getString("mobile"), sendRecord.getId()));
						successQuantity++;
					} else {
						//失败
						sendFailRecordMapper.insert(new SendFailRecord(status.getString("mobile"), sendRecord.getId(), status.getInteger("status")));
						failQuantity++;
					}
				}
				sendRecord.setSuccessQuantity(successQuantity);
				sendRecord.setFailQuantity(failQuantity);
				sendRecordMapper.updateByPrimaryKey(sendRecord);
				
				// 查询账户最近的充值记录，按照发送成功量扣除费用
				AccountInfo account = accountInfoMapper.selectByPrimaryKey(Integer.valueOf(sendRecord.getAccountId()));
				AccountRecharge recharge = null;
				while (successQuantity > 0) {
					// 按照时间顺排序，获取第一个短信条数不为0的充值记录（也就是最早充值的记录）
					recharge = accountRechargeMapper.getHaveLastEarliest(sendRecord.getAccountId());
					if (recharge == null) {
						//TODO 没有短信条数的情况
						break;
					}
					if (recharge.getLastQuantity() >= successQuantity) {
						// 更新充值记录中的数据
						accountRechargeMapper.updateAmountByIdAndSuccess(recharge.getId(), successQuantity);
						
						// 更新账户表中的发送总条数
						accountInfoMapper.updateAmountByAccountId(account.getId(), successQuantity);
						
						successQuantity = 0;
					} else {
						successQuantity = successQuantity - recharge.getLastQuantity();
						// 更新账户表中的发送总条数
						accountInfoMapper.updateAmountByAccountId(account.getId(), recharge.getLastQuantity());
						
						// 更新充值记录中的数据
						accountRechargeMapper.lastQuantityUseEnd(recharge.getId());
					}
				}
			}
		} catch (Exception e) {
			log.info("【网易接口】调用网易云短信查询发送状态失败！");
			e.printStackTrace();
		} finally {
			// 更新预扣短信数量
			messageQueueUtil.decreaseAccountSmsAmount(result.getType(), result.getAccountId(), result.getMobiles());
		}
	}

}
