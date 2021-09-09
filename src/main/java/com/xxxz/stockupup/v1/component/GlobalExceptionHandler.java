package com.xxxz.stockupup.v1.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @autor jiangll
 * @date 2021/9/9
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public Map exceptionHandler(Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        Map<String, String> result = new HashMap<>(3);
        result.put("data", "后端服务异常，请联系管理员");
        result.put("code", "500");
        result.put("errorInfo", ex.getMessage());
        return result;
    }
}
