package cn.com.clubank.sms.controller.statistics;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.clubank.sms.common.response.ResponseJson;
import cn.com.clubank.sms.common.utils.Constants;
import cn.com.clubank.sms.manage.statistics.SmsStatisticsService;

/**
 * 统计
 * @author Liangwl
 *
 */
@Controller
@RequestMapping("statistics")
public class StatisticsController {

	@Autowired
	private SmsStatisticsService smsStatisticsService;
	
	/**
	 * 获得柱状图所需数据(发送总量统计)
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
	 * 获得饼图所需数据（短信类型发送总量统计）
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
	 * 获得折线图所需数据
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/lineChart", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainLineChartDate(@RequestBody JSONObject param, HttpServletRequest request){
		if(request.getHeader("roles").indexOf(Constants.Sort_99) != -1){
			if(param.getInteger("key") == 1){// 1-半年、2-一年
				return smsStatisticsService.accountHalfYearStatistics(null);
			}else{
				return smsStatisticsService.accountOneYearStatistics(param.getString("year"));
			}
		}else{
			if(param.getInteger("key") == 1){// 1-半年、2-一年
				return smsStatisticsService.appHalfYearStatistics(request.getIntHeader("userId"), param.getString("accountId"), null);
			}else{
				return smsStatisticsService.appOneYearStatistics(request.getIntHeader("userId"), param.getString("accountId"), param.getString("year"));
			}
		}
	}
	
	/**
	 * 获得账户下拉框（统计用）
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/comboBox", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJson gainAccountComboBox(HttpServletRequest request){
		
		return smsStatisticsService.selectAllAccount(request.getIntHeader("userId"), request.getHeader("roles"));
	}	
}
