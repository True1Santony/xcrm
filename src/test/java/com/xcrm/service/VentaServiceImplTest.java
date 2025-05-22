package com.xcrm.service;

import com.xcrm.model.Venta;
import com.xcrm.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VentaServiceImplTest {
    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldCallRepositorySave() {
        // Given
        Venta venta = new Venta();
        venta.setProducto("Seguro");
        venta.setMonto(BigDecimal.valueOf(1200.00));
        venta.setFecha(LocalDate.now());

        // When
        ventaService.save(venta);

        // Then
        verify(ventaRepository, times(1)).save(venta);
    }
}
