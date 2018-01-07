package com.finance.security.api.service;

import com.finance.security.api.domain.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User update(Long id, User user);

    void delete(Long id);

    List<User> getAll();
}
