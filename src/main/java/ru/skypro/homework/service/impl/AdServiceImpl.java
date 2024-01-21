package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.util.List;
@Service
public class AdServiceImpl implements AdService {

    @Value("${path.to.ad.photo.file.disk}")
    private String adPhotoFilePath;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final ImageService imageService;

    public AdServiceImpl(AdRepository adRepository, UserRepository userRepository, AdMapper adMapper, ImageService imageService) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.adMapper = adMapper;
        this.imageService = imageService;
    }

    /**
     * Возвращает список всех объявлений из БД
     * @return список объявлений в виде объекта AdsDTO
     */
    @Override
    public AdsDTO getAllAds() {
        List<Ad> adList = adRepository.findAll();
        return adMapper.toAdsDTO(adList);
    }


    /**
     * Возвращает объявление из БД по его уникальному идентификатору
     * @param adId идентификатор объявления
     * @return Объявление в виде объекта класса Ad
     * @throws AdNotFoundException - если объявление не найдено в БД по указанному идентфикатру
     */
    @Override
    public Ad getAdById(Integer adId) {
        return adRepository.findById(adId).orElseThrow(() -> new AdNotFoundException(String.format("Объявление с id: %d не найдено в БД", adId)));
    }

    /**
     * Удаляет объявление с указанным идентификатором adId.
     * Дополнительно проводится проверка в соответствии с переданным параметром authentication и ролевой моделью.
     * Права на удаление объявления есть только у его создателя и администраторов (тех, кто обладает ролью ADMIN).
     * @param adId идентификатор объявления
     * @param authentication текущий авторизованный пользователь, предоставляет фронтенд
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerAd(authentication, #adId)")
    public void removeAd(Integer adId, Authentication authentication) throws IOException {
        Ad ad = getAdById(adId);
        adRepository.delete(ad);
        imageService.deleteAdPhoto(ad.getImage());
    }

    /**
     * Обновляет существующее объявление в соответствии с заданными параметрами.
     * Дополнительно проводится проверка в соответствии  с переданным параметром authentication и ролевой моделью.
     * Права на обновление объявления есть только у его создателя и администраторов (тех, кто обладает ролью ADMIN).
     * @param adId идентификатор объявления
     * @param updateAdDTO объект, содержащий параметры объявления, которые можно обновлять, предоставляет фронтенд
     * @param authentication текущий авторизованный пользователь, предоставляет фронтенд
     * @return Возвращает объект класса AdDTO в случае успешного обновления объявления и сохранения его в БД
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerAd(authentication, #adId)")
    public AdDTO updateAd(Integer adId, CreateOrUpdateAdDTO updateAdDTO, Authentication authentication) {
        Ad ad = getAdById(adId);
        ad.setTitle(updateAdDTO.getTitle());
        ad.setDescription(updateAdDTO.getDescription());
        ad.setPrice(updateAdDTO.getPrice());

        ad = adRepository.save(ad);
        return adMapper.toAdDTO(ad);
    }

    /**
     * Возвращает список объявлений текущего авторизованного пользователя
     * @param authentication текущий авторизованный пользователь, предоставляет фронтенд
     * @return список объявлений в виде объекта AdsDTO
     */
    @Override
    public AdsDTO getAllAdsByMe(Authentication authentication) {
        String email = authentication.getName();
        List<Ad> adList = adRepository.getAllByAuthor_Email(email);
        return adMapper.toAdsDTO(adList);
    }

    /**
     * Возвращает расширенную информацию для объявления с указанным идентификатором
     * @param adId идентификатор объявления
     * @return Расширенную информацию для объявления в виде объекта ExtendedAdDTO
     */
    @Override
    public ExtendedAdDTO getExtendedAdInfo(Integer adId) {
        Ad ad = getAdById(adId);
        return adMapper.toExtendedAdDTO(ad);
    }

    /**
     * Проверяет, существует ли в БД объявление с указанным идентификатором
     * @param adId идентификатор объявления
     * @return возвращает true, если объявление с указанным идентификатором найдено
     */
    @Override
    public boolean isAdPresent(Integer adId) {
        return adRepository.existsById(adId);
    }

    /**
     * Создает новое объявление на основе переданных параметров и сохраняет его в БД
     * @param createAdDTO объект с полями нового объявления
     * @param file графический файл с картинкой (фото) объявления
     * @param authentication объект типа Authentication, текущий авторизованный пользователь, предоставляет фронтенд
     * @return Возвращает объект класса AdDTO в случае успешного создания нового объявления и сохранения его в БД
     * @throws IOException - если не удалось сохранить на диске файл с картиной объявления
     */
    @Override
    public AdDTO addAd(CreateOrUpdateAdDTO createAdDTO, MultipartFile file, Authentication authentication) throws IOException {
        String username = authentication.getName();
        User user = userRepository.findAuthUserByEmail(username);
        Ad ad = new Ad();
        ad.setTitle(createAdDTO.getTitle());
        ad.setDescription(createAdDTO.getDescription());
        ad.setAuthor(user);
        ad.setPrice(createAdDTO.getPrice());
        ad = adRepository.save(ad);

        String photoFileName = imageService.saveFileToDisk(file, adPhotoFilePath, "photo_" + ad.getAdId());
        ad.setImage(photoFileName);
        ad = adRepository.save(ad);

        return adMapper.toAdDTO(ad);
    }
}
