package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.service.CommentService;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class CommentsController {

    private final CommentService commentService;


    @GetMapping("/ads/{id}/comments")
    public ResponseEntity<CommentsDTO> getComments(@PathVariable(name = "adId") int adId) {
        return ResponseEntity.ok(commentService.getCommentsByAdId(adId));
    }

    @PostMapping("/ads/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(@PathVariable(name = "adId") int adId,
                                                 @RequestBody CreateOrUpdateCommentDTO createCommentDTO,
                                                 Authentication authentication) {
        return ResponseEntity.ok(commentService.addCommentToAd(adId, createCommentDTO, authentication));
    }

    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "adId") int adId,
                                              @PathVariable(name = "commentId") int commentId,
                                              Authentication authentication)
    {
        commentService.deleteComment(adId, commentId, authentication);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable(name = "adId") int adId,
                                                    @PathVariable(name = "commentId") int commentId,
                                                    @RequestBody CreateOrUpdateCommentDTO updateCommentDTO,
                                                    Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId, updateCommentDTO, authentication));
    }
}
