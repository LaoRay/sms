   package cn.com.clubank.sms.manage.app.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.app.pojo.AppTemplate;

public interface AppTemplateMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(AppTemplate record);

    int insertSelective(AppTemplate record);

    AppTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppTemplate record);

    int updateByPrimaryKey(AppTemplate record);
    
    /**
     * 根据模版编号获取模版详情
     * @param code
     * @return
     */
    AppTemplate selectByCode(String code);
    
    /**
     * 根据模版编号获取第三方模版id
     * @param code
     * @return
     */
    String selectTemplateIdByCode(String code);
    
    /**
     * 通过模板编号、应用编号、应用名称模糊查询到的条数
     * @param tempCode 模板编号
     * @param appSort 应用编号
     * @param appName 应用名称
     * @param key 1-（待审核）2-（待审核、审核通过、审核不通过）
     * @return
     */
    int selectTotal(@Param("accountId")String accountId, @Param("tempCode")String tempCode, @Param("appSort")String appSort, @Param("appName")String appName, @Param("key")Integer key);
    
    /**
     * 通过模板编号、应用编号、应用名称模糊查询模板列表并分页
     * @param tempCode 模板编号
     * @param appSort 应用编号
     * @param appName 应用名称
     * @param key 1-（待审核）2-（待审核、审核通过、审核不通过）
     * @param startIndex 起始索引
     * @param pageSize 页容量
     */
    List<AppTemplate> selectTemplateListAndPage(@Param("accountId")String accountId, @Param("tempCode")String tempCode, @Param("appSort")String appSort, @Param("appName")String appName, @Param("key")Integer key, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 删除模板：将删除标记设置为1
     * @param tempId 模板id
     * @return
     */
    int updateDeleteMark(Integer tempId);
    
    /**
     * 审核模板
     * @param tempId 模板id
     * @param key 1-审核通过、2-审核不通过
     * @param templateId 第三方模板id
     * @param dismissal 驳回原因
     * @return
     */
    int updateAuditTemplate(@Param("tempId")Integer tempId, @Param("key")Integer key, @Param("templateId")String templateId, @Param("dismissal")String dismissal);
   
    /**
     * 统计待审核模板个数
     * @return
     */
    int selectCheckPending();
    
    /**
     * 获得应用下模板列表
     * @param appId 应用id
     * @return
     */
    List<AppTemplate> selectTempListByAppId(String appId);
}