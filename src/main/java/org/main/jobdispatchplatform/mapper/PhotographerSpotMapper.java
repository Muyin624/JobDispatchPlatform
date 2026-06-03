package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.main.jobdispatchplatform.entity.Photographer;
import org.main.jobdispatchplatform.entity.Spot;

import java.util.List;

@Mapper
public interface PhotographerSpotMapper {
    //    摄影师绑定地点
    @Insert("INSERT INTO photographer_spot(photographer_id,spot_id) VALUES (#{photographerId},#{spotId})")
    void insertSpot(@Param("photographerId") int photographerId, @Param("spotId") int spotId);

    @Delete("DELETE FROM photographer_spot WHERE id = #{id}")
    int delete(int id);

    @Select("SELECT spot.* FROM spot spot JOIN photographer_spot ps ON spot.id = ps.spot_id WHERE ps.photographer_id = #{photographerId}")
    List<Spot> findSpots(int photographerId);

    @Select("SELECT photographer.* FROM photographer photographer JOIN photographer_spot ps ON photographer.id = ps.photographer_id WHERE ps.spot_id = #{spotId}")
    List<Photographer> findPhotographers(int spotId);
}
