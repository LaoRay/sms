package cn.com.clubank.sms.manage.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * redis中消息
 * 
 * @author YangWei
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

	/**
	 * 短信类型：1-验证码
	 */
	public static final int type_code = 1;
	/**
	 * 短信类型：2-通知运营类短信
	 */
	public static final int type_temp = 2;

	/**
	 * 短信类型：1-验证码，2-通知运营类
	 */
	private int type = 1;

	/**
	 * 第三方模版id
	 */
	private String templateId;

	/**
	 * 模版编号：远古短信平台的模版编号
	 */
	private String tempCode;

	/**
	 * 手机号码
	 */
	private String mobiles;

	/**
	 * 参数（验证码类型时为验证码）
	 */
	private String params;

	/**
	 * 账户id
	 */
	private String accountId;

	/**
	 * 发送次数，当有发送失败的短信重新发送时使用
	 */
	private Integer times = 1;

	/**
	 * 网易云接口的发送结果
	 */
	private JSONObject wyiResult;

	public MessageEntity(int type, String templateId, String accountId, String tempCode, String mobiles,
			String params) {
		super();
		this.type = type;
		this.templateId = templateId;
		this.tempCode = tempCode;
		this.mobiles = mobiles;
		this.params = params;
		this.accountId = accountId;
	}

	/**
	 * 返回短信数量
	 * 
	 * @return
	 */
	public int getMobileNum() {
		if (type == type_code) {
			return 1;
		}

		return JSON.parseArray(mobiles).size();
	}

}
