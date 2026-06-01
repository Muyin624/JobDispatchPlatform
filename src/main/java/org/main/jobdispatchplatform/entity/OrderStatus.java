package org.main.jobdispatchplatform.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED(0,"待分配"),
    ASSIGNED(1,"已分配"),
    COMPLETED(2,"已完成"),
    CANCELLED(3,"已取消");

    private final int code;
    private final String desc;
    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatus fromCode(int code){
        for (OrderStatus status : OrderStatus.values()){
            if (status.getCode()==code){
                return status;
            }
        }
        throw new IllegalArgumentException("非法的状态code"+code);
    }
}
