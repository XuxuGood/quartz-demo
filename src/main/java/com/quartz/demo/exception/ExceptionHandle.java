package com.quartz.demo.exception;

import com.quartz.demo.common.ErrorCode;
import com.quartz.demo.common.Result;
import com.quartz.demo.common.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * 自定义异常捕获类
 *
 * @author xiaoxuxuy
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public <T> Result<T> exceptionGet(Exception e) {

        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            // 返回异常信息
            return ResultGenerator.genFailResult(Integer.parseInt(String.valueOf(exception.getCode())),
                    exception.getMessage());
        }

        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;

            log.error("【@Valid异常捕获】：{}", e.getMessage());

            // 返回异常信息
            return ResultGenerator.genFailResult(-1,
                    Objects.requireNonNull(validException.getBindingResult().getFieldError()).getDefaultMessage());
        }

        log.error("【系统异常】", e);
        return ResultGenerator.genFailResult(ErrorCode.SYS_ERROR);

    }
}

