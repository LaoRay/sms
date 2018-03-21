package cn.com.clubank.sms.manage.sys.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色信息表
 * @author Liangwl
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {
	/**
	 * 角色主键
	 */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色序号
     */
    private Integer sort;
    
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