package com.chatapp.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = AuthServiceApplication.class)
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
