package ru.skypro.homework.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.Ad;

import java.io.File;
import java.io.IOException;

public interface ImageService {
    void updateCurrentUserAvatar(Authentication authentication, MultipartFile file) throws IOException;

    File getAvatarFileByCurrentUser(Authentication authentication);

    File getAvatarFileByUserId(Integer userId);

    File getPhotoByAd(Ad ad);

    File getFileByPathAndImageName(String dirPath, String imageName);

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerAd(authentication, #ad.adId)")
    void updateAdPhoto(Ad ad, MultipartFile file, Authentication authentication) throws IOException;

    String saveFileToDisk(MultipartFile file, String dirPath, String fileName) throws IOException;

    void deleteAdPhoto(String adPhotoName) throws IOException;
}
