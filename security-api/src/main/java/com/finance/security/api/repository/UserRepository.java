package com.finance.security.api.repository;

import com.finance.security.api.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select u from User u where u.login = ?1")
    User findOneByLogin(String login);

    @Query("SELECT u FROM User u")
    List<User> getAll();
}
