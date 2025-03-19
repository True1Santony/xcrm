package com.xcrm.service;

import com.xcrm.model.Cliente;
import com.xcrm.model.User;
import com.xcrm.repository.ClienteRepository;
import com.xcrm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    // Obtener un cliente por su id
    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }

}
