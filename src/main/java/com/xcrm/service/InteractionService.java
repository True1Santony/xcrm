package com.xcrm.service;

import com.xcrm.model.Interaccion;
import com.xcrm.repository.InteractionReposytory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class InteractionService {

    private final InteractionReposytory interactionReposytory;

    public Interaccion save(Interaccion interaction) {
        return interactionReposytory.save(interaction);
    }
}