package com.xcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReportResponseDto {
    private final byte[] content;
    private final String contentType;
    private final String fileName;
}