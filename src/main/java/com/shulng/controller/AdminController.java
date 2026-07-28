package com.shulng.controller;

import com.shulng.common.PageResult;
import com.shulng.common.Result;
import com.shulng.dto.request.PageQueryRequest;
import com.shulng.dto.response.CategoryResponse;
import com.shulng.dto.response.StatsResponse;
import com.shulng.dto.response.UserResponse;
import com.shulng.dto.response.VideoResponse;
import com.shulng.security.SecurityUtils;
import com.shulng.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final SecurityUtils securityUtils;

    public AdminController(AdminService adminService, SecurityUtils securityUtils) {
        this.adminService = adminService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/stats")
    public Result<StatsResponse> getStats() {
        StatsResponse stats = new StatsResponse(
                adminService.getUserCount(),
                adminService.getVideoCount(),
                adminService.getPublishedVideoCount()
        );
        return Result.success(stats);
    }

    @GetMapping("/users")
    public Result<PageResult<UserResponse>> getAllUsers(PageQueryRequest request) {
        PageResult<UserResponse> response = adminService.getAllUsers(request);
        return Result.success(response);
    }

    @PostMapping("/users")
    public Result<UserResponse> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nickname,
            @RequestParam(defaultValue = "USER") String role) {
        UserResponse response = adminService.createUser(username, password, email, nickname, role);
        return Result.success("创建成功", response);
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/users/{id}/status")
    public Result<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        UserResponse response = adminService.updateUserStatus(id, status);
        return Result.success("更新成功", response);
    }

    @PutMapping("/users/{id}/password")
    public Result<UserResponse> updateUserPassword(
            @PathVariable Long id,
            @RequestParam String password) {
        UserResponse response = adminService.updateUserPassword(id, password);
        return Result.success("密码修改成功", response);
    }

    @PutMapping("/users/{id}/role")
    public Result<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        UserResponse response = adminService.updateUserRole(id, role);
        return Result.success("角色修改成功", response);
    }

    @GetMapping("/videos")
    public Result<PageResult<VideoResponse>> getAllVideos(PageQueryRequest request) {
        PageResult<VideoResponse> response = adminService.getAllVideos(request);
        return Result.success(response);
    }

    @PutMapping("/videos/{id}/status")
    public Result<VideoResponse> updateVideoStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        VideoResponse response = adminService.updateVideoStatus(id, status);
        return Result.success("更新成功", response);
    }

    @GetMapping("/categories")
    public Result<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> response = adminService.getAllCategories();
        return Result.success(response);
    }

    @PostMapping("/categories")
    public Result<CategoryResponse> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        CategoryResponse response = adminService.createCategory(name, description);
        return Result.success("创建成功", response);
    }

    @PutMapping("/categories/{id}")
    public Result<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Integer status) {
        CategoryResponse response = adminService.updateCategory(id, name, description, sortOrder, status);
        return Result.success("更新成功", response);
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
