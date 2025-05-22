package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.repository.AuthorityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class AuthorityServiceImplTest {

    private AuthorityRepository authorityRepository;
    private AuthorityServiceImpl authorityService;

    @BeforeEach
    void setUp() {
        authorityRepository = mock(AuthorityRepository.class);
        authorityService = new AuthorityServiceImpl(authorityRepository);
    }

    @Test
    void testSave_callsRepositorySave() {
        Authority authority = new Authority(UUID.randomUUID(), null, "ROLE_USER");

        authorityService.save(authority);

        verify(authorityRepository, times(1)).save(authority);
    }

    @Test
    void testDeleteAll_callsRepositoryDeleteAll() {
        Set<Authority> authorities = Set.of(
                new Authority(UUID.randomUUID(), null, "ROLE_ADMIN"),
                new Authority(UUID.randomUUID(), null, "ROLE_USER")
        );

        authorityService.deleteAll(authorities);

        // Capturar que se llama deleteAll sin argumentos
        verify(authorityRepository, times(1)).deleteAll();
    }
}
