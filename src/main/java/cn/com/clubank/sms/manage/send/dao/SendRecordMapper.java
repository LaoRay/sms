package cn.com.clubank.sms.manage.send.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.send.pojo.SendRecord;

public interface SendRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SendRecord record);

    int insertSelective(SendRecord record);

    SendRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SendRecord record);

    int updateByPrimaryKey(SendRecord record);
        
    /**
     * 通过应用名称、模板编号模糊查询、发送时间查询到发送记录的条数
     * @param appName 应用名称
     * @param status  200-成功，201-失败
	 * @param tempCode 模板编号
	 * @param dateOne 时间1
	 * @param dateTwo 时间2
	 * @return
     */
    int selectTotal(@Param("accountId")String accountId, @Param("appName")String appName, @Param("status")Integer status, @Param("tempCode")String tempCode, @Param("dateOne")String dateOne, @Param("dateTwo")String dateTwo);
    
    /**
     * 通过应用名称、模板编号模糊查询、发送时间查询发送记录列表
     * @param appName 应用名称
     * @param status  200-成功，201-失败
	 * @param tempCode 模板编号
	 * @param dateOne 时间1
	 * @param dateTwo 时间2
	 * @param startIndex 起始下标
	 * @param pageSize 页容量
	 * @return
     */
    List<SendRecord> selectSendRecordList(@Param("accountId")String accountId, @Param("appName")String appName, @Param("status")Integer status, @Param("tempCode")String tempCode, @Param("dateOne")String dateOne, @Param("dateTwo")String dateTwo, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 通过发送记录id,查询发送成功的条数
     * @param recordId 发送记录id
     * @return
     */
    int selectSuccessTotal(@Param("recordId")String recordId);
    
    /**
     * 查询发送成功记录详情列表
     * @param recordId 发送记录id
     * @param startIndex 起始下标
     * @param pageSize 页容量
     * @return
     */
    List<Map<String, Object>> selectSendSuccessList(@Param("recordId")String recordId, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 通过发送记录id,查询发送失败的条数
     * @param recordId 发送记录id
     * @return
     */
    int selectFailTotal(@Param("recordId")String recordId);
    
    /**
     * 查询发送失败记录详情列表
     * @param recordId 发送记录id
     * @param startIndex 起始下标
     * @param pageSize 页容量
     * @return
     */
    List<Map<String, Object>> selectSendFailList(@Param("recordId")String recordId, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 通过应用名称、模板编号、手机号模糊查询、发送时间查询到发送失败记录的条数
     * @param accountId 账户id
     * @param appName 应用名称
     * @param tempCode 模板编号
     * @param mobile 手机号
     * @param dateOne 时间1
     * @param dateTwo 时间2
     * @return
     */
    int selectFailRecordTotal(@Param("accountId")String accountId,@Param("appName")String appName, @Param("tempCode")String tempCode, @Param("mobile")String mobile, @Param("dateOne")String dateOne, @Param("dateTwo")String dateTwo);
    
    /**
     * 通过应用名称、模板编号、手机号模糊查询、发送时间查询发送失败记录列表
     * @param accountId 账户id
     * @param appName 应用名称
     * @param tempCode 模板编号
     * @param mobile 手机号
     * @param dateOne 时间1
     * @param dateTwo 时间2
     * @param startIndex 起始下标
     * @param pageSize 页容量
     * @return
     */
    List<Map<String, Object>> selectSendFailRecordList(@Param("accountId")String accountId, @Param("appName")String appName, @Param("tempCode")String tempCode, @Param("mobile")String mobile, @Param("dateOne")String dateOne, @Param("dateTwo")String dateTwo, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
        
    /**
     * 各账户发送量统计（一年）（管理员）
     * @param year 年（yyyy）
     * @return
     */
    List<Map<String ,Object>> selectAllRecordByYear(@Param("year")String year);
    
    /**
     * 各账户发送量统计（近半年）（管理员）
     * @param dateStart 日期开始
     * @param dateEnd 日期结束
     * @return
     */
    List<Map<String ,Object>> selectAllSendRecord(@Param("dateStart")String dateStart, @Param("dateEnd")String dateEnd);
    
    /**
     * 账户各应用发送量统计（一年）
     * @param accountId 账户id
     * @param year 年（yyyy）
     * @return
     */
    List<Map<String ,Object>> selectAppOneYearSends(@Param("accountId")String accountId, @Param("year")String year);
    
    /**
     * 账户各应用发送量统计（近半年）
     * @param accountId 账户id
     * @param dateStart 开始日期
     * @param dateEnd 结束日期
     * @return
     */
    List<Map<String ,Object>> selectAppHalfYearSends(@Param("accountId")String accountId, @Param("dateStart")String dateStart, @Param("dateEnd")String dateEnd);
    
    /**
     * 账户短信类型发送量统计
     * @param accountId
     * @param yearMonth
     * @return
     */
    List<Map<String ,Object>> selectSmsTypeSends(@Param("accountId")String accountId);
    
    /**
     * 账户短信发送量统计（一年）
     * @param accountId 账户id
     * @param year 年（yyyy）
     * @return
     */
    List<Map<String ,Object>> selectAccountOneYearSends(@Param("accountId")String accountId, @Param("year")String year);
    
    /**
     * 账户短信发送量统计（近半年）
     * @param accountId 账户id
     * @param dateStart 开始日期
     * @param dateEnd 结束日期
     * @return
     */
    List<Map<String ,Object>> selectAccountHalfYearSends(@Param("accountId")String accountId, @Param("dateStart")String dateStart, @Param("dateEnd")String dateEnd);

}