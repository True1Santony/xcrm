package com.xcrm.service;

import com.xcrm.model.Campania;
import com.xcrm.model.Cliente;
import com.xcrm.repository.CampaniaRepository;
import com.xcrm.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CampaniaService {

    @Autowired
    private CampaniaRepository campaniaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener una campaña por su id
    public Optional<Campania> getCampaniaById(Long id) {
        return campaniaRepository.findById(id);
    }

    // Asignar una campaña a un cliente o comercial (mediante las tablas intermedias)
    @Transactional
    public void addClienteToCampania(Long clienteId, Long campaniaId) {
        Optional<Campania> campaniaOpt = campaniaRepository.findById(campaniaId);
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if (campaniaOpt.isPresent() && clienteOpt.isPresent()) {
            Campania campania = campaniaOpt.get();
            Cliente cliente = clienteOpt.get();
            // La relación entre cliente y campaña puede gestionarse a través de la tabla intermedia
            cliente.getCampanias().add(campania);
            clienteRepository.save(cliente); // Guardamos al cliente con la relación actualizada
        }
    }

    public List<Campania> getAll() {
       return campaniaRepository.findAll();
    }

    public void save(Campania campania) {
        campaniaRepository.save(campania);
    }
}
