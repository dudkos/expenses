package com.finance.security.api;

import com.finance.security.api.domain.User;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public abstract class AbstractSecurityApiTest implements TestConstants {

    public User user() {
        User user = new User();
        user.setLogin(TEST_LOGIN);
        user.setPassword(TEST_PASSWORD);
        return user;
    }
}
