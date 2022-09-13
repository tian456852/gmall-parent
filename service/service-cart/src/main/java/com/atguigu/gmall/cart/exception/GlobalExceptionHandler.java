package com.atguigu.gmall.cart.exception;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author tkwrite
 * @create 2022-09-10-22:32
 */
@EnableAutoExceptionHandler
// @RestControllerAdvice
// @ControllerAdvice//告诉springboot 这是所有controller的统一切面
//收到所有controller异常
public class GlobalExceptionHandler {


    /**
     * 业务期间出现的所有异常都用 GmallException 包装
     * @param exception
     * @return
     */

    @ExceptionHandler(GmallException.class)
    public Result handleGmallException(GmallException exception){
        //业务状态的枚举类
        ResultCodeEnum codeEnum = exception.getCodeEnum();
        Result<String> result = Result.build("", codeEnum);
        return result;  //给前端的返回
    }

    @ExceptionHandler(NullPointerException.class)
    public String handlenullException(NullPointerException gmallException){

        return "haha";  //给前端的返回
    }
}
