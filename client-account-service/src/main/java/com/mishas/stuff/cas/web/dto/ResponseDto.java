package com.mishas.stuff.cas.web.dto;

import java.util.Map;

public class ResponseDto implements IDto {

    private int code;
    private String status;
    private String message;
    private Map<String, Object> data;

    public ResponseDto() {
        super();
    }

    public ResponseDto(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public ResponseDto(int code, String status, Map<String, Object> data) {
        this.code = code;
        this.status = status;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
