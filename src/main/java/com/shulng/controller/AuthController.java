package com.shulng.controller;

import com.shulng.common.Result;
import com.shulng.dto.request.LoginRequest;
import com.shulng.dto.request.RegisterRequest;
import com.shulng.dto.response.LoginResponse;
import com.shulng.dto.response.UserResponse;
import com.shulng.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return Result.success("注册成功", response);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }
}
