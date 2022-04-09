package com.a2v5lv.sockstorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SockResponseResult {  // data transfer object (прием/передача ответов)
    private Integer code;
    private String result;

    public SockResponseResult(Integer code, String result) {
        this.code = code;
        this.result = result;
    }

    public SockResponseResult() {
    }
}
