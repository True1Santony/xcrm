package com.xcrm.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EditarFotoDTO {
    private UUID id;
    private Long organizacionId;
    private String fotoUrl;
}