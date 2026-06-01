package org.main.jobdispatchplatform.service;

import org.main.jobdispatchplatform.entity.BookingOrder;
import org.main.jobdispatchplatform.entity.Channel;
import org.main.jobdispatchplatform.entity.OrderStatus;
import org.main.jobdispatchplatform.entity.Photographer;
import org.main.jobdispatchplatform.mapper.BookingOrderMapper;
import org.main.jobdispatchplatform.mapper.ChannelMapper;
import org.main.jobdispatchplatform.mapper.PhotographerMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingOrderService {
    @Autowired
    private BookingOrderMapper bookingOrderMapper;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private PhotographerMapper photographerMapper;

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

    public void assignOrder(int orderId,int photographerId){
        BookingOrder bookingOrder = bookingOrderMapper.findById(orderId);
        if(bookingOrder==null){
            throw new RuntimeException("订单不存在");
        }

        Photographer photographer = photographerMapper.findById(photographerId);
        if(photographer==null){
            throw new RuntimeException("摄影师不存在");
        }

// 校验：非待分配状态直接拦截
        if (bookingOrder.getStatus() != OrderStatus.CREATED.getCode()) {
            throw new RuntimeException("订单不是待分配状态，无法分配");
        }

// 走到这里，说明一定是合法的，正常处理
        bookingOrder.setStatus(OrderStatus.ASSIGNED.getCode());
        bookingOrder.setPhotographerId(photographerId);
        bookingOrder.setUpdateTime(LocalDateTime.now());
        bookingOrderMapper.assign(bookingOrder);
    }
}
