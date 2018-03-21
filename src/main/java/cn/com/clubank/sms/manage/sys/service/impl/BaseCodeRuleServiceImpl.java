package cn.com.clubank.sms.manage.sys.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.com.clubank.sms.common.redis.JedisClient;
import cn.com.clubank.sms.common.redis.RedisKeyCode;
import cn.com.clubank.sms.manage.sys.service.BaseCodeRuleService;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author Administrator
 * 获取编号服务
 */
@Slf4j
@Service
public class BaseCodeRuleServiceImpl implements BaseCodeRuleService{
	
	@Autowired
	private JedisClient jedisClient;

	/**
	 * 获取编号服务
	 * @param 编码类型 内容来自枚举BaseCodeRule.XX.getCode();
	 * @return 编号信息
	 */
	@Override
	public synchronized String getCode(String itemCode) {
		// 统一生成
		String today = RedisKeyCode.BASE_CODE + new SimpleDateFormat("yyyyMMdd").format(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = RedisKeyCode.BASE_CODE + new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		String result = itemCode + new SimpleDateFormat("yyyyMMdd").format(new Date());
		Boolean exists = jedisClient.exists(today);
		Jedis jedis = null;
		try {
			if (!exists) {
				jedisClient.del(yesterday);
				jedisClient.setnxNoEncode(today, "0");
			}
			jedis = jedisClient.getJedis();
			jedisClient.watch(today);
			Transaction tx = jedis.multi(); // 开启事务
			tx.incr(today);
			List<Object> list = tx.exec(); // 提交事务
			String code = jedisClient.getNoEncode(today);
			if (list != null && list.size() > 0 && StringUtils.isNotBlank(code)) {
				if (code.length() < 4) {
					for (int i = 0; i < 4 - code.length(); i++) {
						result += "0";
					}
				}
				result += code;
			} else {
				result = itemCode + new SimpleDateFormat("yyDDDHHmmssSSS").format(new Date());
			}
		} catch (Exception e) {
			log.error("生成编号异常", e);
			result = itemCode + new SimpleDateFormat("yyDDDHHmmssSSS").format(new Date());
		} finally {
			jedis.close();
		}
		log.info("生成编号：{}", result);
		return result;
	}
	
}
