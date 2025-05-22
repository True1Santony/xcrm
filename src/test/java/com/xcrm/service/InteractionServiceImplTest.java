package com.xcrm.service;

import com.xcrm.model.Interaccion;
import com.xcrm.repository.InteractionReposytory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class InteractionServiceImplTest {
    @Mock
    private InteractionReposytory interactionReposytory;

    @InjectMocks
    private InteractionServiceImpl interactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldReturnSavedInteraction() {
        // Given
        Interaccion interaction = new Interaccion();
        interaction.setTipo("llamada");
        interaction.setEstado("realizada");
        interaction.setFechaHora(LocalDateTime.now());
        interaction.setNotas("Cliente interesado");

        when(interactionReposytory.save(interaction)).thenReturn(interaction);

        // When
        Interaccion result = interactionService.save(interaction);

        // Then
        assertEquals(interaction, result);
        verify(interactionReposytory, times(1)).save(interaction);
    }
}
