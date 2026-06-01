package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.main.jobdispatchplatform.entity.BookingOrder;

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

    @Insert("INSERT INTO booking_order(channel_id, photographer_id, appointment_time, appointment_address, status, update_time) VALUES (#{channelId}, #{photographerId}, #{appointmentTime}, #{address}, #{status}, #{updateTime})")
    void insert(BookingOrder bookingOrder);

    @Update("UPDATE booking_order SET appointment_time = #{appointmentTime}, appointment_address = #{address} WHERE id = #{id}")
    void update(BookingOrder bookingOrder);

    @Update("UPDATE booking_order SET photographer_id = #{photographerId},status = #{status},update_time = #{updateTime} WHERE id = #{id}")
    void assign(BookingOrder bookingOrder);
}
