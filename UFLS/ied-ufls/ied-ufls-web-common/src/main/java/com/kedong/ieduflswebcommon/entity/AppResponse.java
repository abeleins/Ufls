package com.kedong.ieduflswebcommon.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "响应结果")
public class AppResponse<T> implements Serializable {
    private ResponseCode code;
    private String message;
    private T data;

    public AppResponse(T data) {
        this.code = ResponseCode.SUCCESS;
        this.message = "响应成功";
        this.data = data;
    }

    public AppResponse(ResponseCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public AppResponse(ResponseCode code, T data) {
        this.code = code;
        this.data = data;
    }

    public AppResponse(String message) {
        this.message = message;
    }

    public static <T> AppResponse<T> success() {
        return success(null);
    }

    public static <T> AppResponse<T> success(T data) {
        return new AppResponse<T>(ResponseCode.SUCCESS, data);
    }

    public static <T> AppResponse<T> fail(String message) {
        return new AppResponse<>(ResponseCode.FAIL, message);
    }

    public static <T> AppResponse<T> fail(ResponseCode code, String message) {
        return new AppResponse<>(code, message);
    }

}

