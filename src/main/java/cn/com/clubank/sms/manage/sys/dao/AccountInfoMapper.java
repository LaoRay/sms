package cn.com.clubank.sms.manage.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.com.clubank.sms.manage.sys.pojo.AccountInfo;

public interface AccountInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AccountInfo record);

    int insertSelective(AccountInfo record);

    AccountInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountInfo record);

    int updateByPrimaryKey(AccountInfo record);
    
    /**
     * 根据id和短信成功条数修改账户的发送短信条数及剩余短信条数
     * @return
     */
    int updateAmountByAccountId(@Param(value="id") Integer id, @Param(value="successQuantity") Integer successQuantity);
    
    /**
     * 通过账户名称查询账户信息
     * @param accountName 账户名称
     * @return
     */
    AccountInfo selectByAccountName(String accountName);
    
    /**
     * 统计账户个数
     * @return
     */
    int selectAccountNumber();
    
    /**
     * 查询账户列表（管理员首页）10条
     * @return
     */
    List<AccountInfo> selectAccountList();
    
    /**
     * 查询所有账户（管理员统计用）
     * @return
     */
    List<AccountInfo> selectAllAcount();
    
    /**
     * 删除账户：将删除标记设置为1
     * @param accountId 账户id
     * @return
     */
    int updateDeleteMark(int accountId);
}