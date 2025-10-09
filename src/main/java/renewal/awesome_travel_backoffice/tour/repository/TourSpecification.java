package renewal.awesome_travel_backoffice.tour.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import renewal.common.entity.Location;
import renewal.common.entity.Schedule;
import renewal.common.entity.Tour;
import renewal.common.entity.Product;

public class TourSpecification {

    public static Specification<Tour> companyIn(List<String> companies) {
        return (root, query, builder) -> {
            if (companies == null || companies.isEmpty()) {
                return null;
            }
            return root.get("company").in(companies);
        };
    }

    // Tour.name LIKE %name%
    public static Specification<Tour> nameContains(String name) {
        return (root, query, builder) -> builder.like(root.get("name"), "%" + name + "%");
    }

    // Tour.company LIKE %company%
    public static Specification<Tour> companyContains(String company) {
        return (root, query, builder) -> builder.like(root.get("company"), "%" + company + "%");
    }

    // Tour.startDate BETWEEN from AND to
    public static Specification<Tour> startDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("startDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("startDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(root.get("startDate"), to);
            } else {
                return null;
            }
        };
    }

    // Tour.endDate BETWEEN from AND to
    public static Specification<Tour> endDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("endDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("endDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(root.get("endDate"), to);
            } else {
                return null;
            }
        };
    }

    // Tour.price BETWEEN min AND max
    public static Specification<Tour> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, builder) -> {
            if (min != null && max != null) {
                return builder.between(root.get("price"), min, max);
            } else if (min != null) {
                return builder.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return builder.lessThanOrEqualTo(root.get("price"), max);
            } else {
                return null;
            }
        };
    }

    // 연관된 Course 리스트에서 location LIKE %location%
    public static Specification<Tour> scheduleLocationContains(String city) {
        return (root, query, builder) -> {
            Join<Tour, Schedule> scheduleJoin = root.join("schedules");
            Join<Schedule, Location> locationJoin = scheduleJoin.join("locations");
            return builder.like(locationJoin.get("city"), "%" + city + "%");
        };
    }

    // Tour.name LIKE %name%
    public static Specification<Tour> countryContains(String country) {
        return (root, query, builder) -> builder.like(root.get("country"), "%" + country + "%");
    }

    // Tour.count BETWEEN min AND max
    public static Specification<Tour> countBetween(Long min, Long max) {
        return (root, query, builder) -> {
            if (min != null && max != null) {
                return builder.between(root.get("count"), min, max);
            } else if (min != null) {
                return builder.greaterThanOrEqualTo(root.get("count"), min);
            } else if (max != null) {
                return builder.lessThanOrEqualTo(root.get("count"), max);
            } else {
                return null;
            }
        };
    }

    // Product와 연결되지 않은 Tour만 조회 (사용 중인 tour_id 제외)
    public static Specification<Tour> notConnectedToProduct(List<Long> usedTourIds) {
        return (root, query, builder) -> {
            if (usedTourIds == null || usedTourIds.isEmpty()) {
                return null; // 사용 중인 tour_id가 없으면 모든 Tour 조회
            }
            return builder.not(root.get("id").in(usedTourIds));
        };
    }

}
