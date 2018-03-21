package cn.com.clubank.sms.manage.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.clubank.sms.common.utils.StringUtil;

/**
 * 网易视频云短信接口处理
 * 
 * @author YangWei
 *
 */
@Component
public class WYiCloudMsgUtil {

	@Value("${wyi.codeUrl}")
	private String CODE_SERVER_URL; // 发送验证码的请求路径URL
	
	@Value("${wyi.templateUrl}")
	private String TEMP_SERVER_URL; // 发送通知类、运营类短信的请求路径URL
	
	@Value("${wyi.statusUrl}")
	private String QUERY_STATUS_URL; // 查询发送状态路径URL
	
	@Value("${wyi.appKey}")
	private String APP_KEY; // 网易云信分配的账号，请替换你在管理后台应用下申请的Appkey
	
	@Value("${wyi.appSecret}")
	private String APP_SECRET; // 网易云信分配的密钥，请替换你在管理后台应用下申请的appSecret
	
	@Value("${wyi.nonce}")
	private String NONCE; // 随机数

	/**
	 * 发送验证码
	 * @param tempId
	 * @param mobile
	 * @param authCode
	 * @throws Exception
	 */
	public String sendCode(String tempId, String mobile, String authCode) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(CODE_SERVER_URL);
		String curTime = String.valueOf((new Date()).getTime() / 1000L);
		
		//参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
		String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, NONCE, curTime);

		// 设置请求的header
		httpPost.addHeader("AppKey", APP_KEY);
		httpPost.addHeader("Nonce", NONCE);
		httpPost.addHeader("CurTime", curTime);
		httpPost.addHeader("CheckSum", checkSum);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// 设置请求的的参数，requestBody参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("templateid", tempId));
		nvps.add(new BasicNameValuePair("mobile", mobile));
		nvps.add(new BasicNameValuePair("authCode", authCode));

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

		// 执行请求
		HttpResponse response = httpClient.execute(httpPost);
		
		/* 1.打印执行结果，打印结果一般会200、315、403、404、413、414、500
		 * 2.具体的code有问题的可以参考官网的Code状态表*/
		 
		return EntityUtils.toString(response.getEntity(), "utf-8");
		
		// 测试
		/*Thread.sleep(50);
		JSONObject res = new JSONObject();
		if ("13600536032".equals(mobile)) {
			res.put("code", "315");
		} else {
			res.put("code", "200");
		}
		res.put("msg", System.currentTimeMillis());
		res.put("obj", authCode);
		return res.toString();*/
	}
	
	/**
	 * 发送通知类短信
	 * @param tempId
	 * @param mobile
	 * @param authCode
	 * @throws Exception
	 */
	public String sendTemplete(String tempId, String mobiles, String params) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(TEMP_SERVER_URL);
		String curTime = String.valueOf((new Date()).getTime() / 1000L);
		
		//参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
		String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, NONCE, curTime);

		// 设置请求的header
		httpPost.addHeader("AppKey", APP_KEY);
		httpPost.addHeader("Nonce", NONCE);
		httpPost.addHeader("CurTime", curTime);
		httpPost.addHeader("CheckSum", checkSum);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// 设置请求的的参数，requestBody参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		/*
		 * 参数格式是jsonArray的格式，例如
		 * "['13888888888','13666666666']"
		 * params是根据你模板里面有几个参数，那里面的参数也是jsonArray格式
		 */
		nvps.add(new BasicNameValuePair("templateid", tempId));
		nvps.add(new BasicNameValuePair("mobiles", mobiles));
		if (StringUtil.isNotBlank(params)) {
			nvps.add(new BasicNameValuePair("params", params));
		}

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

		// 执行请求
		HttpResponse response = httpClient.execute(httpPost);
		/*
		 * 1.打印执行结果，打印结果一般会200、315、403、404、413、414、500
		 * 2.具体的code有问题的可以参考官网的Code状态表
		 */
		return EntityUtils.toString(response.getEntity(), "utf-8");
	}

	/**
	 * 查询发送状态
	 * @param tempId
	 * @param mobile
	 * @param authCode
	 * @throws Exception
	 */
	public String sendStatus(String sendId) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(QUERY_STATUS_URL);
		String curTime = String.valueOf((new Date()).getTime() / 1000L);
		
		//参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
		String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, NONCE, curTime);

		// 设置请求的header
		httpPost.addHeader("AppKey", APP_KEY);
		httpPost.addHeader("Nonce", NONCE);
		httpPost.addHeader("CurTime", curTime);
		httpPost.addHeader("CheckSum", checkSum);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// 设置请求的的参数，requestBody参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("sendid", sendId));

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

		// 执行请求
		HttpResponse response = httpClient.execute(httpPost);
		
		
		 /* 1.打印执行结果，打印结果一般会200、315、403、404、413、414、500
		 * 2.具体的code有问题的可以参考官网的Code状态表*/
		 
		return EntityUtils.toString(response.getEntity(), "utf-8");
		
		// 测试
		/*Thread.sleep(50);
		
		JSONObject res = new JSONObject();
		JSONArray arr = new JSONArray();
		
		JSONObject obj = new JSONObject();
		if ("13600536033".equals(sendId)) {
			obj.put("status", 2);
		} else {
			obj.put("status", 1);
		}
		obj.put("mobile", sendId);
		obj.put("updatetime", System.currentTimeMillis());
		arr.add(obj);
		
		res.put("code", "200");
		res.put("obj", arr);
		
		return res.toString();*/
	}
	
}
