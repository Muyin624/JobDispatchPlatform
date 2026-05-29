package org.main.jobdispatchplatform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.main.jobdispatchplatform.entity.BookingOrder;

import java.util.List;

@Mapper
public interface BookingOrderMapper {
    @Select("SELECT * FROM booking_order")
    List<BookingOrder> findAll();

    @Select("SELECT * FROM booking_order WHERE id =#{id}")
    BookingOrder findById(int id);

    @Insert("INSERT INTO booking_order(channel_id, photographer_id, appointment_time, appointment_address, status, update_time) VALUES (#{channelId}, #{photographerId}, #{appointmentTime}, #{address}, #{status}, #{updateTime})")
    void insert(BookingOrder bookingOrder);

    @Update("UPDATE booking_order SET appointment_time = #{appointmentTime}, appointment_address = #{address} WHERE id = #{id}")
    void update(BookingOrder bookingOrder);
}
