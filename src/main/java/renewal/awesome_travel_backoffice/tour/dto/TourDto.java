package renewal.awesome_travel_backoffice.tour.dto;

import java.time.LocalDate;

import renewal.awesome_travel_backoffice.tour.entity.Tour;

public class TourDto {

    private Long id;
    private String name;
    private String company;
    private LocalDate date;

    // 기본 생성자
    public TourDto() {}

    // 생성자
    public TourDto(Long id, String name, String company, LocalDate date) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.date = date;
    }

    // Entity -> DTO 변환
    public TourDto toDto(Tour tour) {
        return new TourDto(
            tour.getId(),
            tour.getName(),
            tour.getCompany(),
            tour.getDate()
        );
    }

    // DTO -> Entity 변환
    public Tour toEntity() {
        return Tour.builder()
                .id(this.id)
                .name(this.name)
                .company(this.company)
                .date(this.date)
                .build();
    }
}
