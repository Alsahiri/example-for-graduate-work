package ru.skypro.homework.service.impl;

import org.springframework.security.access.prepost.PreAuthorize;
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
    /**
     * Возвращает список комментариев для объявления с указанным идентификатором adId
     * @param adId идентификатор объявления
     * @return список комментариев объявления в виде объекта класса CommentsDTO
     */
    @Override
    public CommentsDTO getCommentsByAdId(int adId) {
        List<Comment> comments = commentRepository.findCommentsByAd_AdId(adId);
        return commentMapper.toCommentsDTO(comments);
    }

    /**
     * Добавляет комментарий от имени текущего авторизованного пользователя для объявления с идентификатором adId.
     * @param adId идентификатор объявления
     * @param createCommentDTO объект, содержащий поля для создания комментария
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     * @return возвращает объект класса CommentDTO, содержащий полную информацию о новом комментарии
     */
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

    /**
     * Удаляет комментарий с идентификатором commentId для объявления с идентификатором adId.
     * Дополнительно проводится проверка в соответствии с переданным параметром authentication и ролевой моделью.
     * Права на удаление комментария есть только у его создателя и администраторов (тех, кто обладает ролью ADMIN).
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerComment(authentication, #commentId)")
    public void deleteComment(int adId, int commentId, Authentication authentication) {
        Comment comment = checkCommentPresent(adId, commentId);
        commentRepository.delete(comment);
    }

    /**
     * Обновляет комментарий с идентификатором commentId для объявления с идентификатором adId в соответствии
     * с переданным параметром updateCommentDTO (текст комментария)
     * Дополнительно проводится проверка в соответствии с переданным параметром authentication и ролевой моделью.
     * Права на удаление комментария есть только у его создателя и администраторов (тех, кто обладает ролью ADMIN).
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerComment(authentication, #commentId)")
    public CommentDTO updateComment(int adId, int commentId, CreateOrUpdateCommentDTO updateCommentDTO, Authentication authentication) {
        Comment comment = checkCommentPresent(adId, commentId);
        comment.setCommentText(updateCommentDTO.getText());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDTO(comment);
    }

    /**
     * Возвращает комментарий по его уникальному идентификатору из БД
     * @param commentId идентификатор комментария
     * @return Комментарий в виде объекта класса Comment
     * @throws CommentNotFoundException - если комментарий с указанным идентификатором не найден в БД
     */
    @Override
    public Comment getCommentById(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException(String.format("Комментарий с id: %d не найден в БД", commentId)));
    }

    private Comment checkCommentPresent(int adId, int commentId) {
        Comment comment = getCommentById(commentId);
        if (!adService.isAdPresent(adId) || comment.getAd().getAdId() != adId) {
            throw new CommentNotFoundException(String.format("Комментарий с id: %d не найден в БД для объявления с id: %d", commentId, adId));
        }
        return comment;
    }

}
