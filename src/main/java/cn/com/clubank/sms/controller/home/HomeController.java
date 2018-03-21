package cn.com.clubank.sms.controller.home;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.response.ResponseObjectJson;
import cn.com.clubank.sms.manage.statistics.SmsStatisticsService;
import cn.com.clubank.sms.manage.sys.service.AccountService;

/**
 * 首页
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private SmsStatisticsService smsStatisticsService;

	/**
	 * 获得登录用户及其所属账户信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/userAndAccountInfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainUserAndAccountInfo(HttpServletRequest request){
		 
		return accountService.selectUserDetail(request.getIntHeader("userId"));
	}
	
	/**
	 * 获得账户充值记录（最新的五条）
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/newRecharge", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainNewRecharge(HttpServletRequest request){
		ResponseObjectJson date = (ResponseObjectJson) accountService.selectUserDetail(request.getIntHeader("userId"));
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) date.getData();
		return accountService.selectRechargeListAndPage(map.get("accountName").toString(), null, null, 0, 5);
	}
	
	/**
	 * 获得首页柱状图所需数据
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/histogram", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainHistogramDate(@RequestBody JSONObject param, HttpServletRequest request){
		if(param.getInteger("key") == 1){// 1-半年、2-一年
			return smsStatisticsService.sendStatisticsHalfYear(request.getIntHeader("userId"), param.getString("accountId"), null);
		}
		return smsStatisticsService.sendStatisticsOneYear(request.getIntHeader("userId"), param.getString("accountId"), param.getString("year"));
	}
	
	/**
	 * 获得首页饼图所需数据
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/pieChart", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainpieChartDate(@RequestBody JSONObject param, HttpServletRequest request){
		
		return smsStatisticsService.typeStatistics(request.getIntHeader("userId"), param.getString("accountId"));
	}
	
	/**
	 * 管理员首页:概况
	 * @return
	 */
	@RequestMapping(value = "/overview", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainAllAccountNumber(){
		 
		return accountService.selectAllAccountNumber();
	}
	
	/**
	 * 管理员首页:账户列表
	 * @return
	 */
	@RequestMapping(value = "/accountList", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainAccountList(){
		 
		return accountService.selectAccountList();
	}
}
