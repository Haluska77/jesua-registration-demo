package com.jesua.registration.repository;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    User user1;
    User user2;
    PasswordToken passwordToken;

    @BeforeAll
    public void setUp(){
        userRepository.deleteAll();

        UserDto userDto1 = buildUserDto();
        user1 = buildUserFromDto(userDto1);
        userRepository.save(user1);

        passwordToken = new PasswordToken();
        passwordToken.setToken("newToken555555");
        passwordToken.setApplied(false);
        passwordToken.setExpiration(Instant.now().plusSeconds(100));
        passwordToken.setUser(user1);
        passwordTokenRepository.save(passwordToken);

        UserDto userDto2 = buildUserDto();
        userDto2.setEmail("another@user.com");
        userDto2.setActive(false);
        user2 = buildUserFromDto(userDto2);
        userRepository.save(user2);
    }

    @AfterAll
    public void tearDown(){
        passwordTokenRepository.delete(passwordToken);
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @Test
    void findByEmailAndActiveTrueSuccessTest() {

        User user = userRepository.findByEmailAndActiveTrue(user1.getEmail()).orElse(null);

        assertNotNull(user);
        assertEquals(user1.getEmail(), user.getEmail());

    }

    @Test
    void findByEmailAndActiveTrueFailTest() {

        User user = userRepository.findByEmailAndActiveTrue(user2.getEmail()).orElse(null);

        assertNull(user);

    }

    @Test
    void existsByEmailSuccessTest() {

        Boolean exists = userRepository.existsByEmail(user1.getEmail());

        assertTrue(exists);
    }

    @Test
    void existsByEmailFailedTest() {

        Boolean exists = userRepository.existsByEmail("none@existing.com");

        assertFalse(exists);
    }

    @Test
    void findAllTest() {

        List<User> all = userRepository.findAll();

        assertEquals(2, all.size());

    }

    @Test
    void findByPasswordTokensTest() {

        User userWithToken = userRepository.findByPasswordTokens_Token(passwordToken.getToken()).orElse(null);

        assertNotNull(userWithToken);
        assertEquals(user1.getId(), userWithToken.getId());
    }
}