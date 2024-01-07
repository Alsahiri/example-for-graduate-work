package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public CommentDTO toCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setPk(comment.getCommentId());
        commentDTO.setAuthor(comment.getAuthor().getUserId());
        commentDTO.setAuthorFirstName(comment.getAuthor().getFirstName());
        commentDTO.setAuthorImage(comment.getAuthor().getAvatarFilePath());
        commentDTO.setCreatedAt(comment.getCreatedAt().atZone(ZoneId.systemDefault()).toEpochSecond());

        return commentDTO;
    }

    public Comment fromCommentDTO(CommentDTO commentDTO) {
        Comment comment = new Comment();

        comment.setCommentId(commentDTO.getPk());
        comment.setCreatedAt(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(commentDTO.getCreatedAt()), TimeZone.getDefault().toZoneId()));
        return comment;
    }

    public CommentsDTO toCommentsDTO(List<Comment> comments) {
        CommentsDTO commentsDTO = new CommentsDTO();
        commentsDTO.setCount(comments.size());
        commentsDTO.setResults(comments.stream().map(this::toCommentDTO).collect(Collectors.toList()));

        return commentsDTO;
    }
}
