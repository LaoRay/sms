package cn.com.clubank.sms.manage.app.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.app.pojo.AppInfo;

public interface AppInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppInfo record);

    int insertSelective(AppInfo record);

    AppInfo selectByPrimaryKey(Integer id);
    
    /**
     * 根据标识key获取对象
     * @param appKey
     * @return
     */
    AppInfo selectByKey(String appKey);

    int updateByPrimaryKeySelective(AppInfo record);

    int updateByPrimaryKey(AppInfo record);
    
    /**
     * 通过应用编号、应用名称模糊查询，状态查询到的条数
     * @param appSort 应用编号
     * @param appName 应用名称
     * @param key 1-（待审核）2-（待审核、审核通过、审核不通过）
     * @return
     */
    int selectTotal(@Param("accountId")String accountId, @Param("appSort")String appSort, @Param("appName")String appName, @Param("key")Integer key);
    
    /**
     * 通过应用编号、应用名称模糊查询，状态查询应用列表并分页
     * @param appSort 应用编号
     * @param appName 应用名称
     * @param key 1-（待审核）2-（待审核、审核通过、审核不通过）
     * @param startIndex 起始索引
     * @param pageSize 页容量
     */
    List<AppInfo> selectAppListAndPage(@Param("accountId")String accountId, @Param("appSort")String appSort, @Param("appName")String appName, @Param("key")Integer key, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 删除应用：将删除标记设置为1
     * @param appId 应用id
     * @return
     */
    int updateDeleteMark(Integer appId);
    
    /**
     * 查询账户下审核通过应用列表（新增模板：应用名称下拉框）
     * @param accountId 账户id
     * @return
     */
    List<Map<String, Object>> selectAppListByAccountId(String accountId);
    
    /**
     * 审核应用
     * @param appId 应用id
     * @param key 1-审核通过、2-审核不通过
     * @param thirdId 第三方id
     * @param dismissal 驳回原因
     * @return
     */
    int updateAuditApp(@Param("appId")Integer appId, @Param("key")Integer key, @Param("thirdId")String thirdId, @Param("dismissal")String dismissal);
    
    /**
     * 通过应用名称获得应用信息
     * @param appName 应用名称
     * @return
     */
    AppInfo selectAppInfoByAppName(String appName);
    
    /**
     * 统计应用个数
     * @return
     */
    int selectAppNumber();
    
    /**
     * 统计待审核应用个数
     * @return
     */
    int selectCheckPending();
}