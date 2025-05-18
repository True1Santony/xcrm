package com.xcrm.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditarFotoDTO {
    private UUID id;
    private Long organizacionId;
    private String fotoUrl;

    private String compania;
    private String nombre;
    private String correo;
    private String plan;
}