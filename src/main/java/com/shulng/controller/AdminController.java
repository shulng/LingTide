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

    @PutMapping("/users/{id}/status")
    public Result<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        UserResponse response = adminService.updateUserStatus(id, status);
        return Result.success("更新成功", response);
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

    @PostMapping("/categories")
    public Result<CategoryResponse> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        CategoryResponse response = adminService.createCategory(name, description);
        return Result.success("创建成功", response);
    }
}
