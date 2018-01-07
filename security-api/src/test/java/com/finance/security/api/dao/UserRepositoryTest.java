package com.finance.security.api.dao;

import com.finance.security.api.SecurityApiApplicationTests;
import com.finance.security.api.domain.User;
import com.finance.security.api.repository.UserRepository;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.Assert.*;

public class UserRepositoryTest {//extends SecurityApiApplicationTests {

//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Before
//    public void setup() {
//        User user = user();
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//    }
//
//    @After
//    public void clean() {
//        userRepository.delete(userRepository.findOneByLogin(TEST_LOGIN));
//        assertNull(userRepository.findOneByLogin(TEST_LOGIN));
//    }
//
//    @Test
//    public void findOneByLogin() {
//        User found = userRepository.findOneByLogin(TEST_LOGIN);
//        assertEquals(found.getLogin(), user().getLogin());
//        assertTrue(passwordEncoder.matches(user().getPassword(), found.getPassword()));
//    }
//
//    @Test(expected = javax.validation.ConstraintViolationException.class)
//    public void createUserNullLogin() {
//        User user = user();
//        user.setLogin(null);
//        userRepository.save(user);
//    }
//
//    @Test(expected = javax.validation.ConstraintViolationException.class)
//    public void createUserNullPassword() {
//        User user = user();
//        user.setPassword(null);
//        userRepository.save(user);
//    }
//
//    @Test(expected = javax.validation.ConstraintViolationException.class)
//    public void createUserWithEmptyLogin() {
//        User user = user();
//        user.setLogin("");
//        userRepository.save(user);
//    }
//
//    @Test(expected = javax.validation.ConstraintViolationException.class)
//    public void createUserWithEmptyPassword() {
//        User user = user();
//        user.setPassword("");
//        userRepository.save(user);
//    }
//
//    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
//    public void createDuplicationUser() {
//        userRepository.save(user());
//    }
//
//    @Test
//    public void getAll() {
//        List<User> users = userRepository.getAll();
//        assertFalse(users.isEmpty());
//    }
}
