package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AdsController {

    private final AdService adService;
    private final ImageService imageService;

    @GetMapping(" /ads")
    public ResponseEntity<AdsDTO> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    @PostMapping(value = "/ads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDTO> addAd(@RequestPart("properties") CreateOrUpdateAdDTO createAdDTO,
                                       @RequestPart("image") MultipartFile multipartFile,
                                       Authentication authentication) throws IOException {
        AdDTO adDTO = adService.addAd(createAdDTO, multipartFile, authentication);
        return ResponseEntity.ok(adDTO);
    }

    @GetMapping("/ads/{id}")
    public ResponseEntity<ExtendedAdDTO> extendedAd(@PathVariable(name = "id") int adId) {
        return ResponseEntity.ok(adService.getExtendedAdInfo(adId));
    }

    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable(name = "id") int adId, Authentication authentication) throws IOException {
        adService.removeAd(adId, authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/ads/{id}")
    public ResponseEntity<AdDTO> updateAds(@PathVariable(name = "id") int adId,
                                           @RequestBody CreateOrUpdateAdDTO updateAdDTO,
                                           Authentication authentication) {
        return ResponseEntity.ok(adService.updateAd(adId, updateAdDTO, authentication));
    }

    @GetMapping("/ads/me")
    public ResponseEntity<AdsDTO> getAdsMe(Authentication authentication) {
        return ResponseEntity.ok(adService.getAllAdsByMe(authentication));
    }

    @PatchMapping(value = "/ads/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateImage(@PathVariable(name = "id") int id,
                                            @RequestPart("image") MultipartFile multipartFile) {
        return ResponseEntity.ok().build();
    }
}
