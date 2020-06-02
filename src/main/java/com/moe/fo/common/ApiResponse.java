package com.moe.fo.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

@JsonSerialize
@Data
@Builder
public class ApiResponse<T> {
    private int status;
    private String msg;
    private T data;
}
