package ru.skypro.homework.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.model.Ad;

import java.io.IOException;

public interface AdService {

    AdsDTO getAllAds();

    Ad getAdById(Integer adId);

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerAd(authentication, #adId)")
    void removeAd(Integer adId, Authentication authentication) throws IOException;

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerAd(authentication, #adId)")
    AdDTO updateAd(Integer adId, CreateOrUpdateAdDTO updateAdDTO, Authentication authentication);

    AdsDTO getAllAdsByMe(Authentication authentication);

    ExtendedAdDTO getExtendedAdInfo(Integer adId);

    boolean isAdPresent(Integer adId);

    AdDTO addAd(CreateOrUpdateAdDTO createAdDTO, MultipartFile file, Authentication authentication) throws IOException;
}
