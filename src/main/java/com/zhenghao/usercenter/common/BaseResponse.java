package com.zhenghao.usercenter.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7888821748416341888L;
    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message ="";
    }
    public BaseResponse(int code, String message) {
        this.code = code;
        this.data = null;
        this.message =message;
    }
    public BaseResponse(ErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }
}
