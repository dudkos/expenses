package com.finance.security.api.service;

import com.finance.common.exception.ServiceException;
import com.finance.security.api.domain.User;
import com.finance.security.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static com.finance.security.api.config.Constants.USER_ROLE;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User create(User user) {
        User existing = userRepository.findOneByLogin(user.getLogin());

        if(existing != null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "User " + user.getLogin() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(USER_ROLE);

        userRepository.save(user);

        logger.info("user {} created.", user.getLogin());

        return user;
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        User dbUser  = userRepository.findOne(id);

        if(dbUser == null) {
            throw new ServiceException(HttpStatus.NOT_FOUND.value(), "User with id " + id +" not found.");
        }

        dbUser.update(user);

        userRepository.save(dbUser);

        logger.info("user {} updated.", dbUser.getLogin());

        return dbUser;
    }


    @Override
    public void delete(Long id) {
        User user = userRepository.findOne(id);
        if(user == null) {
            throw new ServiceException(HttpStatus.NOT_FOUND.value(), "User with id " + id +" not found.");
        }
        userRepository.delete(user);

        logger.info("user {} deleted.", user.getLogin());
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }
}
