package org.main.jobdispatchplatform.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Channel {
    private int id;

    @NotBlank(message = "渠道名不能为空")
    private String name;

    private LocalDateTime createTime;
}
