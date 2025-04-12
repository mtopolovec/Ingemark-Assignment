package com.ingemark.assignment.assignment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseMsg {
    private Integer status;
    private String error;
    private String message;
    private String timestamp;
    private List<String> details;

    public ApiResponseMsg(Integer status, String error, String message, List<String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss"));
        this.details = details;
    }

    public ApiResponseMsg(Integer status, String message, List<String> details) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss"));
        this.details = details;
    }
}
