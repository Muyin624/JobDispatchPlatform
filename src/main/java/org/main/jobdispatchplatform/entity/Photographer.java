package org.main.jobdispatchplatform.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Photographer {
    private int id;

    @NotBlank(message = "摄影师名不能为空")
    private String name;

    private LocalDateTime createTime;
}
