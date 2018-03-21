package cn.com.clubank.sms.manage.sys.pojo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户充值记录表
 * @author Liangwl
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRecharge {
	/**
	 * 账户充值记录主键
	 */
    private Integer id;

    /**
     * 充值流水号
     */
    private String serialNumber;

    /**
     * 账户id
     */
    private String accountId;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 充值金额
     */
    private BigDecimal rechargeAmount;

    /**
     * 短信单价
     */
    private BigDecimal singlePrice;

    /**
     * 充值条数
     */
    private Integer smsQuantity;

    /**
     * 赠送条数
     */
    private Integer handselQuantity;

    /**
     * 已发送条数
     */
    private Integer sendQuantity;

    /**
     * 剩余赠送条数
     */
    private Integer lastHandselQuantity;

    /**
     * 剩余充值条数
     */
    private Integer lastQuantity;

    /**
     * 充值时间：创建日期
     */
    private Date createDate;

    /**
     * 备注
     */
    private String description;
    
    /**
     * 自定义字段：余额
     */
    private BigDecimal balance;
}