package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.model.Ad;

public interface AdService {
    AdsDTO getAllAds();

    AdDTO addAd(CreateOrUpdateAdDTO createAdDTO, Authentication authentication);

    Ad getAdById(Integer adId);

    void removeAd(Integer adId);

    AdDTO updateAd(Integer adId, CreateOrUpdateAdDTO updateAdDTO);

    AdsDTO getAllAdsByMe(Authentication authentication);

    ExtendedAdDTO getExtendedAdInfo(Integer adId);
}
