package com.kedong.ieduflswebcommon.entity;

import java.io.Serializable;

public enum ResponseCode implements Serializable {
    SUCCESS(0), FAIL(-1), READ_FAIL(1);
    private Integer code;

    ResponseCode(Integer code) {
        this.code = code;
    }
}
