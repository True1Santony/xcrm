package com.xcrm.repository;

import com.xcrm.model.Authority;
import com.xcrm.model.AuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {
    List<Authority> findByUser_Username(String username);

    // Método para encontrar autoridades por tipo específico
    List<Authority> findByAuthority(String authority);


}
