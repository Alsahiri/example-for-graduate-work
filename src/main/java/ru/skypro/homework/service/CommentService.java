package ru.skypro.homework.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.model.Comment;

public interface CommentService {

    CommentsDTO getCommentsByAdId(int adId);

    CommentDTO addCommentToAd(Integer adId, CreateOrUpdateCommentDTO createCommentDTO, Authentication authentication);

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerComment(authentication, #commentId)")
    void deleteComment(int adId, int commentId, Authentication authentication);

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerComment(authentication, #commentId)")
    CommentDTO updateComment(int adId, int commentId, CreateOrUpdateCommentDTO updateCommentDTO, Authentication authentication);

    Comment getCommentById(Integer commentId);
}
