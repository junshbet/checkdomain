package com.junt.checkdomain;

import com.junt.checkdomain.model.Role;
import com.junt.checkdomain.model.User;
import com.junt.checkdomain.repository.UserRepository;
import com.junt.checkdomain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("123");
            user.setEmail("admin@gmail.com");
            user.setFullname("Administrator");
            user.setRole(Role.ADMIN);
            userService.saveUser(user);
            User user2 = new User();
            user2.setUsername("user");
            user2.setPassword("123");
            user2.setEmail("juntshbet@gmail.com");
            user2.setFullname("User");
            user2.setRole(Role.USER);
            userService.saveUser(user2);
        }
    }
}
