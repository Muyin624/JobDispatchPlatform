package org.main.jobdispatchplatform.exception;

/**
 * 订单分配异常
 * 场景：订单状态不对、摄影师不匹配、时间冲突等
 */
public class OrderAssignException extends BusinessException {
    public OrderAssignException(String message) {
        super(400, message);
    }
}
