package com.xcrm.service;

import com.xcrm.model.Venta;
import com.xcrm.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    public void save(Venta venta) {
        ventaRepository.save(venta);
    }
}