package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImagesController {

    private final ImageService imageService;
    private final AdService adService;

    @GetMapping(value = "/avatars")
    public ResponseEntity<InputStreamResource> getAvatarImage(Authentication authentication) throws IOException {
        File file = imageService.getAvatarFileByCurrentUser(authentication);
        return getResponseEntityForFile(file);
    }

    @GetMapping(value = "/avatars/{id}")
    public ResponseEntity<InputStreamResource> getAvatarImage(@PathVariable(name = "id") Integer userId) throws IOException {
        File file = imageService.getAvatarFileByUserId(userId);
        return getResponseEntityForFile(file);
    }

    @GetMapping(value = "/photo/{id}")
    public ResponseEntity<InputStreamResource> getAdPhoto(@PathVariable(name = "id") Integer adId) throws IOException {
        Ad ad = adService.getAdById(adId);
        File file = imageService.getPhotoByAd(ad);
        return getResponseEntityForFile(file);
    }

    private ResponseEntity<InputStreamResource> getResponseEntityForFile(File file) throws IOException {
        if (file != null && file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(Files.probeContentType(file.toPath())))
                    .contentLength(file.length())
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
