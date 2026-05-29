package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.main.jobdispatchplatform.entity.Photographer;

import java.util.List;

@Mapper
public interface PhotographerMapper {
    @Select("SELECT * FROM photographer")
    List<Photographer> findAll();

    @Select("SELECT * FROM photographer WHERE id=#{id}")
    Photographer findById(int id);

    @Insert("INSERT INTO photographer(name) VALUES (#{name})")
    int insert(Photographer photographer);

    @Update("UPDATE photographer SET name = #{name} WHERE id = #{id}")
    int update(Photographer photographer);

    @Delete("DELETE FROM photographer WHERE id = #{id}")
    int delete(int id);
}
