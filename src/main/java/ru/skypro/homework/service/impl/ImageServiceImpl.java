package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${path.to.user.avatar.file.disk}")
    private String avatarFilePath;

    @Value("${path.to.ad.photo.file.disk}")
    private String adPhotoFilePath;

    private final UserService userService;
    private final UserRepository userRepository;
    private final AdRepository adRepository;

    public ImageServiceImpl(UserService userService, UserRepository userRepository, AdRepository adRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.adRepository = adRepository;
    }

    /**
     * Обновляет фото профиля (аватар) текущего авторизованного пользователя из параметра file.
     * Производит сохранение информации в БД и нового фото на жесткий диск
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     * @param file графический файл с фото профиля (аватаром) пользователя
     * @throws IOException - если не удалось сохранить на диске файл с фото профиля (аватар) пользователя
     */
    @Override
    public void updateCurrentUserAvatar(Authentication authentication, MultipartFile file) throws IOException {
        User user = userService.getCurrentUser(authentication);
        String avatarFileName = saveFileToDisk(file, avatarFilePath, "avatar_" + user.getUserId());
        user.setAvatarFilePath(avatarFileName);
        userRepository.save(user);
    }

    /**
     * Возвращает фото профиля (аватар) текущего авторизованного пользователя в виде объекта класса File
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     * @return объект класса File
     */
    @Override
    public File getAvatarFileByCurrentUser(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        return getFileByPathAndImageName(this.avatarFilePath, user.getAvatarFilePath());
    }

    /**
     * Возвращает фото профиля (аватар) для пользователя с идентификатором userId в виде объекта класса File
     * @param userId идентификатор пользователя
     * @return объект класса File
     */
    @Override
    public File getAvatarFileByUserId(Integer userId) {
        User user = userService.getUserById(userId);
        return getFileByPathAndImageName(this.avatarFilePath, user.getAvatarFilePath());
    }

    /**
     * Возвращает картинку объявления в виде объекта класса File
     * @param ad объявление в виде объекта класса Ad
     * @return объект класса File
     */
    @Override
    public File getPhotoByAd(Ad ad) {
        String photoName = ad.getImage();
        return getFileByPathAndImageName(this.adPhotoFilePath, photoName);
    }

    /**
     * Конструирует полный путь из переданных параметров и возвращает объект класса File
     * @param dirPath каталог на диске, в котором находятся графические файлы
     * @param imageName имя файла
     * @return объект класса File
     */
    @Override
    public File getFileByPathAndImageName(String dirPath, String imageName) {
        if (imageName == null) {
            return null;
        }
        Path imagePath = Path.of(dirPath, imageName);
        return imagePath.toFile();
    }

    /**
     * Обновляет фото в объявлении, производит сохранение информации в БД и нового фото на жесткий диск
     * @param ad объявление, объект класса Ad
     * @param file графический файл с картинкой (фото) объявления
     * @throws IOException - если не удалось сохранить на диске файл с фото объявления
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerAd(authentication, #ad.adId)")
    public void updateAdPhoto(Ad ad, MultipartFile file, Authentication authentication) throws IOException {
        String photoFileName = saveFileToDisk(file, adPhotoFilePath, "photo_" + ad.getAdId());
        ad.setImage(photoFileName);
        adRepository.save(ad);
    }

    /**
     * Сохраняет на диск графический файл с картинкой, указанный в параметре file.
     * Сохранение производится в каталог dirPath под именем, указанном в параметре fileName
     * @param file графический файл с картинкой
     * @param dirPath путь к каталогу на диске для сохранения файла
     * @param fileName имя файла, под которым он будет записан на диск
     * @return имя файла, под которым файл был сохранен на диск
     * @throws IOException если не удалось сохранить на диске файл
     */
    @Override
    public String saveFileToDisk(MultipartFile file, String dirPath, String fileName) throws IOException {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        Path filePath = Path.of(dirPath, fileName + "." + fileExtension);
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return filePath.getFileName().toString();
    }

    /**
     * Удаляет файл с фотографией с именем adPhotoName с жесткого диска
     * @param adPhotoName имя файла с фотографией объявления
     * @throws IOException если не удалось удалить файл
     */
    @Override
    public void deleteAdPhoto(String adPhotoName) throws IOException {
        File adPhotoFile = getFileByPathAndImageName(adPhotoFilePath, adPhotoName);
        FileUtils.delete(adPhotoFile);
    }
}
