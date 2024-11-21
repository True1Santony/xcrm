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

    // Asignar un comercial a un cliente
    @Transactional
    public void addComercialToCliente(Long clienteId, String username) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        Optional<User> userOpt = userRepository.findById(username);

        if (clienteOpt.isPresent() && userOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            User user = userOpt.get();
            cliente.getComerciales().add(user);
            clienteRepository.save(cliente); // Guardamos al cliente con la relación actualizada
            user.getClientes().add(cliente); // Agregamos el cliente al comercial
            userRepository.save(user); // Guardamos al comercial con la relación actualizada
        }
    }
}
