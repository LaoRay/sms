package cn.com.clubank.sms.manage.sys.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单信息表
 * @author Liangwl
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysMenu {
	/**
	 * 菜单主键
	 */
    private Integer id;

    /**
     * 父菜单id
     */
    private Integer parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单链接
     */
    private String menuUrl;

    /**
     * 排序
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
    
    /**
     * 自定义：父菜单名称
     */
    private String parentName;
}