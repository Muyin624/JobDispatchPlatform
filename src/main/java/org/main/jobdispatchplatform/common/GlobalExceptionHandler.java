package org.main.jobdispatchplatform.common;

import org.main.jobdispatchplatform.exception.BusinessException;
import org.main.jobdispatchplatform.exception.ResourceNotFoundException;
import org.main.jobdispatchplatform.exception.OrderAssignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 老开发经验：分类处理异常，记录日志，不要暴露敏感信息给前端
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    // 资源不存在
    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // 订单分配异常
    @ExceptionHandler(OrderAssignException.class)
    public Result<String> handleOrderAssignException(OrderAssignException e) {
        log.warn("订单分配失败: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // 业务异常（自定义）
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // 通用运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        // 生产环境不要返回详细错误信息
        return Result.error(500, "系统繁忙，请稍后重试");
    }

    // 兜底
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("未知异常", e);
        return Result.error(500, "服务器内部错误");
    }
}
