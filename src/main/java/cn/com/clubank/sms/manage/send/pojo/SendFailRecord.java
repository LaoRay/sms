package cn.com.clubank.sms.manage.send.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送失败记录表
 * @author Liangwl
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendFailRecord {
	/**
	 * 发送失败记录主键
	 */
    private Integer id;

    /**
     * 发送记录id
     */
    private String recordId;

    /**
     * 接收者号码列表
     */
    private String mobile;

    /**
     * 失败状态：0-未发送、2-发送失败、3-反垃圾
     */
    private Integer failStatus;

    /**
     * 描述
     */
    private String failDesc;

	public SendFailRecord(String mobile, int recordId, Integer failStatus) {
		super();
		this.recordId = recordId + "";
		this.mobile = mobile;
		this.failStatus = failStatus;
		if (failStatus == 0) {
			failDesc = "未发送";
		} else if (failStatus == 2) {
			failDesc = "发送失败";
		} else if (failStatus == 3) {
			failDesc = "反垃圾";
		} else {
			failDesc = "未知";
		}
	}
}