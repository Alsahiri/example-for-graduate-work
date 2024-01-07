package ru.skypro.homework.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdService adService;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, AdService adService, UserService userService, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.adService = adService;
        this.userService = userService;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentsDTO getCommentsByAdId(Integer adId) {
        List<Comment> comments = commentRepository.findCommentsByAd_AdId(adId);
        if (comments.isEmpty()) {
            throw new CommentNotFoundException(String.format("Комментарии для объявления с id: %d не найдены в БД", adId));
        }
        return commentMapper.toCommentsDTO(comments);
    }

    @Override
    public CommentDTO addCommentToAd(Integer adId, CreateOrUpdateCommentDTO createCommentDTO, Authentication authentication) {
        Ad ad = adService.getAdById(adId);
        User author = userService.getCurrentUser(authentication);

        Comment comment = new Comment();
        comment.setCommentText(createCommentDTO.getText());
        comment.setAd(ad);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        return commentMapper.toCommentDTO(comment);
    }

    @Override
    public void deleteComment(Integer adId, Integer commentId) {
        Ad ad = adService.getAdById(adId);
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public CommentDTO updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDTO updateCommentDTO) {
        Ad ad = adService.getAdById(adId);
        Comment comment = getCommentById(commentId);
        comment.setCommentText(updateCommentDTO.getText());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDTO(comment);
    }

    private Comment getCommentById(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException(String.format("Комментарий с id: %d не найден в БД", commentId)));
    }

}
