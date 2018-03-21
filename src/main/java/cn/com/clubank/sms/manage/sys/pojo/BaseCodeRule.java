package cn.com.clubank.sms.manage.sys.pojo;

/**
 * @author wangtao
 * 编号规则项目表：
 * 账户编号 ACCOUNT
 * 充值流水号 SERIAL
 * 应用编号 APPCODE
 * 模板编号 TEMPCODE
 */
public enum BaseCodeRule {
	
	/**
	 * 鉴权帐号
	 */
	AUTH_ACCOUNT("CLUBANK"),
	
	/**
	 * 账户编号
	 */
	ACCOUNT("ID"),
	
	/**
	 * 账户充值记录流水编号
	 */
	SERIAL("SER"),
	
	/**
	 * 应用编号
	 */
	APPCODE("APP"),
	
	/**
	 * 模版编号
	 */
	TEMPCODE("TEMP");
	
	private String value;
	
	private BaseCodeRule (String value){
		this.value = value;
	}
	
	public String getValue() {
        return value;
    }
	
}
