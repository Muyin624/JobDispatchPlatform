package org.main.jobdispatchplatform.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * 老开发经验：自定义异常让代码更清晰，方便定位问题
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}
