package org.main.jobdispatchplatform.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingOrder {
    private int id;

    @Min(value = 1,message = "用户不存在")
    private int userId;

    private int channelId;

    private int photographerId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private LocalDateTime updateTime;

    @Min(value = 1,message = "地点id没有选择")
    private int spotId;
    private String address;

    private LocalDateTime createTime;
    private int status;  // 0待分配 1已分配 2已完成 3已取消
}
