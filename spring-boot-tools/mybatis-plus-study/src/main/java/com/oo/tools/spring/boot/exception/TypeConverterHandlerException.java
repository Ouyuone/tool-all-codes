package com.oo.tools.spring.boot.exception;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/12/09 09:20:38
 */
public class TypeConverterHandlerException extends RuntimeException{
    public final String message;
    
    public TypeConverterHandlerException(String message) {
        super(message);
        this.message = message;
    }
    
    public TypeConverterHandlerException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
