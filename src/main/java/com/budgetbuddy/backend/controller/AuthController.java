package com.budgetbuddy.backend.controller;

import com.budgetbuddy.backend.entity.User;
import com.budgetbuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://shivani-pawar.onrender.com"
})

public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
public String register(@RequestBody User user) {

    Optional<User> existing = userRepository.findByEmail(user.getEmail());

    if (existing.isPresent()) {
        return "Email already exists";
    }

    userRepository.save(user);

    return "User Registered Successfully";
}
 @PostMapping("/login")
public Object login(@RequestBody User user) {

    try {

        Optional<User> existingUser =
                userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {

            User dbUser = existingUser.get();

            if (dbUser.getPassword().equals(user.getPassword())) {

                dbUser.setPassword(null);

                return dbUser;
            }
        }

        return "Invalid Email or Password";

    } catch (Exception e) {

        e.printStackTrace();

        return e.getMessage();
    }
}

}
