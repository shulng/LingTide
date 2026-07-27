package com.shulng.controller;

import com.shulng.common.Result;
import com.shulng.dto.request.CreateCommentRequest;
import com.shulng.dto.response.CommentResponse;
import com.shulng.security.SecurityUtils;
import com.shulng.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final SecurityUtils securityUtils;

    public CommentController(CommentService commentService, SecurityUtils securityUtils) {
        this.commentService = commentService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public Result<CommentResponse> createComment(
            @RequestParam Long videoId,
            @Valid @RequestBody CreateCommentRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        CommentResponse response = commentService.createComment(videoId, userId, request);
        return Result.success("评论成功", response);
    }

    @GetMapping("/video/{videoId}")
    public Result<List<CommentResponse>> getVideoComments(@PathVariable Long videoId) {
        List<CommentResponse> comments = commentService.getVideoComments(videoId);
        return Result.success(comments);
    }

    @GetMapping("/video/{videoId}/count")
    public Result<Long> getCommentCount(@PathVariable Long videoId) {
        long count = commentService.getCommentCount(videoId);
        return Result.success(count);
    }
}
