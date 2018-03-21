package cn.com.clubank.sms.manage.send.dao;

import cn.com.clubank.sms.manage.send.pojo.SendFailRecord;

public interface SendFailRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SendFailRecord record);

    int insertSelective(SendFailRecord record);

    SendFailRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SendFailRecord record);

    int updateByPrimaryKey(SendFailRecord record);
}