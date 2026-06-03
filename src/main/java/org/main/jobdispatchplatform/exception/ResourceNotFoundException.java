package org.main.jobdispatchplatform.exception;

/**
 * 资源不存在异常
 * 场景：查询订单、摄影师、点位不存在时抛出
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}
