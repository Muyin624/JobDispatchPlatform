package org.main.jobdispatchplatform.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Spot {
    private int id;

    @NotBlank(message = "地点名称不能为空")
    private String name;

    @NotNull(message = "经度不能为空")
    private Double longitude;

    @NotNull(message = "纬度不能为空")
    private Double latitude;

    @NotBlank(message = "详细地址不能为空")
    private String address;

    private LocalDateTime createTime;
}
