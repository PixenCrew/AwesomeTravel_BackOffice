package renewal.awesome_travel_backoffice.banner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import renewal.awesome_travel_backoffice.banner.repository.BannerRepository;
import renewal.common.entity.Banner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;

    // 모든 배너 조회 (관리자용)
    public Page<Banner> getAllBanners(Pageable pageable) {
        return bannerRepository.findAllBanners(pageable);
    }

    // 활성화된 배너만 조회 (현재 날짜 기준)
    public List<Banner> getActiveBanners() {
        return bannerRepository.findActiveBanners(LocalDate.now());
    }

    // 활성화된 배너만 조회 (페이징)
    public Page<Banner> getActiveBanners(Pageable pageable) {
        return bannerRepository.findActiveBanners(LocalDate.now(), pageable);
    }

    // ID로 배너 조회
    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }

    // 제목으로 검색
    public Page<Banner> searchBannersByTitle(String title, Pageable pageable) {
        return bannerRepository.findByTitleContaining(title, pageable);
    }

    // 활성 상태로 검색
    public Page<Banner> searchBannersByActive(Boolean active, Pageable pageable) {
        return bannerRepository.findByActive(active, pageable);
    }

    // 날짜 범위로 검색
    public Page<Banner> searchBannersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return bannerRepository.findByDateRange(startDate, endDate, pageable);
    }

    // 배너 생성
    @Transactional
    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    // 배너 수정
    @Transactional
    public Banner updateBanner(Long id, Banner updatedBanner) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다: " + id));
        
        banner.setTitle(updatedBanner.getTitle());
        banner.setDisplayOrder(updatedBanner.getDisplayOrder());
        banner.setStartDate(updatedBanner.getStartDate());
        banner.setEndDate(updatedBanner.getEndDate());
        banner.setActive(updatedBanner.getActive());
        banner.setFile(updatedBanner.getFile());
        banner.setUrl(updatedBanner.getUrl());
        
        return bannerRepository.save(banner);
    }

    // 배너 삭제
    @Transactional
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    // 배너 활성화/비활성화 토글
    @Transactional
    public Banner toggleBannerActive(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다: " + id));
        
        banner.setActive(!banner.getActive());
        return bannerRepository.save(banner);
    }

    // 배너 순서 변경
    @Transactional
    public Banner updateBannerOrder(Long id, Integer newOrder) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다: " + id));
        
        banner.setDisplayOrder(newOrder);
        return bannerRepository.save(banner);
    }
}

