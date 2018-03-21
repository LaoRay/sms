package cn.com.clubank.sms.manage.send.dao;

import cn.com.clubank.sms.manage.send.pojo.SendSuccessRecord;

public interface SendSuccessRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SendSuccessRecord record);

    int insertSelective(SendSuccessRecord record);

    SendSuccessRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SendSuccessRecord record);

    int updateByPrimaryKey(SendSuccessRecord record);
    
    /**
     * 获取当天发送验证码数量
     * @param mobile
     * @return
     */
    int getThatDayCodeCount(String mobile);
}