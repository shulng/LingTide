package com.shulng.controller;

import com.shulng.common.Result;
import com.shulng.dto.request.UpdateUserRequest;
import com.shulng.dto.response.UserResponse;
import com.shulng.security.SecurityUtils;
import com.shulng.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/{id}")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return Result.success(response);
    }

    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser() {
        Long userId = securityUtils.getCurrentUserId();
        UserResponse response = userService.getUserById(userId);
        return Result.success(response);
    }

    @PutMapping("/me")
    public Result<UserResponse> updateCurrentUser(@RequestBody UpdateUserRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        UserResponse response = userService.updateUser(userId, request);
        return Result.success("更新成功", response);
    }
}
