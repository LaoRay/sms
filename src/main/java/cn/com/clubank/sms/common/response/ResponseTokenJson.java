package cn.com.clubank.sms.common.response;

/**
 * 响应对象-token
 * @author YangWei
 *
 */
public class ResponseTokenJson extends ResponseJson {
	
	private String token;
	
	public ResponseTokenJson(ResultCode code, String token, String msg) {
		super(code, msg);
		this.token = token;
	}

	public String getToken() {
		return token;
	}
	
}
