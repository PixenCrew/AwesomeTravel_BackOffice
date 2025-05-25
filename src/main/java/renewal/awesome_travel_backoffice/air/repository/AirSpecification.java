package renewal.awesome_travel_backoffice.air.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import renewal.awesome_travel_backoffice.air.dto.AirFilterDTO;
import renewal.awesome_travel_backoffice.air.entity.Air;
import renewal.awesome_travel_backoffice.air.entity.SeatClass;
import renewal.awesome_travel_backoffice.air.utiles.AirStatus;
import renewal.awesome_travel_backoffice.air.utiles.FlightType;

public class AirSpecification {

    // code LIKE %code%
    public static Specification<Air> codeContains(String code) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(code)) return null;
            return builder.like(
                builder.lower(root.get("code")),
                "%" + code.toLowerCase() + "%"
            );
        };
    }

    // airline IN (list)
    public static Specification<Air> airlinesIn(List<String> airlines) {
        return (root, query, builder) -> {
            if (airlines == null || airlines.isEmpty()) return null;
            return root.get("airline").in(airlines);
        };
    }

    // departDate BETWEEN from AND to
    public static Specification<Air> departDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("departDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("departDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(root.get("departDate"), to);
            } else {
                return null;
            }
        };
    }

    // arriveDate BETWEEN from AND to
    public static Specification<Air> arriveDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("arriveDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("arriveDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(root.get("arriveDate"), to);
            } else {
                return null;
            }
        };
    }

    // depart == value
    public static Specification<Air> departEquals(String depart) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(depart)) return null;
            return builder.equal(root.get("depart"), depart);
        };
    }

    // arrive == value
    public static Specification<Air> arriveEquals(String arrive) {
        return (root, query, builder) -> {
            if (!StringUtils.hasText(arrive)) return null;
            return builder.equal(root.get("arrive"), arrive);
        };
    }

    // stopovers == value
    public static Specification<Air> stopoversEquals(Long stopovers) {
        return (root, query, builder) -> {
            if (stopovers == null) return null;
            return builder.equal(root.get("stopovers"), stopovers);
        };
    }

    // // seatCount BETWEEN min AND max
    // public static Specification<Air> seatCountBetween(Long min, Long max) {
    //     return (root, query, builder) -> {
    //         if (min != null && max != null) {
    //             return builder.between(root.get("seatCount"), min, max);
    //         } else if (min != null) {
    //             return builder.greaterThanOrEqualTo(root.get("seatCount"), min);
    //         } else if (max != null) {
    //             return builder.lessThanOrEqualTo(root.get("seatCount"), max);
    //         } else {
    //             return null;
    //         }
    //     };
    // }

    // flightType == value
    public static Specification<Air> flightTypeEquals(FlightType type) {
        return (root, query, builder) -> {
            if (type == null) return null;
            return builder.equal(root.get("flightType"), type);
        };
    }

    // status == value
    public static Specification<Air> statusEquals(AirStatus status) {
        return (root, query, builder) -> {
            if (status == null) return null;
            return builder.equal(root.get("status"), status);
        };
    }

    // seatClasses.price BETWEEN min AND max (JOIN & DISTINCT)
    public static Specification<Air> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, builder) -> {
            if (min == null && max == null) return null;

            Join<Air, SeatClass> join = root.join("seatClasses", JoinType.LEFT);
            if (min != null && max != null) {
                query.distinct(false);
                return builder.between(join.get("price"), min, max);
            } else if (min != null) {
                query.distinct(false);
                return builder.greaterThanOrEqualTo(join.get("price"), min);
            } else {
                query.distinct(false);
                return builder.lessThanOrEqualTo(join.get("price"), max);
            }
        };
    }

    // // DTO 전체를 한 번에 묶는 메서드
    // public static Specification<Air> byFilter(AirFilterDTO f) {
    //     return Specification.where(codeContains(f.getCode()))
    //         .and(airlinesIn(f.getArilines()))
    //         .and(departDateBetween(f.getDepartDateFrom(), f.getDepartDateTo()))
    //         .and(arriveDateBetween(f.getArriveDateFrom(), f.getArriveDateTo()))
    //         .and(departEquals(f.getDepart()))
    //         .and(arriveEquals(f.getArrive()))
    //         .and(stopoversEquals(f.getStopovers()))
    //         .and(seatCountBetween(f.getStartCount(), f.getEndCount()))
    //         .and(flightTypeEquals(f.getFlightType()))
    //         .and(statusEquals(f.getStatus()))
    //         .and(priceBetween(f.getMinPrice(), f.getMaxPrice()));
    // }
}
