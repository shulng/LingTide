package com.shulng.service;

import com.shulng.common.BusinessException;
import com.shulng.dto.request.CreateCommentRequest;
import com.shulng.dto.response.CommentResponse;
import com.shulng.entity.Comment;
import com.shulng.entity.Video;
import com.shulng.entity.User;
import com.shulng.repository.CommentRepository;
import com.shulng.repository.VideoRepository;
import com.shulng.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, 
                          VideoRepository videoRepository, 
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse createComment(Long videoId, Long userId, CreateCommentRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new BusinessException(404, "视频不存在"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setVideoId(videoId);
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setLikes(0);
        comment.setStatus(1);

        Comment saved = commentRepository.save(comment);

        video.setComments(video.getComments() + 1);
        videoRepository.save(video);

        return convertToResponse(saved);
    }

    public List<CommentResponse> getVideoComments(Long videoId) {
        List<Comment> comments = commentRepository.findByVideoIdAndStatus(videoId, 1);
        
        Map<Long, List<Comment>> byParent = comments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<CommentResponse> rootComments = comments.stream()
                .filter(c -> c.getParentId() == null)
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        for (CommentResponse root : rootComments) {
            List<Comment> children = byParent.getOrDefault(root.getId(), new ArrayList<>());
            List<CommentResponse> childResponses = children.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            root.setChildren(childResponses);
        }

        return rootComments;
    }

    public long getCommentCount(Long videoId) {
        return commentRepository.countByVideoIdAndStatus(videoId, 1);
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = CommentResponse.fromEntity(comment);
        
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            response.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
            response.setUserAvatar(user.getAvatar());
        });

        return response;
    }
}
