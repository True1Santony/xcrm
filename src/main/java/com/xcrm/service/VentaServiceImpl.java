package com.xcrm.service;

import com.xcrm.model.Venta;
import com.xcrm.repository.VentaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class VentaServiceImpl implements VentaService{
    private VentaRepository ventaRepository;

    @Override
    public void save(Venta venta) {
        ventaRepository.save(venta);
    }
}