package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    public List<Authority> getAuthoritiesByUsername(String username) {
        return authorityRepository.findByUser_Username(username);
    }

    public void saveAuthority(Authority authority) {
        authorityRepository.save(authority);
    }


}
