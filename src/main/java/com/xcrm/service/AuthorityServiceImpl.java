package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.repository.AuthorityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@AllArgsConstructor
@Service
public class AuthorityServiceImpl implements AuthorityService{

    private AuthorityRepository authorityRepository;

    public void save(Authority authority) {
        authorityRepository.save(authority);
    }

    public void deleteAll(Set<Authority> authorities) {
        authorityRepository.deleteAll();;
    }
}