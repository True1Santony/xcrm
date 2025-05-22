package com.xcrm.service;

import com.xcrm.model.Authority;

import java.util.Set;

public interface AuthorityService {
    void save(Authority authority);
    void deleteAll(Set<Authority> authorities);
}
