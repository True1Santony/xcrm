package com.xcrm.service;

import com.xcrm.model.Interaccion;
import com.xcrm.repository.InteractionReposytory;
import org.springframework.stereotype.Service;

@Service
public class InteractionService {

    private final InteractionReposytory interactionReposytory;

    public InteractionService(InteractionReposytory interactionReposytory) {
        this.interactionReposytory = interactionReposytory;
    }

    public Interaccion save(Interaccion interaction) {
        return interactionReposytory.save(interaction);
    }
}