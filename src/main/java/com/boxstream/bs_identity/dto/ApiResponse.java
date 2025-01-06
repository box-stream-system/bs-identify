package com.boxstream.bs_identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // for auto remove null response filed
public class ApiResponse <T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse() {
        this.code = code;
        this.message = message;
    }
}


