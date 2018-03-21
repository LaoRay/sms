package cn.com.clubank.sms.manage.sys.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息表
 * @author Liangwl
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
	/**
	 * 用户主键
	 */
    private Integer id;

    /**
     * 账户id
     */
    private String accountId;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 姓名
     */
    private String realname;

    /**
     * 性别：1-男，2-女
     */
    private Integer gender;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;
    
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