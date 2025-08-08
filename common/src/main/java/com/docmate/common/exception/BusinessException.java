package com.docmate.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final String code;
    private final int status;
    
    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.status = 400;
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.status = 400;
    }
    
    public BusinessException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = "BUSINESS_ERROR";
        this.status = 400;
    }
    
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = 400;
    }
    
    public BusinessException(String code, String message, int status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }
}
