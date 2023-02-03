package com.example.jbmotos.api.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class StandardError {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String path;
}
