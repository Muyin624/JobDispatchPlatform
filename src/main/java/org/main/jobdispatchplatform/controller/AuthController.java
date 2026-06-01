package org.main.jobdispatchplatform.controller;

import jakarta.validation.Valid;
import org.main.jobdispatchplatform.common.LoginRequest;
import org.main.jobdispatchplatform.common.Result;
import org.main.jobdispatchplatform.entity.User;
import org.main.jobdispatchplatform.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody User user) {
        userService.register(user);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest login) {
        String token = userService.login(login.getPhone(), login.getPassword());
        return Result.success(Map.of("token", token));
    }
}
