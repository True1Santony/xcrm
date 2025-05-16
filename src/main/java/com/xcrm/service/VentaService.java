package com.xcrm.service;

import com.xcrm.model.Venta;
import com.xcrm.repository.VentaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class VentaService {

    private VentaRepository ventaRepository;

    public void save(Venta venta) {
        ventaRepository.save(venta);
    }
}