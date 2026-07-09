package nus.iss.wellness.backend.repository;

import nus.iss.wellness.backend.model.Role;
import nus.iss.wellness.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//author: Junior

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Test
    void shouldSaveUser() {

        User user = new User();
        user.setUsername("Junior");
        user.setEmail("junior@test.com");
        user.setPasswordHash("Junior");
        user.setRole(Role.USER);

        User saved = repository.save(user);

        assertNotNull(saved.getUserId());

        Optional<User> found =
                repository.findByUsername("Junior");

        assertTrue(found.isPresent());
        assertEquals("junior@test.com", found.get().getEmail());
    }
}