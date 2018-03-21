package cn.com.clubank.sms.manage.send.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送成功记录表
 * @author Liangwl
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSuccessRecord {
	/**
	 * 发送成功记录主键
	 */
    private Integer id;

    /**
     * 接收者号码列表
     */
    private String mobile;

    /**
     * 发送记录id
     */
    private String recordId;

	public SendSuccessRecord(String mobile, int recordId) {
		super();
		this.mobile = mobile;
		this.recordId = recordId + "";
	}
}