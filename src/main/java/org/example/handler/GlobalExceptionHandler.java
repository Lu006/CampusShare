package org.example.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public String exceptionHandler(Exception ex) {
//        log.error("异常信息：{}", ex.getMessage());
//        return Result.error(ex.getMessage());
        System.out.println(ex.toString());
        return "";
    }

//    @ExceptionHandler
//    public String exceptionHandler(SQLIntegrityConstraintViolationException ex) {
//        log.error("异常信息：{}", ex.getMessage());
//        String errorMsg = ex.getMessage();
//        if (errorMsg.contains("Duplicate entry")) {
//            String[] errorList = errorMsg.split(" ");
//            return Result.error(errorList[2] + MessageConstant.ACCOUNT_ALREADY_EXIST);
//        } else {
//            return Result.error(MessageConstant.UNKNOWN_ERROR);
//        }
//    }

}