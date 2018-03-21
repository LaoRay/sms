package cn.com.clubank.sms.manage.app.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用短信模板
 * @author Liangwl
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppTemplate {
	
	/**
	 * 短信类型：1-验证码
	 */
	public static final int type_code = 1;
	/**
	 * 短信类型：2-通知类短信
	 */
	public static final int type_notice = 2;
	/**
	 * 短信类型：3-运营类短信
	 */
	public static final int type_operate = 3;
	/**
	 * 短信类型：4-语音验证码
	 */
	public static final int type_voice = 4;
	
	/**
	 * 应用短信模板主键
	 */
    private Integer id;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;
    
    /**
     * 应用编号
     */
    private String appSort;

    /**
     * 模版编号：生成的唯一标识code
     */
    private String tempCode;

    /**
     * 第三方模版id
     */
    private String templateId;

    /**
     * 模版状态：0-待审核，1-审核通过，2-审核不通过
     */
    private Integer status;

    /**
     * 模版内容
     */
    private String tempContent;
    
    /**
     * 驳回原因
     */
    private String dismissal;

    /**
     * 短信类型：1-短信验证码，2-通知类短信，3-运营类短信，4-语音验证码
     */
    private Integer smsType;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 修改日期
     */
    private Date modifyDate;

    /**
     * 有效标志：默认1-有效，0-无效
     */
    private Integer enabledMark = 1;

    /**
     * 删除标记
     */
    private Integer deleteMark = 0;

    /**
     * 备注
     */
    private String description;
    
    /**
     * 自定义：帐号id
     */
    private String accountId;
    
}