package ru.skypro.homework.service.impl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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

import java.io.IOException;
import java.util.List;
@Service
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;

    public AdServiceImpl(AdRepository adRepository, UserRepository userRepository, AdMapper adMapper) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.adMapper = adMapper;
    }
    /**
     * Возвращает список всех объявлений из БД
     * @return список объявлений в виде объекта AdsDTO
     */
    @Override
    public AdsDTO getAllAds() {
        List<Ad> adList = adRepository.findAll();
        AdsDTO adsDTO = adMapper.toAdsDTO(adList);
        return adsDTO;
    }

    @Override
    public AdDTO addAd(CreateOrUpdateAdDTO createAdDTO, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        Ad ad = new Ad();
        ad.setTitle(createAdDTO.getTitle());
        ad.setDescription(createAdDTO.getDescription());
        ad.setAuthor(user);
        ad.setPrice(createAdDTO.getPrice());

        return adMapper.toAdDTO(ad);
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
    }

    @Override
    public AdDTO updateAd(Integer adId, CreateOrUpdateAdDTO updateAdDTO) {
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
        String username = authentication.getName();
        List<Ad> adList = adRepository.getAllByAuthor_Username(username);
        return adMapper.toAdsDTO(adList);
    }

    @Override
    public ExtendedAdDTO getExtendedAdInfo(Integer adId) {
        Ad ad = getAdById(adId);
        return adMapper.toExtendedAdDTO(ad);
    }


}
