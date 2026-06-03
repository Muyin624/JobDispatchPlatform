package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.main.jobdispatchplatform.entity.BookingOrder;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookingOrderMapper {
    @Select("SELECT * FROM booking_order")
    List<BookingOrder> findAll();

    @Select("SELECT * FROM booking_order ORDER BY create_time DESC LIMIT  #{pageSize} OFFSET #{offset}")
    List<BookingOrder> findByPage(@Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM booking_order")
    long count();

    @Select("SELECT * FROM booking_order WHERE id =#{id}")
    BookingOrder findById(int id);

//    摄影师分配校验
    @Select("SELECT * FROM booking_order WHERE photographer_id = #{photographerId} AND status IN(0,1) AND #{startTime} < end_time AND #{endTime} > start_time")
    List <BookingOrder> findConflictOrders( @Param("photographerId") int photographerId, @Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime);

//    查询摄影师今日订单数
    @Select("SELECT COUNT(*) FROM booking_order WHERE photographer_id = #{photographerId} AND status IN (0,1) AND DATE(start_time) = DATE(#{date})")
    long todayOrderCount(@Param("photographerId") int photographerId, @Param("date") LocalDateTime date);

    @Insert("INSERT INTO booking_order(channel_id, photographer_id, start_time,end_time, appointment_address, status, update_time,spot_id,user_id) VALUES (#{channelId}, #{photographerId}, #{startTime},#{endTime}, #{address}, #{status}, #{updateTime},#{spotId},#{userId})")
    void insert(BookingOrder bookingOrder);

    @Update("UPDATE booking_order SET start_time = #{startTime},end_time = #{endTime}, appointment_address = #{address},spot_id = #{spotId},status = #{status}  WHERE id = #{id}")
    void update(BookingOrder bookingOrder);

    @Update("UPDATE booking_order SET photographer_id = #{photographerId},status = #{status},update_time = #{updateTime} WHERE id = #{id}")
    void assign(BookingOrder bookingOrder);
}
