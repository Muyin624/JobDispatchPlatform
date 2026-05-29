package org.main.jobdispatchplatform.service;

import org.main.jobdispatchplatform.entity.BookingOrder;
import org.main.jobdispatchplatform.entity.Channel;
import org.main.jobdispatchplatform.mapper.BookingOrderMapper;
import org.main.jobdispatchplatform.mapper.ChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingOrderService {
    @Autowired
    private BookingOrderMapper bookingOrderMapper;
    @Autowired
    private ChannelMapper channelMapper;

    public BookingOrder placeOrder(BookingOrder bookingOrder) {
        //检验渠道是否存在
        Channel channel = channelMapper.findById(bookingOrder.getChannelId());
        if (channel == null) {
            throw new RuntimeException("渠道不存在");
        }
        bookingOrder.setStatus(0);
        bookingOrder.setUpdateTime(LocalDateTime.now());
        bookingOrderMapper.insert(bookingOrder);
        return bookingOrder;
    }
}
