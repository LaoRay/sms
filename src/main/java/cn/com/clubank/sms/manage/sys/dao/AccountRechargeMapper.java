package cn.com.clubank.sms.manage.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.sys.pojo.AccountRecharge;

public interface AccountRechargeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AccountRecharge record);

    int insertSelective(AccountRecharge record);

    AccountRecharge selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountRecharge record);

    int updateByPrimaryKey(AccountRecharge record);
    
    /**
     * 根据id和发送成功的短信条数更新充值记录中的发送短信条数及剩余短信条数
     * @return
     */
    int updateAmountByIdAndSuccess(@Param(value="id") Integer id, @Param(value="successQuantity") Integer successQuantity);
    
    /**
     * 充值记录的短信条数使用结束
     * @return
     */
    int lastQuantityUseEnd(@Param(value="id") Integer id);
    
    /**
     * 获取存在剩余条数，并且最早的充值记录
     * @return
     */
    AccountRecharge getHaveLastEarliest(String accountId);
    
    /**
     * 根据账户id查询账户的剩余短信总条数
     * @param accountId
     * @return
     */
    int totalLastQuantity(String accountId);
    
    /**
     * 通过账户名称模糊查询，充值时间查询到充值记录的条数
     * @param accountName 账户名称
     * @param dateOne 充值时间1
     * @param dateTwo 充值时间2
     * @return
     */
    int selectTotal(@Param("accountName")String accountName, @Param("dateOne")String dateOne , @Param("dateTwo")String dateTwo);
    
    /**
     * 通过账户名称模糊查询，充值时间查询充值记录并分页
     * @param accountName 账户名称
     * @param dateOne 充值时间1
     * @param dateTwo 充值时间2
     * @param startIndex 起始索引
     * @param pageSize 页容量
     * @return
     */
    List<AccountRecharge> selectRechargeListAndPage(@Param("accountName")String accountName, @Param("dateOne")String dateOne , @Param("dateTwo")String dateTwo, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);
    
    /**
     * 通过账户id,获得账户下所有充值记录
     * @param accountId
     * @return
     */
    List<AccountRecharge> selectRechargeListByAccountId(String accountId);
}