package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.main.jobdispatchplatform.entity.User;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);

    @Insert("INSERT INTO user (phone, user_name, password, role, channel_id) VALUES (#{phone},#{userName},#{password},#{role},#{channelId})")
    void insert(User user);
}
