package com.xcrm.repository;

import com.xcrm.model.Interaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionReposytory extends JpaRepository<Interaccion, Long> {
}
