package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.main.jobdispatchplatform.entity.Channel;

import java.util.List;

@Mapper
public interface ChannelMapper {
    @Select("SELECT * FROM channel")
    List<Channel> findAll();

    @Select("SELECT * FROM channel WHERE id = #{id}")
    Channel findById(int id);

    @Insert("INSERT INTO channel(name) VALUES (#{name})")
    int insert(Channel channel);

    @Update("UPDATE channel SET name = #{name} WHERE id = #{id}")
    int update(Channel channel);

    @Delete("DELETE FROM channel WHERE id = #{id}")
    int delete(int id);
}
