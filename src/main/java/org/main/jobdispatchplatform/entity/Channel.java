package org.main.jobdispatchplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Channel {
    private int id;
    private String name;
    private LocalDateTime createTime;
}
