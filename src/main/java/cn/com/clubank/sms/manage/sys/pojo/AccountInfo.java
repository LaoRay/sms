package cn.com.clubank.sms.manage.sys.pojo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户信息表
 * @author Liangwl
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
	/**
	 * 账户信息主键
	 */
    private Integer id;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 账户编号
     */
    private String accountSort;

    /**
     * 鉴权账号
     */
    private String authAccount;

    /**
     * 鉴权密码
     */
    private String authPwd;

    /**
     * 充值总额
     */
    private BigDecimal rechargeTotal = new BigDecimal(0.00);

    /**
     * 短信总条数
     */
    private Integer smsTotalQuantity = 0;

    /**
     * 赠送总条数
     */
    private Integer handselTotalQuantity = 0;

    /**
     * 发送总条数
     */
    private Integer sendTotalQuantity = 0;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 修改日期
     */
    private Date modifyDate;

    /**
     * 有效标志：1-有效，0-无效
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
}