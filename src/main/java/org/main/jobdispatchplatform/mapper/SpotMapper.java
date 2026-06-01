package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.main.jobdispatchplatform.entity.Spot;

import java.util.List;

@Mapper
public interface SpotMapper {
    @Select("SELECT * FROM spot")
    List<Spot> findAll();

    @Select("SELECT * FROM spot WHERE id=#{id} ")
    Spot findById(int id);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("INSERT INTO spot(name,latitude,longitude,address) VALUES(#{name},#{latitude},#{longitude},#{address}) ")
    int insert(Spot spot);

    @Update("UPDATE spot SET name = #{name},latitude = #{latitude},longitude = #{longitude},address = #{address} WHERE id =#{id}")
    int update(Spot spot);

    @Delete("DELETE FROM spot WHERE id = #{id}")
    int delete(int id);

}
