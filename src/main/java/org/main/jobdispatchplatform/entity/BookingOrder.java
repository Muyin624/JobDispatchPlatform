package org.main.jobdispatchplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingOrder {
    private int id;
    private int channelId;
    private int photographerId;
    private LocalDateTime appointmentTime;
    private LocalDateTime updateTime;
    private String address;
    private LocalDateTime createTime;
    private int status;  // 0待分配 1已分配 2已完成 3已取消
}
