package com.upload.app.core.exception;

public abstract class BaseException extends Exception {

    private String message;

    private Integer code;

    public BaseException(Integer code, String message) {

        this.message = message;
        this.code = code;

    }

    public String getMessage() {
        return message;
    }


    public Integer getCode() {
        return code;
    }


}
