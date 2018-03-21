package cn.com.clubank.sms.manage.send.pojo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送记录表
 * @author Liangwl
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendRecord {
	/**
	 * 发送记录主键
	 */
    private Integer id;

    /**
     * 账户id
     */
    private String accountId;
    
    /**
     * 第三方返回的sendid
     */
    private String sendId;

    /**
     * 发送时间：创建日期
     */
    private Date createDate = new Date();

    /**
     * 短信类型：1-短信验证码，2-通知类短信，3-运营类短信，4-语音验证码
     */
    private Integer smsType = 1;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 模版编号
     */
    private String tempCode;

    /**
     * 模版内容
     */
    private String tempContent;

    /**
     * 短信参数列表
     */
    private String params;

    /**
     * 短信单价
     */
    private BigDecimal singlePrice;

    /**
     * 发送数量
     */
    private Integer sendQuantity = 0;

    /**
     * 成功数量
     */
    private Integer successQuantity = 0;

    /**
     * 失败数量
     */
    private Integer failQuantity = 0;
    
    /**
     * 网易接口返回状态码
     */
    private Integer wyiCode = 200;
    
    /**
     * 网易接口返回描述
     */
    private String wyiMsg;
    
    /**
     * 发送状态中文
     * @return
     */
    public String getSendStatus() {
    	if (wyiCode == 200) {
    		return "成功";
    	} else {
    		return "失败";
    	}
    }
}