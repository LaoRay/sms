package cn.com.clubank.sms.manage.app.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用信息表
 * @author Liangwl
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppInfo {
	/**
	 * 应用信息主键
	 */
    private Integer id;

    /**
     * 账户id
     */
    private String accountId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用编号
     */
    private String appSort;

    /**
     * 应用状态：0-待审核、1-审核通过、2-审核不通过
     */
    private Integer status;
    
    /**
     * 驳回原因
     */
    private String dismissal;

    /**
     * 第三方应用id
     */
    private String thirdId;

    /**
     * 应用签名
     */
    private String signature;

    /**
     * 标识key：32位的唯一标识key
     */
    private String appKey;

    /**
     * 发送总条数
     */
    private Integer sendTotalQuantity;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 修改日期
     */
    private Date modifyDate;

    /**
     * 有效标志：1-有效、0-无效
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