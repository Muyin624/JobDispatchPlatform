package org.main.jobdispatchplatform.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingOrder {
    private int id;

    @Min(value = 1,message = "渠道ID不能为空")
    private int channelId;

    private int photographerId;

    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    private LocalDateTime updateTime;

    @NotBlank(message = "预约地址不能为空")
    private String address;
    private LocalDateTime createTime;
    private int status;  // 0待分配 1已分配 2已完成 3已取消
}
