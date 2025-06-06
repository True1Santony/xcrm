package com.xcrm.DTO;

import com.xcrm.model.Organization;
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

    // Identificador único del usuario (clave para operaciones seguras)
    private UUID id;

    // URL de la foto de perfil actual o nueva del usuario
    private String fotoUrl;

    // Nombre de la compañía a la que pertenece el usuario
    private String compania;

    // Nombre visible del usuario
    private String nombre;

    // Objeto completo de la organización, necesario para acceder a su email y plan
    private Organization organizacion;
}